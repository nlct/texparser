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


      throw new IllegalArgumentException(String.format("Invalid argument: %d",
         fieldDelimChange));
   }

   public String toString()
   {
      return String.format("%s[%s]", getClass().getSimpleName(), 
         string.format());
   }

   private void convertAt(TeXObjectList list, TeXParser parser)
      throws IOException
   {
      for (int i = 0; i < list.size(); i++)
      {
         TeXObject object = list.get(i);

         if (object instanceof At)
         {
            list.set(i, parser.getListener().getOther('@'));
         }
         else if (object instanceof TeXObjectList)
         {
            convertAt((TeXObjectList)object, parser);
         }
      }
   }

   public TeXObjectList expand(TeXParser parser)
    throws IOException
   {
      TeXObjectList list;

      if (string instanceof Group)
      {
         list = ((Group)string).toList();
         convertAt(list, parser);
         return list;
      }

      list = new TeXObjectList();

      if (!(string instanceof TeXObjectList))
      {
         if (string instanceof At)
         {
            list.add(parser.getListener().getOther('@'));
         }
         else
         {
            list.add(string);
         }

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
         i = expand(parser, stringList, i, list);
      }

      return list;
   }

   protected int expand(TeXParser parser, 
      TeXObjectList stringList, int i, TeXObjectList list)
   throws IOException
   {
      TeXObject obj = stringList.get(i);

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList subList = ((TeXObjectList)obj).createList();

         for (int j = 0; j < ((TeXObjectList)obj).size(); j++)
         {
            j = expand(parser, (TeXObjectList)obj, j, subList);
         }

         list.add(subList);
      }
      else if (obj instanceof At)
      {
         list.add(parser.getListener().getOther('@'));
      }
      else if (obj instanceof CharObject)
      {
         // # inside a string should be considered a parameter
         // marker, not string concatenation.

         if (((CharObject)obj).getCharCode() == '#'
             && i < stringList.size()-1)
         {
            // is the next object another '#' or a digit?

            TeXObject nextObj = stringList.get(i+1);

            if (nextObj instanceof CharObject)
            {
               int c = ((CharObject)nextObj).getCharCode();

               if (c == '#' && i < stringList.size()-2)
               {
                  nextObj = stringList.get(i+2);

                  if (nextObj instanceof CharObject
                  && ((CharObject)nextObj).getCharCode() > '0'
                  && ((CharObject)nextObj).getCharCode() <= '9')
                  {
                     list.add(parser.getListener().getParam(
                       ((CharObject)nextObj).getCharCode()-(int)'0'));
                     i += 2;
                  }
                  else
                  {
                     list.add(obj);
                  }
               }
               else if (c > '0' && c <= '9')
               {
                  list.add(parser.getListener().getParam(c-(int)'0'));
                  i++;
               }
               else
               {
                  list.add(obj);
               }
            }
            else
            {// leave it as a CharObject
               list.add(obj);
            }
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

      return i;
   }

   public Object clone()
   {
      return new BibUserString((TeXObject)string.clone());
   }

   private TeXObject string;
}
