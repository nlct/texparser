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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataToolEntryRow extends Vector<DataToolEntry> 
 implements TeXObject,Expandable
{
   public DataToolEntryRow(DataToolSty sty)
   {
      this(1, sty);
   }

   public DataToolEntryRow(int rowIndex, DataToolSty sty)
   {
      super();
      this.sty = sty;
      setRowIndex(rowIndex);
   }

   public DataToolEntryRow(int rowIndex, DataToolSty sty, int capacity)
   {
      super(capacity);
      this.sty = sty;
      setRowIndex(rowIndex);
   }

   @Override
   public Object clone()
   {
      DataToolEntryRow row = new DataToolEntryRow(rowIndex, sty, capacity());

      for (DataToolEntry element : this)
      {
         row.add((DataToolEntry)element.clone());
      }

      return row;
   }

   public int getRowIndex()
   {
      return rowIndex;
   }

   public void setRowIndex(int rowIndex)
   {
      if (rowIndex < 1)
      {
         throw new IllegalArgumentException("Invalid row index "+rowIndex);
      }

      this.rowIndex = rowIndex;
   }

   public static DataToolEntryRow toEntryRow(TeXParser parser,
     TeXObjectList stack, DataToolSty sty)
      throws IOException
   {
      if (stack.peekStack(PopStyle.IGNORE_LEADING_SPACE)
            instanceof DataToolEntryRow)
      {
         return (DataToolEntryRow)stack.popToken(
            PopStyle.IGNORE_LEADING_SPACE);
      }

      if (!stack.popCsMarker(parser, "db@row@elt@w", 
            PopStyle.IGNORE_LEADING_SPACE))
      {
         return null;
      }

      if (!stack.popCsMarker(parser, "db@row@id@w", 
            PopStyle.IGNORE_LEADING_SPACE))
      {
         return null;
      }

      Numerical number = stack.popNumerical(parser);

      int rowIndex = number.number(parser);

      stack.popCsMarker(parser, "db@row@id@end@");

      DataToolEntryRow row = new DataToolEntryRow(sty);

      DataToolEntry entry;

      while ((entry = DataToolEntry.toEntry(parser, stack, sty)) != null)
      {
         row.add(entry);
      }

      stack.popCsMarker(parser, "db@row@id@w");

      number = stack.popNumerical(parser);

      int n = number.number(parser);

      stack.popCsMarker(parser, "db@row@id@end@");

      if (rowIndex != n)
      {
         throw new LaTeXSyntaxException(parser,
            DataToolSty.ERROR_MISMATCHED,
            String.format("\\db@col@id@w %d\\db@col@id@end@", rowIndex),
            String.format("\\db@col@id@w %d\\db@col@id@end@", n)
          );
      }

      stack.popCsMarker(parser, "db@row@elt@end@");

      row.setRowIndex(rowIndex);

      return row;
   }

   public DataToolEntry getEntry(int columnIndex)
   {
      for (DataToolEntry entry : this)
      {
         if (entry.getColumnIndex() == columnIndex)
         {
            return entry;
         }
      }

      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      for (DataToolEntry entry : this)
      {
         list.addAll(entry.expandonce(parser));
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      parser.addAll(0, expandonce(parser));
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.addAll(0, expandonce(parser, stack));
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      process(parser, stack);
      return false;
   }

   @Override
   public String toString(TeXParser parser)
   {
      try
      {
         return expandonce(parser).toString(parser);
      }
      catch (IOException e)
      {
         return "";
      }
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return expandonce(parser).string(parser);
   }

   @Override
   public String format()
   {
      try
      {
         return expandonce(sty.getListener().getParser()).format();
      }
      catch (IOException e)
      {
         return "";
      }
   }

   @Override
   public String stripToString(TeXParser parser)
     throws IOException
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < size(); i++)
      {
         if (i > 0)
         {
            builder.append(' ');
         }

         builder.append(get(i).stripToString(parser));
      }

      return builder.toString();
   }

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return false;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public int getTeXCategory()
   {
      return TYPE_OBJECT;
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }

   private DataToolSty sty;
   int rowIndex;
}
