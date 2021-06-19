/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public class PopStyle
{
   public PopStyle(byte style)
   {
      if (style < 0)
      {
         throw new IllegalArgumentException("Invalid pop value "+style);
      }

      this.style = style;
   }

   public PopStyle(int style)
   {
      this((byte)style);
   }

   public byte getStyle()
   {
      return style;
   }

   public boolean isSet(byte value)
   {
      return (style & value) == value;
   }

   public boolean isShort()
   {
      return isSet(TeXObject.POP_SHORT);
   }

   public boolean isRetainIgnoreables()
   {
      return isSet(TeXObject.POP_RETAIN_IGNOREABLES);
   }

   public boolean isIgnoreLeadingSpace()
   {
      return isSet(TeXObject.POP_IGNORE_LEADING_SPACE);
   }

   public boolean isIgnoreLeadingPar()
   {
      return isSet(TeXObject.POP_IGNORE_LEADING_PAR);
   }

   public static PopStyle valueOf(int newStyle)
   {
      return valueOf((byte)newStyle);
   }

   public static PopStyle valueOf(byte newStyle)
   {
      if (newStyle == DEFAULT.style) return DEFAULT;
      if (newStyle == SHORT.style) return SHORT;
      if (newStyle == RETAIN_IGNOREABLES.style) return RETAIN_IGNOREABLES;
      if (newStyle == IGNORE_LEADING_SPACE.style) return IGNORE_LEADING_SPACE;
      if (newStyle == SHORT_IGNORE_LEADING_SPACE.style) 
         return SHORT_IGNORE_LEADING_SPACE;
      if (newStyle == SHORT_RETAIN_IGNOREABLES.style)
         return SHORT_RETAIN_IGNOREABLES;
      if (newStyle == IGNORE_LEADING_PAR.style) return IGNORE_LEADING_PAR;
      if (newStyle == IGNORE_LEADING_PAR_AND_SPACE.style) 
         return IGNORE_LEADING_PAR_AND_SPACE;

      return new PopStyle(newStyle);
   }

   public PopStyle excludeLeadingStyles()
   {
      return excludeStyle(
           TeXObject.POP_IGNORE_LEADING_SPACE
         | TeXObject.POP_IGNORE_LEADING_PAR);
   }

   public PopStyle excludeStyle(int exStyle)
   {
      return excludeStyle((byte)exStyle);
   }

   public PopStyle excludeStyle(byte exStyle)
   {
      return valueOf(style ^ (style & exStyle));
   }

   @Override
   public boolean equals(Object other)
   {
      if (!(other instanceof PopStyle)) return false;

      return ((PopStyle)other).style == style;
   }

   private byte style;

   public static final PopStyle DEFAULT = new PopStyle(0);

   public static final PopStyle SHORT = new PopStyle(TeXObject.POP_SHORT);

   public static final PopStyle RETAIN_IGNOREABLES 
     = new PopStyle(TeXObject.POP_RETAIN_IGNOREABLES);

   public static final PopStyle IGNORE_LEADING_SPACE 
     = new PopStyle(TeXObject.POP_IGNORE_LEADING_SPACE);

   public static final PopStyle IGNORE_LEADING_PAR 
     = new PopStyle(TeXObject.POP_IGNORE_LEADING_PAR);

   public static final PopStyle SHORT_IGNORE_LEADING_SPACE 
     = new PopStyle(
       TeXObject.POP_SHORT |
       TeXObject.POP_IGNORE_LEADING_SPACE);

   public static final PopStyle SHORT_RETAIN_IGNOREABLES 
     = new PopStyle(
       TeXObject.POP_SHORT |
       TeXObject.POP_RETAIN_IGNOREABLES);

   public static final PopStyle IGNORE_LEADING_SPACE_RETAIN_IGNOREABLES
     = new PopStyle(
       TeXObject.POP_IGNORE_LEADING_SPACE |
       TeXObject.POP_RETAIN_IGNOREABLES);

   public static final PopStyle IGNORE_LEADING_PAR_AND_SPACE
     = new PopStyle(
       TeXObject.POP_IGNORE_LEADING_SPACE |
       TeXObject.POP_IGNORE_LEADING_PAR);

}

