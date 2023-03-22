/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.util.Vector;
import java.util.ArrayDeque;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;

/**
 * For lists of objects that should be treated as a single unit.
 */

public class DataObjectList extends TeXObjectList
{
   public DataObjectList()
   {
      this(false);
   }

   public DataObjectList(boolean protect)
   {
      super();
      this.protect = protect;
   }

   public DataObjectList(int capacity)
   {
      this(capacity, false);
   }

   public DataObjectList(int capacity, boolean protect)
   {
      super(capacity);
      this.protect = protect;
   }

   public DataObjectList(TeXParserListener listener, String text)
   {
      this(listener, text, false);
   }

   public DataObjectList(TeXParserListener listener, String text, boolean protect)
   {
      super(listener, text);
      this.protect = protect;
   }

   @Override
   public boolean isStack()
   {
      return false;
   }

   @Override
   public boolean canExpand()
   {
      return protect ? false : super.canExpand();
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public boolean isSingleToken()
   {
      return false;
   }

   public TeXObjectList createList()
   {
      return new DataObjectList(capacity(), protect);
   }

   @Override
   public boolean add(TeXObject object, boolean flatten)
   {
      if (flatten && object instanceof DataObjectList
           && (protect || ((DataObjectList)object).protect))
      {
         return add(object, false);
      }
      else
      {
         return super.add(object, flatten);
      }
   }

   @Override
   public void push(TeXObject object, boolean flatten)
   {
      if (flatten && object instanceof DataObjectList
           && (protect || ((DataObjectList)object).protect))
      {
         super.push(object, false);
      }
      else
      {
         super.push(object, flatten);
      }
   }

   protected boolean protect = false;
}
