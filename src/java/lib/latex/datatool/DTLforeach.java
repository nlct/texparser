/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
import java.util.HashMap;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.ifthen.IfThenSty;

public class DTLforeach extends ControlSequence
{
   public DTLforeach(DataToolSty sty)
   {
      this("DTLforeach", sty);
   }

   public DTLforeach(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new DTLforeach(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = parser.isNextChar('*', stack);

      TeXObject condition = parser.popOptional(stack);
      TeXObject dbArg = parser.popRequiredExpandFully(stack);
      CsvList csvList = CsvList.popCsvListFromStack(parser, stack, true);
      TeXObject body = parser.popRequired(stack);

      String dbName = dbArg.toString(parser);

      DataBase db = sty.getDataBase(dbName);

      if (db == null)
      {
         throw new LaTeXSyntaxException(parser,
           DataToolSty.ERROR_DB_DOESNT_EXIST, dbName);
      }

      HashMap<String,DataToolHeader> map = new HashMap<String,DataToolHeader>();

      for (TeXObject element : csvList)
      {
         if (!(element instanceof TeXObjectList))
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_SYNTAX, getName());
         }

         TeXObjectList elementList = (TeXObjectList)element;

         TeXObject object = elementList.popToken(
           PopStyle.IGNORE_LEADING_SPACE);

         if (!(object instanceof ControlSequence))
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_SYNTAX, getName());
         }

         String csName = ((ControlSequence)object).getName();

         object = elementList.popToken(
           PopStyle.IGNORE_LEADING_SPACE);

         if (!parser.isCharacter(object, '='))
         {
            throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_SYNTAX, getName());
         }

         TeXObjectList expanded = elementList.expandfully(parser);

         if (expanded != null)
         {
            element = expanded;
         }

         String field = element.toString(parser);
         DataToolHeader header = db.getHeaders().getHeader(field);

         if (header == null)
         {
            throw new LaTeXSyntaxException(parser, 
              DataToolSty.ERROR_HEADER_DOESNT_EXIST, field);
         }

         map.put(csName, header);
      }

      parser.startGroup();

      for (int i = 0; i < db.getRowCount(); i++)
      {
         int rowIdx = i+1;
         DataToolEntryRow row = db.getData().getRow(rowIdx);

         if (row == null)
         {
            throw new LaTeXSyntaxException(parser, 
              DataToolSty.ERROR_ROW_NOT_FOUND, rowIdx);
         }

         if (condition != null)
         {
            IfThenSty ifThenSty = sty.getIfThenSty();

            if (!ifThenSty.evaluate(stack, (TeXObject)condition.clone()))
            {
               continue;
            }
         }

         for (Iterator<String> it = map.keySet().iterator();
              it.hasNext(); )
         {
            String csName = it.next();
            DataToolHeader header = map.get(csName);

            if (header == null)
            {
               throw new NullPointerException("No map found for \\"+csName);
            }

            DataToolEntry entry = row.getEntry(header.getColumnIndex());

            parser.putControlSequence(true, new GenericCommand(csName, null, 
               entry.getContents()));
         }

         parser.putControlSequence(true, 
           new GenericCommand("DTLcurrentindex", null, new UserNumber(rowIdx)));

         if (i == 0)
         {
            parser.putControlSequence(true, new AtFirstOfTwo("DTLiffirstrow"));
         }
         else
         {
            parser.putControlSequence(true, new AtSecondOfTwo("DTLiffirstrow"));
         }

         if (i == db.getRowCount()-1)
         {
            parser.putControlSequence(true, new AtFirstOfTwo("DTLiflastrow"));
         }
         else
         {
            parser.putControlSequence(true, new AtSecondOfTwo("DTLiflastrow"));
         }

         if (rowIdx%2 == 0)
         {
            parser.putControlSequence(true, new AtSecondOfTwo("DTLifoddrow"));
         }
         else
         {
            parser.putControlSequence(true, new AtFirstOfTwo("DTLifoddrow"));
         }

         // TODO add \dtlbreak etc

         TeXObject object = (TeXObject)body.clone();

         if (parser == stack)
         {
            object.process(parser);
         }
         else
         {
            object.process(parser, stack);
         }
      }

      parser.endGroup();
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
