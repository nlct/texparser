/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
import java.io.EOFException;

import java.util.Vector;
import java.util.Hashtable;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.latex3.PropertyCommand;

/**
 * FileMapHandler used to read CSV files by DataBase.read().
 * This is designed to approximate the way that <code>\DTLread</code>
 * parses a CSV file, although there may be some differences.
 */
public class CsvReadHandler implements FileMapHandler
{
   public CsvReadHandler(DataBase database, boolean appending, IOSettings settings)
   {
      this.database = database;
      this.appending = appending;
      this.settings = settings;

      headers = new Vector<DataToolHeader>();

      parser = settings.getSty().getParser();

      ControlSequence cs = parser.getControlSequence("l__datatool_csv_headers_prop");

      if (cs instanceof PropertyCommand)
      {
         @SuppressWarnings("unchecked")
         PropertyCommand<Integer> prop = (PropertyCommand<Integer>)cs;

         colHeadersProp = prop;
      }

      cs = parser.getControlSequence("l__datatool_csv_keys_prop");

      if (cs instanceof PropertyCommand)
      {
         @SuppressWarnings("unchecked")
         PropertyCommand<Integer> prop = (PropertyCommand<Integer>)cs;

         colKeysProp = prop;
      }

      headers = new Vector<DataToolHeader>();
   }

   @Override
   public void processLine(TeXParser parser, TeXObjectList line, int lineNumber)
   throws IOException
   {
      this.parser = parser;
      currentStack = line;

      if (rowIdx == 0)
      {
         if (settings.getSkipLines() < lineNumber)
         {
            TeXObjectList row = splitRow(line);

            if (row != null)
            {
               if (row.isEmpty())
               {
                  switch (settings.getCsvBlankOption())
                  {
                     case IGNORE: return;
                     case END:
                        throw new EOFException();
                  }
               }

               if (settings.isHeaderIncluded())
               {
                  parseHeader(row);
                  rowIdx = 1;
               }
               else
               {
                  parseRow(row);
                  rowIdx++;
               }
            }
         }
      }
      else
      {
         TeXObjectList row = splitRow(line);

         if (row != null)
         {
            if (row.isEmpty())
            {
               switch (settings.getCsvBlankOption())
               {
                  case IGNORE: return;
                  case END:
                     throw new EOFException();
               }
            }

            parseRow(row);
            rowIdx++;
         }
      }
   }

   protected void parseHeader(TeXObjectList row)
   throws IOException
   {
      boolean autokeys = settings.isAutoKeysOn();

      DataToolHeaderRow headerRow = database.getHeaders();

      if (headerRow == null)
      {
         headerRow = new DataToolHeaderRow(settings.getSty());
         database.update(headerRow, database.getData());
      }

      for (int i = 0; i < row.size(); i++)
      {
         Integer colIdx = Integer.valueOf(i+1);
         String colKey;
         TeXObject cell;

         if (autokeys)
         {
            colKey = parser.expandToString(
               parser.getListener().getControlSequence("dtldefaultkey"),
                 currentStack) + colIdx;
            cell = parser.getListener().createString(colKey);
         }
         else
         {
            TeXObject key = (colKeysProp == null ? null : colKeysProp.get(colIdx));

            if (key == null)
            {
               cell = processCell(row.get(i));
            }
            else
            {
               cell = key;
            }

            colKey = cell.toString(parser);

            if (colKey.isEmpty())
            {
               colKey = parser.expandToString(
                 parser.getListener().getControlSequence("dtldefaultkey"),
                 currentStack) + colIdx;
            }
         }

         TeXObject title = (colHeadersProp == null ? null : colHeadersProp.get(colIdx));

         if (title == null)
         {
            title = cell;
         }

         DataToolHeader header;

         if (appending)
         {
            header = database.getHeader(colKey);

            if (header == null)
            {
               header = new DataToolHeader(settings.getSty(),
                 headerRow.size()+1, colKey);
               header.setTitle(title);
               headerRow.add(header);
            }
         }
         else
         {
            header = new DataToolHeader(settings.getSty(), colIdx, colKey);
            header.setTitle(title);
            headerRow.add(header);
         }

         headers.add(header);
      }
   }

