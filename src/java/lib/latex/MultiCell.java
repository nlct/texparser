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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class MultiCell extends Command
{
   public MultiCell(String name)
   {
      this(name, 1, 1, null);
   }

   public MultiCell(String name, int columnSpan, int rowSpan, 
     TeXCellAlign alignment)
   {
      super(name);
      setColumnSpan(columnSpan);
      setRowSpan(rowSpan);
      setAlignment(alignment);
   }

   public MultiCell(String name, int columnSpan, int rowSpan, 
     TeXParser parser, TeXObject alignSpecs)
    throws IOException
   {
      super(name);
      setColumnSpan(columnSpan);
      setRowSpan(rowSpan);
      setAlignment(parser, alignSpecs);
   }

   public Object clone()
   {
      MultiCell object = new MultiCell(getName());

      object.setColumnSpan(getColumnSpan());
      object.setRowSpan(getRowSpan());
      object.setAlignment(getAlignment());

      return object;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      stack.popArg(parser).process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      parser.popNextArg().process(parser);
   }

   public int getColumnSpan()
   {
      return columnSpan;
   }

   public int getRowSpan()
   {
      return rowSpan;
   }

   public TeXCellAlign getAlignment()
   {
      return alignment;
   }

   public void setColumnSpan(int columnSpan)
   {
      this.columnSpan = columnSpan;
   }

   public void setRowSpan(int rowSpan)
   {
      this.rowSpan = rowSpan;
   }

   public void setAlignment(TeXCellAlign alignment)
   {
      this.alignment = alignment;
   }

   public void setAlignment(TeXParser parser, TeXObject alignSpecs)
     throws IOException
   {
      TeXCellAlignList alignList = new TeXCellAlignList(parser, alignSpecs);

      if (alignList.size() > 0)
      {
         this.alignment = alignList.get(0);
      }
      else
      {
         this.alignment = null;
      }
   }

   public String toString()
   {
      return String.format("%s{colspan:%d,rowspan:%d,align:%s}",
       getName(), getColumnSpan(), getRowSpan(), getAlignment());
   }

   private int columnSpan=1, rowSpan=1;
   private TeXCellAlign alignment;
}
