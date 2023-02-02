/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class EtoolboxList extends DataObjectList
{
   public EtoolboxList()
   {
      super(true);
   }

   public EtoolboxList(int capacity)
   {
      super(capacity, true);
   }

   public TeXObjectList createList()
   {
      return new EtoolboxList(capacity());
   }

   @Override
   public boolean add(TeXObject obj)
   {
      if (obj instanceof TeXObjectList)
      {
         ((TeXObjectList)obj).stripIgnoreables();
      }

      return super.add(obj);
   }

   @Override
   public boolean add(TeXObject obj, boolean flatten)
   {
      if (obj instanceof TeXObjectList)
      {
         ((TeXObjectList)obj).stripIgnoreables();
      }

      return super.add(obj, false);
   }

   @Override
   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < size(); i++)
      {
         if (i > 0)
         {
            builder.append('|');
         }

         builder.append(get(i).toString(parser));
      }

      return builder.toString();
   }
}
