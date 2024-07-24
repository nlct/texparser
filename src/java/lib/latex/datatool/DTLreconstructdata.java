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
import com.dickimawbooks.texparserlib.latex.*;

public class DTLreconstructdata extends ControlSequence
{
   public DTLreconstructdata(DataToolSty sty)
   {
      this("DTLreconstructdata", sty);
   }

   public DTLreconstructdata(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLreconstructdata(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXApp texApp = listener.getTeXApp();

      texApp.warning(parser,
       texApp.getMessage("datatool.warn.dbtex3exp"));

      parser.putControlSequence(true,
        new TextualContentCommand(
        DataToolSty.CURRENT_FILE_TYPE, "dbtex"));

      parser.setCatCode(true, '@', TeXParser.TYPE_LETTER);

      String name = parser.expandToString(
        listener.getControlSequence(DataToolSty.LAST_LOADED_NAME), stack);

      TeXObjectList headerArg = TeXParserUtils.toList(popArg(parser, stack), parser);
      TeXObjectList contentArg = TeXParserUtils.toList(popArg(parser, stack), parser);
      int numRows = popInt(parser, stack);
      int numCols = popInt(parser, stack);
      TeXObjectList mapArg = TeXParserUtils.toList(popArg(parser, stack), parser);

      DataBase db = sty.getDataBase(name);

      String colRegName = sty.getColumnCountRegisterName(name);
      String rowRegName = sty.getRowCountRegisterName(name);
      String contentsRegName = sty.getContentsRegisterName(name);
      String headerRegName = sty.getHeaderRegisterName(name);

      TeXSettings settings = parser.getSettings();
      NumericRegister reg = settings.getNumericRegister(colRegName);

      if (reg == null)
      {
         settings.newcount(false, colRegName, numCols);
      }
      else
      {
         settings.globalSetRegister(colRegName, numCols);
      }

      reg = settings.getNumericRegister(rowRegName);

      if (reg == null)
      {
         settings.newcount(false, rowRegName, numRows);
      }
      else
      {
         settings.globalSetRegister(rowRegName, numRows);
      }

      TokenRegister contentsReg, headerReg;

      ControlSequence cs = parser.getControlSequence(contentsRegName);

      if (cs == null || !(cs instanceof TokenRegister))
      {
         contentsReg = new TokenRegister(contentsRegName);
      }
      else
      {
         contentsReg = (TokenRegister)cs;
      }

      cs = parser.getControlSequence(headerRegName);

      if (cs == null || !(cs instanceof TokenRegister))
      {
         headerReg = new TokenRegister(headerRegName);
      }
      else
      {
         headerReg = (TokenRegister)cs;
      }

      int maxProgress = numCols+numRows;
      int progress = 0;
      texApp.progress(progress);

      DataToolHeaderRow headers = new DataToolHeaderRow(sty, numCols);
      TeXObjectList headerTokenList = listener.createStack();

      while (!headerArg.isEmpty())
      {
         ControlSequence markerCs;

         try
         {
            markerCs = TeXParserUtils.popControlSequence(parser, headerArg);

            if (!markerCs.getName().equals("dtl@db@header@reconstruct"))
            {
               throw new TeXSyntaxException(parser,
                TeXSyntaxException.ERROR_EXPECTED_BUT_FOUND, 
                 "\\dtl@db@header@reconstruct", markerCs);
            }
         }
         catch (EOFException e)
         {
            break;
         }

         int colIdx = TeXParserUtils.popInt(parser, headerArg);
         String label = TeXParserUtils.popLabelString(parser, headerArg);
         int type = TeXParserUtils.popInt(parser, headerArg);
         TeXObject title = TeXParserUtils.popArg(parser, headerArg);

         DataToolHeader header = new DataToolHeader(sty, colIdx, label, 
           (byte)type, title);

         headers.add(header);

         headerTokenList.add(new TeXCsRef("db@plist@elt@w"));
         headerTokenList.add(new TeXCsRef("db@col@id@w"));
         headerTokenList.add(new UserNumber(colIdx));
         headerTokenList.add(new TeXCsRef("db@col@id@end@"));

         headerTokenList.add(new TeXCsRef("db@key@id@w"));
         headerTokenList.add(listener.createString(label));
         headerTokenList.add(new TeXCsRef("db@key@id@end@"));

         headerTokenList.add(new TeXCsRef("db@type@id@w"));
         headerTokenList.add(new UserNumber(type));
         headerTokenList.add(new TeXCsRef("db@type@id@end@"));

         headerTokenList.add(new TeXCsRef("db@header@id@w"));
         headerTokenList.add(title);
         headerTokenList.add(new TeXCsRef("db@header@id@end@"));

         headerTokenList.add(new TeXCsRef("db@col@id@w"));
         headerTokenList.add(new UserNumber(colIdx));
         headerTokenList.add(new TeXCsRef("db@col@id@end@"));
         headerTokenList.add(new TeXCsRef("db@plist@elt@end@"));

         texApp.progress((100*(++progress))/maxProgress);
      }

      DataToolRows rows = new DataToolRows(sty, numRows);

      TeXObjectList contentTokenList = listener.createStack();

      while (!contentArg.isEmpty())
      {
         TeXObject obj = contentArg.popStack(parser, 
           TeXObjectList.POP_IGNORE_LEADING_SPACE);

         if (obj == null)
         {
            break;
         }

         if (!TeXParserUtils.isControlSequence(obj, "dtl@db@row@reconstruct"))
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_EXPECTED_BUT_FOUND, 
              "\\dtl@db@row@reconstruct", obj);
         }

         int rowIdx = popInt(parser, contentArg);

         contentTokenList.add(new TeXCsRef("db@row@elt@w"));
         contentTokenList.add(new TeXCsRef("db@row@id@w"));
         contentTokenList.add(new UserNumber(rowIdx));
         contentTokenList.add(new TeXCsRef("db@row@id@end@"));

         DataToolEntryRow row = new DataToolEntryRow(rowIdx, sty, numCols);
         rows.add(row);

         TeXObjectList rowContent
            = TeXParserUtils.toList(popArg(parser, contentArg), parser);

         while (!rowContent.isEmpty())
         {
            obj = rowContent.popStack(parser, 
              TeXObjectList.POP_IGNORE_LEADING_SPACE);

            if (obj == null)
            {
               break;
            }

            if (!TeXParserUtils.isControlSequence(obj, "dtl@db@col@reconstruct"))
            {
               throw new TeXSyntaxException(parser,
                TeXSyntaxException.ERROR_EXPECTED_BUT_FOUND, 
                 "\\dtl@db@col@reconstruct", obj);
            }

            int colIdx = popInt(parser, rowContent);

            TeXObjectList cellContent
              = TeXParserUtils.toList(popArg(parser, rowContent), parser);

            obj = cellContent.peekStack();
            TeXObject value = cellContent;

            if (obj instanceof ControlSequence)
            {
               String csname = ((ControlSequence)obj).getName();

               if (csname.equals("dtl@db@value@reconstruct"))
               {
                  cellContent.popStack(parser);
                  value = TeXParserUtils.popArg(parser, cellContent);
               }
               else if (csname.equals("dtl@db@datum@reconstruct"))
               {
                  cellContent.popStack(parser);

                  obj = TeXParserUtils.popArg(parser, cellContent);
                  TeXObject numArg = TeXParserUtils.popArg(parser, cellContent);
                  TeXObject symArg = TeXParserUtils.popArg(parser, cellContent);
                  int type = TeXParserUtils.popInt(parser, cellContent);

                  switch (type)
                  {
                     case DataToolHeader.TYPE_INT:
                       value = new DatumElement(obj, 
                         new UserNumber(
                           TeXParserUtils.toInt(numArg, parser, cellContent)),
                         null, DatumType.INTEGER
                       );
                     break;
                     case DataToolHeader.TYPE_REAL:
                       value = new DatumElement(obj, 
                         new TeXFloatingPoint(
                           TeXParserUtils.toDouble(numArg, parser, cellContent)),
                         null, DatumType.DECIMAL
                       );
                     break;
                     case DataToolHeader.TYPE_CURRENCY:
                       value = new DatumElement(obj, 
                         new TeXFloatingPoint(
                           TeXParserUtils.toDouble(numArg, parser, cellContent)),
                         symArg, DatumType.CURRENCY
                       );
                     break;
                     default:
                       value = new DatumElement(obj);
                  }
               }
            }

            DataToolEntry entry = new DataToolEntry(sty, colIdx, value);

            row.add(entry);

            contentTokenList.add(new TeXCsRef("db@col@id@w"));
            contentTokenList.add(new UserNumber(colIdx));
            contentTokenList.add(new TeXCsRef("db@col@id@end@"));

            contentTokenList.add(new TeXCsRef("db@col@elt@w"));
            contentTokenList.add(value, true);
            contentTokenList.add(new TeXCsRef("db@col@elt@end@"));

            contentTokenList.add(new TeXCsRef("db@col@id@w"));
            contentTokenList.add(new UserNumber(colIdx));
            contentTokenList.add(new TeXCsRef("db@col@id@end@"));
         }

         contentTokenList.add(new TeXCsRef("db@row@elt@w"));
         contentTokenList.add(new TeXCsRef("db@row@id@w"));
         contentTokenList.add(new UserNumber(rowIdx));
         contentTokenList.add(new TeXCsRef("db@row@id@end@"));

         texApp.progress((100*(++progress))/maxProgress);
      }

      contentsReg.setContents(parser, contentTokenList);
      headerReg.setContents(parser, headerTokenList);

      parser.putControlSequence(false, contentsReg);
      parser.putControlSequence(false, headerReg);

      while (!mapArg.isEmpty())
      {
         ControlSequence markerCs;

         try
         {
            markerCs = TeXParserUtils.popControlSequence(parser, mapArg);

            if (!markerCs.getName().equals("dtl@db@reconstruct@keyindex"))
            {
               throw new TeXSyntaxException(parser,
                TeXSyntaxException.ERROR_EXPECTED_BUT_FOUND, 
                 "\\dtl@db@reconstruct@keyindex", markerCs);
            }
         }
         catch (EOFException e)
         {
            break;
         }

         String key = TeXParserUtils.popLabelString(parser, mapArg);
         int colIdx = TeXParserUtils.popInt(parser, mapArg);

         parser.putControlSequence(false,
          new IntegerContentCommand("dtl@ci@"+name+"@"+key, colIdx));
      }

      db.update(headers, rows);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
