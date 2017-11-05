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
 * Bib number
 */

public class BibNumber implements BibValue
{
   public BibNumber(TeXNumber value)
   {
      this.value = value;
   }

   public TeXObject getContents()
   {
      return value;
   }

   public String applyDelim(byte fieldDelimChange)
   {
      return value.format();
   }

   public String toString()
   {
      return String.format("%s[value=%s]", getClass().getSimpleName(),
       value.format());
   }

   public TeXObjectList expand(TeXParser parser) throws IOException
   {
      TeXObjectList expanded;

      if (value instanceof Expandable)
      {
         expanded = ((Expandable)value).expandfully(parser);

         if (expanded != null) return expanded;
      }

      expanded = new TeXObjectList();
      expanded.add(value);

      return expanded;
   }

   public Object clone()
   {
      return new BibNumber((TeXNumber)value.clone());
   }

   private TeXNumber value;
}
