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

import com.dickimawbooks.texparserlib.*;

/**
 * FileMapHandler used to read CSV files by DataBase.read().
 * This is designed to approximate the way that <code>\DTLread</code>
 * parses a CSV file, although there may be some differences.
 */
public class CsvReadHandler implements FileMapHandler
{
   public CsvReadHandler(DataBase database, IOSettings settings)
   {
      this.database = database;
      this.settings = settings;
   }

   @Override
   public void processLine(TeXParser parser, TeXObjectList line, int lineNumber)
   throws IOException
   {
System.out.println("PROCESSING LINE "+lineNumber+" "+line.toString(parser));
      if (rowIdx == 0)
      {
         if (settings.getSkipLines() < lineNumber)
         {
            TeXObjectList row = splitRow(parser, line);

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
                  parseHeader(row, parser, line);
                  rowIdx = 1;
               }
               else
               {
                  parseRow(row, parser, line);
                  rowIdx++;
               }
            }
         }
      }
      else
      {
         TeXObjectList row = splitRow(parser, line);

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

            parseRow(row, parser, line);
            rowIdx++;
         }
      }
   }

   protected void parseHeader(TeXObjectList row, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
// TODO
      for (int i = 0; i < row.size(); i++)
      {
         TeXObject cell = processCell(row.get(i), parser, stack);
System.out.println("Header Row element "+i+": "+cell.toString(parser));
      }
   }

   protected void parseRow(TeXObjectList row, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
// TODO
      for (int i = 0; i < row.size(); i++)
      {
         TeXObject cell = processCell(row.get(i), parser, stack);
System.out.println("Row element "+i+": "+cell.toString(parser));
      }
   }

   protected TeXObject processCell(TeXObject obj, 
     TeXParser parser, TeXObjectList stack)
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
               processLiteralToken(parser, list.get(i), builder);
            }
         }
         else
         {
            processLiteralToken(parser, obj, builder);
         }

         list = parser.getListener().createStack();

         parser.scan(builder.toString(), list);

// TODO apply user mappings

         obj = list;
      }

      switch (settings.getExpandOption())
      {
         case PROTECTED:
           obj = TeXParserUtils.expandOnce(obj, parser, stack);
         break;
         case FULL:
           obj = TeXParserUtils.expandFully(obj, parser, stack);
         break;
      }

      if (settings.isTrimElementOn() && parser.isStack(obj))
      {
         ((TeXObjectList)obj).trim();
      }

      return obj;
   }

   protected void processLiteralToken(TeXParser parser,
       TeXObject obj, StringBuilder builder)
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

   protected TeXObjectList splitRow(TeXParser parser, TeXObjectList line)
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
                           && ((CharObject)nextObj).getCharCode() == separator
                          )
                  {
                     // end of cell
                     pendingRow.add(pendingCell);
                     pendingCell = null;
                     needsClosingDelim = false;
                     line.popStack(parser, TeXObjectList.POP_IGNORE_LEADING_SPACE);
                  }
                  else
                  {
                     pendingCell.add(obj);
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

            pendingCell.add(fromControlSequence(parser, (ControlSequence)obj));
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

   protected TeXObject fromControlSequence(TeXParser parser, ControlSequence cs)
   {
      EscapeCharsOption opt = settings.getEscapeCharsOption();

      if (opt == EscapeCharsOption.NONE)
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
            case DELIM:

               if (cp == delimiter && csname.length() 
                    == Character.charCount(cp))
               {
                  return parser.getListener().getOther(cp);
               }

            break;

            case DELIM_BKSL:

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
// TODO
   }

   DataBase database;
   IOSettings settings;
   int rowIdx = 0;
   TeXObjectList pendingRow = null;
   TeXObjectList pendingCell = null;
}
