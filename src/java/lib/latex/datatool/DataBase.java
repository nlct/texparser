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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataBase
{
   public DataBase(String name)
   {
      this(name, null, null);
   }

   public DataBase(String name, DataToolHeaderRow headers,
    DataToolRows rows)
   {
      this.name = name;
      update(headers, rows);
   }

   public void update(DataToolHeaderRow headers,
    DataToolRows rows)
   {
      this.headerRow = headers;
      this.dataRows = rows;
   }

   public int getColumnCount()
   {
      return headerRow == null ? 0 : headerRow.size();
   }

   public int getRowCount()
   {
      return dataRows == null ? 0 : dataRows.size();
   }

   public DataToolHeaderRow getHeaders()
   {
      return headerRow;
   }

   public DataToolHeader getHeader(String key)
   {
      return headerRow.getHeader(key);
   }

   public DataToolHeader getHeader(int columnIndex)
   {
      return headerRow.getHeader(columnIndex);
   }

   public DataToolRows getData()
   {
      return dataRows;
   }

   public String getName()
   {
      return name;
   }

   public String toString()
   {
      return String.format("%s[name=%s,header=%s,data=%s]",
       getClass().getSimpleName(), name, headerRow, dataRows);
   }

   private DataToolHeaderRow headerRow;
   private DataToolRows dataRows;
   private String name;
}
