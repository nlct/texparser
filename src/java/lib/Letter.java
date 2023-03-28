/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

public class Letter extends CharObject implements CaseChangeable
{
   public Letter(int charCode)
   {
      super(charCode);
   }

   @Override
   public TeXObject toLowerCase(TeXParser parser)
   {
      // Not locale-sensitive
      int cp = Character.toLowerCase(charCode);

      if (cp == getCharCode())
      {
         String str = toString(parser).toLowerCase();
         cp = str.codePointAt(0);

         if (str.length() == Character.charCount(cp))
         {
            return parser.getListener().getLetter(cp);
         }
         else
         {
            return parser.getListener().createString(str);
         }
      }
      else
      {
         return parser.getListener().getLetter(cp);
      }
   }

   @Override
   public TeXObject toUpperCase(TeXParser parser)
   {
      // Not locale-sensitive
      int cp = Character.toUpperCase(charCode);

      if (cp == getCharCode())
      {
         /*
           Allow for the possibility that the uppercase 
           version of the letter may consist of multiple characters.
           (For example, eszett -> SS.) In this case,
            Character.toUpperCase(int) will return the original code point.
          */ 
         String str = toString(parser).toUpperCase();

         cp = str.codePointAt(0);

         if (str.length() == Character.charCount(cp))
         {
            return parser.getListener().getLetter(cp);
         }
         else
         {
            return parser.getListener().createString(str);
         }
      }
      else
      {
         return parser.getListener().getLetter(cp);
      }
   }

   @Override
   public Object clone()
   {
      return new Letter(getCharCode());
   }

   @Override
   public int getCatCode()
   {
      return TeXParser.TYPE_LETTER;
   }

}

