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
      super();
   }

   public DataObjectList(int capacity)
   {
      super(capacity);
   }

   public DataObjectList(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public boolean isStack()
   {
      return false;
   }

   public TeXObjectList createList()
   {
      return new DataObjectList(capacity());
   }

}
