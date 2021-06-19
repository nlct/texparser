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

public class DataToolHeaderRow extends Vector<DataToolHeader> 
 implements TeXObject,Expandable
{
   public DataToolHeaderRow(DataToolSty sty)
   {
      super();
      this.sty = sty;
   }

   public DataToolHeaderRow(DataToolSty sty, int capacity)
   {
      super(capacity);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      DataToolHeaderRow row = new DataToolHeaderRow(sty, capacity());

      for (DataToolHeader header : this)
      {
         row.add((DataToolHeader)header.clone());
      }

      return row;
   }

   public static DataToolHeaderRow toHeaderRow(TeXParser parser,
     TeXObjectList stack, DataToolSty sty)
      throws IOException
   {
      if (stack.peekStack() instanceof DataToolHeaderRow)
      {
         return (DataToolHeaderRow)stack.popToken();
      }

      DataToolHeaderRow row = new DataToolHeaderRow(sty);

      DataToolHeader header;

      while ((header = DataToolHeader.popHeader(parser, stack, sty)) != null)
      {
         row.add(header);
      }

      return row;
   }

   public DataToolHeader getHeader(String key)
   {
      for (DataToolHeader header : this)
      {
         if (header.getColumnLabel().equals(key))
         {
            return header;
         }
      }

      return null;
   }

   public DataToolHeader getHeader(int columnIndex)
   {
      for (DataToolHeader header : this)
      {
         if (header.getColumnIndex() == columnIndex)
         {
            return header;
         }
      }

      return null;
   }

   public int getMaxIndex()
   {
      int index = 0;

      for (DataToolHeader header : this)
      {
         int colIndex = header.getColumnIndex();

         if (colIndex > index)
         {
            index = colIndex;
         }
      }

      return index;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      for (DataToolHeader header : this)
      {
         list.addAll(header.expandonce(parser));
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
      return expandonce(parser, stack);
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
      stack.addAll(0, expandonce(parser, stack));
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
}
