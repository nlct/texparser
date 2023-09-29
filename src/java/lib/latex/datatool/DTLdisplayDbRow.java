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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLdisplayDbRow extends ControlSequence
{
   public DTLdisplayDbRow(DataToolSty sty)
   {
      this("__datatool_display_db_row:Nn", sty);
   }

   public DTLdisplayDbRow(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLdisplayDbRow(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TokenListCommand contentTl = listener.popTokenListCommand(parser, stack);
      int rowIdx = TeXParserUtils.popInt(parser, stack);

      String dbLabel = parser.expandToString(
        listener.getControlSequence("dtldbname"), stack);

      DataBase db = sty.getDataBase(dbLabel);
      DataToolRows data = db.getData();
      DataToolEntryRow row = data.getRow(rowIdx);
      int numCols = db.getColumnCount();

      NumericRegister rowNumReg =
        parser.getSettings().getNumericRegister("dtlrownum");

      if (row == null)
      {
         listener.getTeXApp().warning(parser, 
            listener.getTeXApp().getMessage(
           "ERROR_ROW_NOT_FOUND", rowIdx));
      }
      else
      {
         rowNumReg.advance(parser, UserNumber.ONE);

         if (rowNumReg.number(parser) > 1)
         {
            contentTl.appendValue(listener.getControlSequence("dtldisplaycr"), 
              parser, stack);
            contentTl.appendValue(listener.getControlSequence("dtldisplaystartrow"), 
              parser, stack);
         }

         parser.getSettings().localSetRegister("l__datatool_col_idx_int",
            UserNumber.ZERO);

         NumericRegister colIdxReg =
           parser.getSettings().getNumericRegister("l__datatool_col_idx_int");

         SequenceCommand colIndexes = listener.getSequenceCommand(
           "l__datatool_column_indexes_seq", stack);

         for (int i = 0; i < colIndexes.size(); i++)
         {
            int colIdx = TeXParserUtils.toInt(colIndexes.get(i), parser, stack);

            if (i > 0)
            {
               contentTl.append(listener.getTab());
            }

            colIdxReg.advance(parser, UserNumber.ONE);

            DataToolEntry entry = row.getEntry(colIdx);

//TODO check data type and add formatting command
            contentTl.append((TeXObject)entry.getContents().clone());
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