   protected void parseRow(TeXObjectList row)
   throws IOException
   {
      DataToolHeaderRow headerRow = database.getHeaders();

      if (headerRow == null)
      {
         headerRow = new DataToolHeaderRow(settings.getSty());
      }

      DataToolRows data = database.getData();

      if (data == null)
      {
         data = new DataToolRows(settings.getSty());
      }

      DataToolEntryRow entryRow = new DataToolEntryRow(
        data.size()+1, settings.getSty());

      data.add(entryRow);

      database.update(headerRow, data);

      for (int i = 0; i < row.size(); i++)
      {
         TeXObject cell = processCell(row.get(i));

         DataElement element = settings.getSty().getElement(cell);
         boolean update = true;

         if (element == null)
         {
            update = false;
            element = new DataStringElement();
         }

         DataToolHeader header = null;

         if (headers.size() > i)
         {
            header = headers.get(i);
         }
         else
         {
            Integer colIdx = Integer.valueOf(i+1);

            TeXObject key = (colKeysProp == null ? null : colKeysProp.get(colIdx));

            String colKey;

            if (key == null)
            {
               colKey = parser.expandToString(
                 parser.getListener().getControlSequence("dtldefaultkey"),
                    currentStack) + colIdx;
            }
            else
            {
               colKey = parser.expandToString(key, currentStack);
            }

            header = new DataToolHeader(settings.getSty(), headerRow.size()+1, colKey);
            headerRow.add(header);
            headers.add(header);
         }

         if (update)
         {
            header.updateType(element);
         }

         DataToolEntry entry = new DataToolEntry(settings.getSty(),
          header.getColumnIndex(), element);
         entryRow.add(entry);
      }
   }

   protected TeXObject processCell(TeXObject obj)
   throws IOException
   {
      if (settings.isCsvLiteral())
      {
         StringBuilder builder = new StringBuilder();

         TeXObjectList list;

         if (parser.isStack(obj))
         {
            list = (TeXObjectList)obj;

            for (int i = 0; i < list.size(); i++)
            {
               processLiteralToken(list.get(i), builder);
            }
         }
         else
         {
            processLiteralToken(obj, builder);
         }

         list = parser.getListener().createStack();

         parser.scan(builder.toString(), list);

// TODO apply user mappings

         obj = list;
      }

      switch (settings.getExpandOption())
      {
         case PROTECTED:
           obj = TeXParserUtils.expandOnce(obj, parser, currentStack);
         break;
         case FULL:
           obj = TeXParserUtils.expandFully(obj, parser, currentStack);
         break;
      }

      if (settings.isTrimElementOn() && parser.isStack(obj))
      {
         ((TeXObjectList)obj).trim();
      }

      return obj;
   }

   protected void processLiteralToken(TeXObject obj, StringBuilder builder)
   {
      if (obj instanceof ControlSequence)
      {
         String csname = ((ControlSequence)obj).getName();

         if (csname.equals("f") || csname.equals("n") || csname.equals("r"))
         {
            builder.append(' ');
         }
         else if (csname.equals("t"))
         {
            builder.append('\t');
         }
         else
         {
            builder.append(obj.toString(parser));
         }
      }
      else if (obj instanceof CharObject)
      {
         int cp = ((CharObject)obj).getCharCode();

         if (cp == '#' || cp == '$' || cp == '%' || cp == '&'
             || cp == '_' || cp == '{' || cp == '}')
         {
            builder.append('\\');
            builder.appendCodePoint(cp);
         }
         else if (cp == '\\')
         {
            builder.append("\\textbackslash ");
         }
         else if (cp == '^')
         {
            builder.append("\\textasciicircum ");
         }
         else if (cp == '~')
         {
            builder.append("\\textasciitilde ");
         }
         else
         {
            builder.appendCodePoint(cp);
         }
      }
      else
      {
         builder.append(obj.toString(parser));
      }
   }

