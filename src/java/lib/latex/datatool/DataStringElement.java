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

public class DataStringElement extends TeXObjectList implements DataElement
{
   public DataStringElement()
   {
      super();
   }

   public DataStringElement(int capacity)
   {
      super(capacity);
   }

   public DataStringElement(TeXObjectList list)
   {
      super(list.capacity());
      addAll(list);
   }

   public Object clone()
   {
      DataStringElement element = new DataStringElement(capacity());

      for (TeXObject obj : this)
      {
         element.add((TeXObject)obj.clone());
      }

      return element;
   }

   public byte getDataType()
   {
      return DataToolHeader.TYPE_STRING;
   }
}
