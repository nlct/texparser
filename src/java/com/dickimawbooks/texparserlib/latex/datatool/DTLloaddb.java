/*
    Copyright (C) 2013 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLloaddb extends ControlSequence
{
   public DTLloaddb(DataToolSty sty)
   {
      this("DTLloaddb", false, sty);
   }

   public DTLloaddb(String name, boolean mapChars, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
      this.mapChars = mapChars;
   }

   public Object clone()
   {
      return new DTLloaddb(getName(), mapChars, sty);
   }

   private void mapEntry(TeXObjectList entry)
   {// TODO
   }

   // This is quite primitive, mimicking the primitive file parsing
   // method used by datatool.sty
   protected void readData(TeXParser parser, TeXObject options,
     TeXObject dbArg, TeXObject csvArg)
     throws IOException
   {
      String dbLabel = dbArg.toString(parser);

      DataBase db = sty.createDataBase(dbLabel, true);

      TeXPath texPath = new TeXPath(parser, csvArg.toString(parser));

      File file = texPath.getFile();

      TeXParserListener listener = parser.getListener();

      if (!listener.getTeXApp().isReadAccessAllowed(file))
      {
         listener.getTeXApp().warning(parser,
            listener.getTeXApp().getMessage(TeXApp.MESSAGE_NO_READ, file));

         return;
      }

      boolean noheader = false;
      boolean autokeys = false;
      Vector<String> keys = null;
      CsvList headers = null;// TODO
      int omitlines = 0;

      if (options != null)
      {
         KeyValList keyValList = KeyValList.getList(parser, options);

         TeXObject val = keyValList.getValue("noheader");

         if (val != null)
         {
            if (!val.toString(parser).trim().equals("false"))
            {
               noheader = true;
            }
         }

         val = keyValList.getValue("autokeys");

         if (val != null)
         {
            if (!val.toString(parser).trim().equals("false"))
            {
               autokeys = true;
            }
         }

         val = keyValList.getValue("omitlines");

         if (val != null)
         {
            if (val instanceof Numerical)
            {
               omitlines = ((Numerical)val).number(parser);
            }
            else
            {
               String string = val.toString(parser).trim();

               try
               {
                  omitlines = Integer.parseInt(string);
               }
               catch (NumberFormatException e)
               {
                  throw new TeXSyntaxException(e, parser,
                   TeXSyntaxException.ERROR_NUMBER_EXPECTED, string);
               }
            }
         }

         if (!autokeys)
         {
            val = keyValList.getValue("keys");

            if (val != null)
            {
               CsvList csvList = CsvList.getList(parser, val);
               keys = new Vector<String>();

               for (int i = 0; i < csvList.size(); i++)
               {
                  val = csvList.getValue(i);

                  if (val instanceof Expandable)
                  {
                     TeXObjectList expanded 
                        = ((Expandable)val).expandfully(parser);

                     if (expanded != null)
                     {
                        val = expanded;
                     }
                  }

                  keys.add(val.toString(parser));
               }
            }
         }
      }

      String defaultKey = "Column";

      TeXObject cs = parser.getControlSequence("dtldefaultkey");

      if (cs != null)
      {
         if (cs instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               cs = expanded;
            }
         }

         defaultKey = cs.toString(parser);
      }

      TeXReader reader = null;

      try
      {
         reader = new TeXReader(null, file, 
           parser.getListener().getCharSet());

         boolean headerSet = noheader;

         TeXObjectList lineList;

         int delim = sty.getDelimiter();
         int sep = sty.getSeparator();
         int rowIdx = 0;
         int lineNum = 1;

         while ((lineList = parser.readLine(reader, false)) != null)
         {
            lineNum++;

            if (lineNum < omitlines)
            {
               continue;
            }

            rowIdx++;

            boolean delimFound = false;
            int colIdx = 1;
            TeXObjectList entry = new TeXObjectList();

            if (headerSet)
            {
               sty.addNewRow(dbLabel);
            }

            for (TeXObject obj : lineList)
            {
               if (obj instanceof CharObject)
               {
                  int cp = ((CharObject)obj).getCharCode();

                  if (cp == delim)
                  {
                     delimFound = !delimFound;
                  }
                  else if (cp == sep && !delimFound)
                  {
                     entry.popLeadingWhiteSpace();

                     if (headerSet)
                     {
                        DataToolHeader header = db.getHeader(colIdx);
                        String thisKey;

                        if (header == null)
                        {
                           if (autokeys || keys == null || keys.size() < colIdx)
                           {
                              thisKey = defaultKey+colIdx;
                           }
                           else
                           {
                              thisKey = keys.get(colIdx-1);
                           }
                        }
                        else
                        {
                           thisKey = header.getColumnLabel();
                        }

                        if (mapChars)
                        {
                           mapEntry(entry);
                        }

                        sty.addNewEntry(dbLabel, thisKey, entry);
                     }
                     else
                     {
                        if (keys == null)
                        {
                           keys = new Vector<String>();
                        }

                        if (keys.size() < colIdx)
                        {
                           keys.add(entry.toString(parser));
                        }
                        else if (keys.get(colIdx-1).isEmpty())
                        {
                           keys.set(colIdx-1, entry.toString(parser));
                        }
                     }

                     colIdx++;
                     entry = new TeXObjectList();
                  }
                  else
                  {
                     entry.add(obj);
                  }
               }
               else
               {
                  entry.add(obj);
               }
            }

            entry.popLeadingWhiteSpace();

            if (headerSet)
            {
               DataToolHeader header = db.getHeader(colIdx);
               String thisKey;

               if (header == null)
               {
                  if (autokeys || keys == null || keys.size() < colIdx)
                  {
                     thisKey = defaultKey+colIdx;
                  }
                  else
                  {
                     thisKey = keys.get(colIdx-1);
                  }
               }
               else
               {
                  thisKey = header.getColumnLabel();
               }

               if (mapChars)
               {
                  mapEntry(entry);
               }

               sty.addNewEntry(dbLabel, thisKey, entry);
            }
            else
            {
               if (keys == null)
               {
                  keys = new Vector<String>();
               }

               if (keys.size() < colIdx)
               {
                  keys.add(entry.toString(parser));
               }
               else if (keys.get(colIdx-1).isEmpty())
               {
                  keys.set(colIdx-1, entry.toString(parser));
               }
            }

            headerSet = true;
         }

         listener.getTeXApp().message(listener.getTeXApp().getMessage(
          DataToolSty.MESSAGE_LOADDB, dbLabel, file, db.getColumnCount(),
           db.getRowCount()));
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject options = stack.popArg(parser, '[', ']');

      TeXObject dbArg = stack.popArg(parser);

      if (dbArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)dbArg).expandfully(parser, 
            stack);

         if (expanded != null)
         {
            dbArg = expanded;
         }
      }

      TeXObject csvArg = stack.popArg(parser);

      if (csvArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)csvArg).expandfully(parser, 
            stack);

         if (expanded != null)
         {
            csvArg = expanded;
         }
      }

      readData(parser, options, dbArg, csvArg);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject options = parser.popNextArg('[', ']');

      TeXObject dbArg = parser.popNextArg();

      if (dbArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)dbArg).expandfully(parser);

         if (expanded != null)
         {
            dbArg = expanded;
         }
      }

      TeXObject csvArg = parser.popNextArg();

      if (csvArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)csvArg).expandfully(parser);

         if (expanded != null)
         {
            csvArg = expanded;
         }
      }

      readData(parser, options, dbArg, csvArg);
   }

   protected DataToolSty sty;
   protected boolean mapChars;
}