   protected TeXObjectList splitRow(TeXObjectList line)
   throws IOException
   {
      int delimiter = settings.getDelimiter();
      int separator = settings.getSeparator();
      boolean needsClosingDelim = false;

      if (pendingCell != null)
      {
         // continue parsing from previous line

         needsClosingDelim = true;
      }

      if (pendingRow == null)
      {
         pendingRow = new TeXObjectList();
      }

      while (!line.isEmpty())
      {
         TeXObject obj = line.pop();

         if (obj instanceof CharObject)
         {
            int cp = ((CharObject)obj).getCharCode();

            if (needsClosingDelim)
            {
               if (cp == delimiter)
               {
                  TeXObject nextObj
                     = line.peekStack(TeXObjectList.POP_IGNORE_LEADING_SPACE);

                  if (nextObj == null)
                  {
                     // end of row
                     pendingRow.add(pendingCell);
                     TeXObjectList row = pendingRow;

                     pendingCell = null;
                     pendingRow = null;
                     needsClosingDelim = false;

                     return row;
                  }
                  else if ((nextObj instanceof CharObject)
                            && (((CharObject)nextObj).getCharCode()) == separator)
                  {
                     // Closing delimiter followed by separator.
                     // End of cell
                     pendingRow.add(pendingCell);
                     pendingCell = null;
                     needsClosingDelim = false;
                     line.popStack(parser, TeXObjectList.POP_IGNORE_LEADING_SPACE);
                  }
                  else
                  {
                     nextObj = line.peekStack();

                     if (nextObj instanceof CharObject
                          && ((CharObject)nextObj).getCharCode() == delimiter
                          && (settings.getEscapeCharsOption()
                                  == EscapeCharsOption.DOUBLE_DELIM))
                     {
                        // double delimiter

                        pendingCell.add(obj);
                        line.popStack(parser);
                     }
                     else
                     {
                        /*
                         Delimiter not followed by separator or
                         eol or (with csv-escape-chars=double-delim)
                         another delimiter.
                         Assume literal?
                        */ 

                        pendingCell.add(obj);
                     }
                  }
               }
               else
               {
                  pendingCell.add(obj);
               }
            }
            else if (cp == delimiter)
            {
               if (pendingCell == null)
               {
                  pendingCell = new TeXObjectList();
                  needsClosingDelim = true;
               }
               else if (pendingCell.isBlank())
               {
                  pendingCell.clear();
                  needsClosingDelim = true;
               }
               else
               {
                  pendingCell.add(obj);
               }
            }
            else if (cp == separator)
            {
               // new cell

               if (pendingCell != null)
               {
                  if (!settings.isCsvLiteral())
                  {
                     pendingCell = TeXParserUtils.removeGroup(pendingCell);
                  }

                  if (settings.isTrimElementOn())
                  {
                     pendingCell.trimTrailing();
                  }

                  pendingRow.add(pendingCell);
                  pendingCell = null;
               }
               else
               {
                  pendingRow.add(new TeXObjectList());
               }
            }
            else if (parser.isCatCode(TeXParser.TYPE_SPACE, cp))
            {
               Space space = parser.getListener().getSpace();
               space.setSpace(cp);

               if (pendingCell == null)
               {
                  if (!settings.isTrimElementOn())
                  {
                     pendingCell = new TeXObjectList();
                     pendingCell.add(space);
                  }
               }
               else
               {
                  pendingCell.add(space);
               }
            }
            else
            {
               if (pendingCell == null)
               {
                  pendingCell = new TeXObjectList();
               }

               pendingCell.add(obj);
            }
         }
         else if (obj instanceof WhiteSpace || obj instanceof Ignoreable)
         {
            if (pendingCell == null)
            {
               if (!settings.isTrimElementOn())
               {
                  pendingCell = new TeXObjectList();
                  pendingCell.add(obj);
               }
            }
            else
            {
               pendingCell.add(obj);
            }
         }
         else if (obj instanceof ControlSequence)
         {
            if (pendingCell == null)
            {
               pendingCell = new TeXObjectList();
            }

            if (settings.getEscapeCharsOption() == EscapeCharsOption.DOUBLE_DELIM)
            {
               String csname = ((ControlSequence)obj).getName();
               int cp = csname.codePointAt(0);

               pendingCell.add(obj);

               if (cp == delimiter
                   && csname.length() == Character.charCount(cp))
               {
                  TeXObject nextObj = line.peekStack();

                  if ((nextObj instanceof CharObject)
                   && ((CharObject)nextObj).getCharCode() == delimiter
                     )
                  {
                     line.popStack(parser);
                  }
               }
            }
            else
            {
               pendingCell.add(fromControlSequence((ControlSequence)obj));
            }
         }
         else
         {
            if (pendingCell == null)
            {
               pendingCell = new TeXObjectList();
            }

            pendingCell.add(obj);
         }
      }

      if (needsClosingDelim)
      {
         return null;
      }
      else
      {
         if (pendingCell != null)
         {
            if (!settings.isCsvLiteral())
            {
               pendingCell = TeXParserUtils.removeGroup(pendingCell);
            }

            if (settings.isTrimElementOn())
            {
               pendingCell.trimTrailing();
            }

            if (pendingRow == null)
            {
               pendingRow = new TeXObjectList();
            }

            pendingRow.add(pendingCell);
            pendingCell = null;
         }

         if (pendingRow == null)
         {
            return new TeXObjectList();
         }
         else
         {
            TeXObjectList row = pendingRow;
            pendingRow = null;

            return row;
         }
      }
   }

   protected TeXObject fromControlSequence(ControlSequence cs)
   {
      EscapeCharsOption opt = settings.getEscapeCharsOption();

      if (opt == EscapeCharsOption.NONE
       || opt == EscapeCharsOption.DOUBLE_DELIM)
      {
         return cs;
      }
      else
      {
         String csname = cs.getName();
         int cp = csname.codePointAt(0);
         int delimiter = settings.getDelimiter();

         switch (opt)
         {
            case ESC_DELIM:

               if (cp == delimiter && csname.length() 
                    == Character.charCount(cp))
               {
                  return parser.getListener().getOther(cp);
               }

            break;

            case ESC_DELIM_BKSL:

               if ((cp == delimiter || parser.isCatCode(TeXParser.TYPE_ESC, cp))
                   && csname.length() == Character.charCount(cp)
                  )
               {
                  return parser.getListener().getOther(cp);
               }

            break;
         }

         return cs;
      }
   }

   @Override
   public void processCompleted(TeXParser parser)
     throws IOException
   {
   }

   DataBase database;
   IOSettings settings;
   boolean appending = false;
   int rowIdx = 0;
   TeXObjectList pendingRow = null;
   TeXObjectList pendingCell = null;
   TeXObjectList currentStack = null;
   PropertyCommand<Integer> colKeysProp;
   PropertyCommand<Integer> colHeadersProp;
   Vector<DataToolHeader> headers;
   TeXParser parser;
}
