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
package com.dickimawbooks.texparserlib.bib;

import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/**
 * Bib value list
 */

public class BibValueList extends Vector<BibValue> implements BibValue
{
   public BibValueList()
   {
      super();
   }

   public BibValueList(int capacity)
   {
      super(capacity);
   }

   public Object clone()
   {
      BibValueList obj;

      int n = size();

      if (n > 10)
      {
         obj = new BibValueList(n);
      }
      else
      {
         obj = new BibValueList();
      }

      for (BibValue value : this)
      {
         obj.add((BibValue)value.clone());
      }

      return obj;
   }

   public TeXObject getContents()
   {
      return getContents(false);
   }

   public static TeXObjectList stripDelim(TeXObjectList list)
   {
      int n = list.size()-1;

      if (n == -1 || n == 1) return list;

      if (n == 0)
      {
         if (list.get(0) instanceof TeXObjectList)
         {
            return stripDelim((TeXObjectList)list.get(0));
         }
         else
         {
            return list;
         }
      }

      TeXObject firstObj = list.get(0);
      TeXObject lastObj = list.get(n);

      if (firstObj instanceof CharObject
        && lastObj instanceof CharObject
        && ((CharObject)firstObj).getCharCode() == '"'
        && ((CharObject)lastObj).getCharCode() == '"')
      {
         list.remove(n);
         list.remove(0);
      }

      return list;
   }

   public TeXObject getContents(boolean stripDelim)
   {
      TeXObjectList list = new TeXObjectList();

      int n = size()-1;

      if (n == -1) return list;

      BibValue value = get(0);
      TeXObject obj = value.getContents();

      if (n >= 2 && obj instanceof CharObject 
          && ((CharObject)obj).getCharCode() == '"')
      {
         BibValue lastValue = get(n);

         if (lastValue instanceof CharObject
           && ((CharObject)obj).getCharCode() == '"')
         {
            n--;
         }
         else
         {
            list.add(obj);
         }
      }
      else
      {
         list.add(obj);
      }

      for (int i = 1; i <= n; i++)
      {
         value = get(i);
         obj = value.getContents();

         list.add(obj);
      }

      if (n == 0 && stripDelim && obj instanceof TeXObjectList
          && !(obj instanceof Group))
      {
         return stripDelim((TeXObjectList)list);
      }

      return list;
   }

   public TeXObjectList expand(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList(size());

      for (BibValue value : this)
      {
         list.addAll(value.expand(parser));
      }

      return list;
   }

   public String applyDelim(byte fieldDelimChange)
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < size(); i++)
      {
         if (i > 0)
         {
            builder.append(" ");
         }

         builder.append(get(i).applyDelim(fieldDelimChange));
      }

      return builder.toString();
   }
}
