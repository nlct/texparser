/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

public enum TeXFontSize
{
    USER(-2), INHERIT(-1), NORMAL(0), LARGE(1), XLARGE(2), XXLARGE(3), HUGE(4),
    XHUGE(5), XXHUGE(6), SMALL(7), FOOTNOTE(8), SCRIPT(9), TINY(10);

   TeXFontSize(int id)
   {
      this.id = id;
   }

   public int getValue()
   {
      return id;
   }

   /**
    * Gets the approximate size or the normal size if it can't be
    * determined.
    * @param normal the normal font size
    * @return the approximate size in TeX points
    */ 
   public double deriveSize(int normal)
   {
      if (id <= 0)
      {
         return normal;
      }

      if (normal == 10)
      {
         switch (this)
         {
            case SMALL: return 9;
            case FOOTNOTE: return 8;
            case SCRIPT: return 7;
            case TINY: return 5;
            case LARGE: return 12;
            case XLARGE: return 14;
            case XXLARGE: return 17;
            case HUGE: return 20;
            case XHUGE: return 25;
            case XXHUGE: return 30;
         }
      }
      else if (normal == 11)
      {
         switch (this)
         {
            case SMALL: return 10;
            case FOOTNOTE: return 9;
            case SCRIPT: return 8;
            case TINY: return 7;
            case LARGE: return 14;
            case XLARGE: return 18;
            case XXLARGE: return 22;
            case HUGE: return 25;
            case XHUGE: return 30;
            case XXHUGE: return 35;
         }
      }
      else if (normal == 12)
      {
         switch (this)
         {
            case SMALL: return 11;
            case FOOTNOTE: return 10;
            case SCRIPT: return 8;
            case TINY: return 6;
            case LARGE: return 18;
            case XLARGE: return 22;
            case XXLARGE: return 25;
            case HUGE: return 30;
            case XHUGE: return 24;
            case XXHUGE: return 26;
         }
      }

      switch (this)
      {
         case SMALL: return Math.max(5, 0.8*normal);
         case FOOTNOTE: return Math.max(5, 0.6*normal);
         case SCRIPT: return Math.max(5, 0.5*normal);
         case TINY: return Math.max(5, 0.3*normal);
         case LARGE: return 1.2*normal;
         case XLARGE: return 1.4*normal;
         case XXLARGE: return 1.6*normal;
         case HUGE: return 1.8*normal;
         case XHUGE: return 2*normal;
         case XXHUGE: return 2.2*normal;
      }

      return normal;
   }

   private final int id;
}

