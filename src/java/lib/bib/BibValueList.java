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

   public TeXObject getContents()
   {
      TeXObjectList list = new TeXObjectList(size());

      for (BibValue value : this)
      {
         list.add(value.getContents());
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
