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
 * Bib user string (delimited by "" or {})
 */

public class BibUserString implements BibValue
{
   public BibUserString(TeXObject string)
   {
      this.string = string;
   }

   public TeXObject getContents()
   {
      return string;
   }

   public String applyDelim(byte fieldDelimChange)
   {
      String value = string.format();

      switch (fieldDelimChange)
      {
         case FIELD_DELIM_NOCHANGE: return value;

         case FIELD_DELIM_BRACES:

            if (value.startsWith("{") && value.endsWith("}"))
            {
               return value;
            }

            if (value.startsWith("\"") && value.endsWith("\""))
            {
               return String.format("{%s}",
                 value.substring(1, value.length()-1));
            }

            return String.format("{%s}", value);

         case FIELD_DELIM_QUOTES:

            if (value.startsWith("\"") && value.endsWith("\""))
            {
               return value;
            }

            if (value.startsWith("{") && value.endsWith("}"))
            {
               return String.format("\"%s\"",
                 value.substring(1, value.length()-1));
            }

            return String.format("\"%s\"", value);
      }


      throw new IllegalArgumentException("Invalid argument "+fieldDelimChange);
   }

   public String toString()
   {
      return String.format("%s[%s]", getClass().getSimpleName(), 
         string.format());
   }

   public TeXObjectList expand(TeXParser parser)
    throws IOException
   {
      if (string instanceof Group)
      {
         return ((Group)string).toList();
      }

      TeXObjectList list = new TeXObjectList();

      if (!(string instanceof TeXObjectList))
      {
         list.add(string);
         return list;
      }

      TeXObjectList stringList = (TeXObjectList)string;

      if (stringList.isEmpty())
      {
         return list;
      }

      TeXObject first = stringList.firstElement();
      TeXObject last = stringList.lastElement();

      int firstIdx = 0;
      int lastIdx = stringList.size()-1;

      if (first instanceof CharObject
       && ((CharObject)first).getCharCode() == (int)'"'
       && last instanceof CharObject
       && ((CharObject)last).getCharCode() == (int)'"')
      {
         firstIdx++;
         lastIdx--;
      }

      for (int i = firstIdx; i <= lastIdx; i++)
      {
         list.add(stringList.get(i));
      }

      return list;
   }

   private TeXObject string;
}
