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
import java.util.Vector;
import java.util.Collection;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataToolRows extends Vector<DataToolEntryRow> 
 implements TeXObject
{
   public DataToolRows(DataToolSty sty)
   {
      super();
      this.sty = sty;
   }

   public DataToolRows(DataToolSty sty, int capacity)
   {
      super(capacity);
      this.sty = sty;
   }

   public Object clone()
   {
      DataToolRows rows = new DataToolRows(sty, capacity());

      for (DataToolEntryRow row : this)
      {
         rows.add((DataToolEntryRow)row.clone());
      }

      return rows;
   }

   @Override
   public synchronized void addElement(DataToolEntryRow row)
   {
      row.setRowIndex(size()+1);
      super.addElement(row);
   }

   @Override
   public synchronized boolean add(DataToolEntryRow row)
   {
      row.setRowIndex(size()+1);
      return super.add(row);
   }

   @Override
   public synchronized void insertElementAt(DataToolEntryRow row, int index)
   {
      super.insertElementAt(row, index);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }
   }

   @Override
   public synchronized DataToolEntryRow set(int index, DataToolEntryRow row)
   {
      DataToolEntryRow oldRow = super.set(index, row);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }

      return oldRow;
   }

   @Override
   public synchronized void setElementAt(DataToolEntryRow row, int index)
   {
      super.setElementAt(row, index);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }
   }

   @Override
   public synchronized boolean addAll(int index, Collection<? extends DataToolEntryRow> collection)
   {
      boolean result = super.addAll(index, collection);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }

      return result;
   }

   @Override
   public synchronized boolean addAll(Collection<? extends DataToolEntryRow> collection)
   {
      int n = size();

      boolean result = super.addAll(collection);

      for (int i = n; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }

      return result;
   }

   @Override
   public synchronized void removeElementAt(int index)
   {
      super.removeElementAt(index);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }
   }

   @Override
   public synchronized DataToolEntryRow remove(int index)
   {
      DataToolEntryRow oldRow = super.remove(index);

      for (int i = index; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }

      return oldRow;
   }

   @Override
   protected synchronized void removeRange(int fromIndex, int toIndex)
   {
      super.removeRange(fromIndex, toIndex);

      for (int i = fromIndex; i < size(); i++)
      {
         get(i).setRowIndex(i+1);
      }
   }

   public static DataToolRows toRows(TeXParser parser,
     TeXObjectList stack, DataToolSty sty, int likelyRowCount)
      throws IOException
   {
      if (stack.peekStack() instanceof DataToolRows)
      {
         return (DataToolRows)stack.popToken();
      }

      DataToolRows rows = new DataToolRows(sty);
      DataToolEntryRow row;

      int progress=0;
      TeXApp texApp = null;

      if (likelyRowCount > 0)
      {
         texApp = parser.getListener().getTeXApp();
         texApp.progress(progress);
      }

      while ((row = DataToolEntryRow.toEntryRow(parser, stack, sty)) != null)
      {
         rows.add(row);

         if (texApp != null)
         {
            progress++;
            texApp.progress((100*progress)/likelyRowCount);
         }
      }

      return rows;
   }

   public DataToolEntryRow getRow(int rowIndex)
   {
      for (DataToolEntryRow row : this)
      {
         if (row.getRowIndex() == rowIndex)
         {
            return row;
         }
      }

      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      for (DataToolEntryRow row : this)
      {
         list.addAll(row.expandonce(parser));
      }

      return list;
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.addAll(0, expandonce(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      process(parser);
   }

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

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return expandonce(parser).string(parser);
   }

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

   public boolean isPar()
   {
      return false;
   }

   private DataToolSty sty;
}
