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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class Space extends WhiteSpace
{
   public Space()
   {
      this(' ');
   }

   public Space(char spaceChar)
   {
      this((int)spaceChar);
   }

   public Space(int spaceCodePoint)
   {
      setSpace(spaceCodePoint);
   }

   public void setSpace(int spaceCodePoint)
   {
      charCode = spaceCodePoint;
   }

   public int getSpace()
   {
      return charCode;
   }

   public String toString(TeXParser parser)
   {
      return format();
   }

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return parser.string(" ");
   }

   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   public String toString()
   {
      return String.format("%s%s", getClass().getSimpleName(), format());
   }

   public Object clone()
   {
      return new Space(charCode);
   }

   public boolean isEol()
   {
      return charCode == (int)'\n' || charCode == (int)'\r';
   }

   private int charCode;

   public static final int SPACE=0x0020;
   public static final int NO_BREAK_SPACE=0x00A0;
   public static final int OGHAM_SPACE_MARK=0x1680;
   public static final int MONGOLIAN_VOWEL_SEPARATOR=0x180E;// no width
   public static final int EN_QUAD=0x2000;// 1en = 1/2em
   public static final int EM_QUAD=0x2001;// 1em
   public static final int EN_SPACE=0x2002;// 1en = 1/2em
   public static final int EM_SPACE=0x2003;// 1em
   public static final int THREE_PER_EM_SPACE=0x2004;// 1/3 em
   public static final int FOUR_PER_EM_SPACE=0x2005;// 1/4 em
   public static final int SIX_PER_EM_SPACE=0x2006;// 1/6 em
   public static final int FIGURE_SPACE=0x2007;// width of digits
   public static final int PUNCTUATION_SPACE=0x2008;// width of a period .
   public static final int THIN_SPACE=0x2009;// 1/5 em
   public static final int HAIR_SPACE=0x200A;// narrower than thin space
   public static final int ZERO_WIDTH_SPACE=0x200B;// nominally no width
   public static final int NARROW_NO_BREAK_SPACE=0x202F;
   public static final int MEDIUM_MATHEMATICAL_SPACE=0x205F;// 4/18em
   public static final int IDEOGRAPHIC_SPACE=0x3000; 
   public static final int ZERO_WIDTH_NO_BREAK_SPACE=0xFEFF; 
}

