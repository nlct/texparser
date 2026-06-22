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
    XHUGE(5), XXHUGE(6), SMALL(7), FOOTNOTE(8), SCRIPT(9), TINY(10),
    SMALLER(11), LARGER(12), XXXHUGE(13), XXXXHUGE(14);

   TeXFontSize(int id)
   {
      this.id = id;
   }

   public int getValue()
   {
      return id;
   }

   public TeXFontSize deriveRelative(int step)
   {
      if (step == 0) return this;

      switch (this)
      {
         case TINY:
          switch (step)
          {
             case 1: return SCRIPT;
             case 2: return FOOTNOTE;
             case 3: return SMALL;
             case 4: return NORMAL;
             case 5: return LARGE;
             case 6: return XLARGE;
             case 7: return XXLARGE;
             case 8: return HUGE;
             case 9: return XHUGE;
             case 10: return XXHUGE;
             case 11: return XXXHUGE;
             case 12: return XXXXHUGE;
          }
         break;
         case SCRIPT:
          switch (step)
          {
             case -1: return TINY;
             case 1: return FOOTNOTE;
             case 2: return SMALL;
             case 3: return NORMAL;
             case 4: return LARGE;
             case 5: return XLARGE;
             case 6: return XXLARGE;
             case 7: return HUGE;
             case 8: return XHUGE;
             case 9: return XXHUGE;
             case 10: return XXXHUGE;
             case 11: return XXXXHUGE;
          }
         break;
         case FOOTNOTE:
          switch (step)
          {
             case -2: return TINY;
             case -1: return SCRIPT;
             case 1: return SMALL;
             case 2: return NORMAL;
             case 3: return LARGE;
             case 4: return XLARGE;
             case 5: return XXLARGE;
             case 6: return HUGE;
             case 7: return XHUGE;
             case 8: return XXHUGE;
             case 9: return XXXHUGE;
             case 10: return XXXXHUGE;
          }
         break;
         case SMALL:
          switch (step)
          {
             case -3: return TINY;
             case -2: return SCRIPT;
             case -1: return FOOTNOTE;
             case 1: return NORMAL;
             case 2: return LARGE;
             case 3: return XLARGE;
             case 4: return XXLARGE;
             case 5: return HUGE;
             case 6: return XHUGE;
             case 7: return XXHUGE;
             case 8: return XXXHUGE;
             case 9: return XXXXHUGE;
          }
         break;
         case NORMAL:
          switch (step)
          {
             case -4: return TINY;
             case -3: return SCRIPT;
             case -2: return FOOTNOTE;
             case -1: return SMALL;
             case 1: return LARGE;
             case 2: return XLARGE;
             case 3: return XXLARGE;
             case 4: return HUGE;
             case 5: return XHUGE;
             case 6: return XXHUGE;
             case 7: return XXXHUGE;
             case 8: return XXXXHUGE;
          }
         break;
         case LARGE:
          switch (step)
          {
             case -5: return TINY;
             case -4: return SCRIPT;
             case -3: return FOOTNOTE;
             case -2: return SMALL;
             case -1: return NORMAL;
             case 1: return XLARGE;
             case 2: return XXLARGE;
             case 3: return HUGE;
             case 4: return XHUGE;
             case 5: return XXHUGE;
             case 6: return XXXHUGE;
             case 7: return XXXXHUGE;
          }
         break;
         case XLARGE:
          switch (step)
          {
             case -6: return TINY;
             case -5: return SCRIPT;
             case -4: return FOOTNOTE;
             case -3: return SMALL;
             case -2: return NORMAL;
             case -1: return LARGE;
             case 1: return XXLARGE;
             case 2: return HUGE;
             case 3: return XHUGE;
             case 4: return XXHUGE;
             case 5: return XXXHUGE;
             case 6: return XXXXHUGE;
          }
         break;
         case XXLARGE:
          switch (step)
          {
             case -7: return TINY;
             case -6: return SCRIPT;
             case -5: return FOOTNOTE;
             case -4: return SMALL;
             case -3: return NORMAL;
             case -2: return LARGE;
             case -1: return XLARGE;
             case 1: return HUGE;
             case 2: return XHUGE;
             case 3: return XXHUGE;
             case 4: return XXXHUGE;
             case 5: return XXXXHUGE;
          }
         break;
         case HUGE:
          switch (step)
          {
             case -8: return TINY;
             case -7: return SCRIPT;
             case -6: return FOOTNOTE;
             case -5: return SMALL;
             case -4: return NORMAL;
             case -3: return LARGE;
             case -2: return XLARGE;
             case -1: return XXLARGE;
             case 1: return XHUGE;
             case 2: return XXHUGE;
             case 3: return XXXHUGE;
             case 4: return XXXXHUGE;
          }
         break;
         case XHUGE:
          switch (step)
          {
             case -9: return TINY;
             case -8: return SCRIPT;
             case -7: return FOOTNOTE;
             case -6: return SMALL;
             case -5: return NORMAL;
             case -4: return LARGE;
             case -3: return XLARGE;
             case -2: return XXLARGE;
             case -1: return HUGE;
             case 1: return XXHUGE;
             case 2: return XXXHUGE;
             case 3: return XXXXHUGE;
          }
         break;
         case XXHUGE:
          switch (step)
          {
             case -10: return TINY;
             case -9: return SCRIPT;
             case -8: return FOOTNOTE;
             case -7: return SMALL;
             case -6: return NORMAL;
             case -5: return LARGE;
             case -4: return XLARGE;
             case -3: return XXLARGE;
             case -2: return HUGE;
             case -1: return XHUGE;
             case 1: return XXXHUGE;
             case 2: return XXXXHUGE;
          }
         break;
         case XXXHUGE:
          switch (step)
          {
             case -11: return TINY;
             case -10: return SCRIPT;
             case -9: return FOOTNOTE;
             case -8: return SMALL;
             case -7: return NORMAL;
             case -6: return LARGE;
             case -5: return XLARGE;
             case -4: return XXLARGE;
             case -3: return HUGE;
             case -2: return XHUGE;
             case -1: return XXHUGE;
             case 1: return XXXXHUGE;
          }
         break;
         case XXXXHUGE:
          switch (step)
          {
             case -12: return TINY;
             case -11: return SCRIPT;
             case -10: return FOOTNOTE;
             case -9: return SMALL;
             case -8: return NORMAL;
             case -7: return LARGE;
             case -6: return XLARGE;
             case -5: return XXLARGE;
             case -4: return HUGE;
             case -3: return XHUGE;
             case -2: return XXHUGE;
             case -1: return XXXHUGE;
          }
         break;
      }

      return null;
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
// extsizes
      else if (normal == 8)
      {
         switch (this)
         {
            case SMALL: return 7;
            case FOOTNOTE: return 6;
            case SCRIPT: return 5;
            case TINY: return 5;
            case LARGE: return 10;
            case XLARGE: return 11;
            case XXLARGE: return 12;
            case HUGE: return 14;
            case XHUGE: return 17;
            case XXHUGE: return 22;
         }
      }
      else if (normal == 9)
      {
         switch (this)
         {
            case SMALL: return 8;
            case FOOTNOTE: return 7;
            case SCRIPT: return 6;
            case TINY: return 5;
            case LARGE: return 10;
            case XLARGE: return 11;
            case XXLARGE: return 12;
            case HUGE: return 14;
            case XHUGE: return 17;
            case XXHUGE: return 22;
         }
      }
      else if (normal == 14)
      {
         switch (this)
         {
            case SMALL: return 12;
            case FOOTNOTE: return 10;
            case SCRIPT: return 8;
            case TINY: return 6;
            case LARGE: return 17;
            case XLARGE: return 20;
            case XXLARGE: return 25;
            case HUGE: return 29.86;
            case XHUGE: return 35.83;
            case XXHUGE: return 40;
         }
      }
      else if (normal == 17)
      {
         switch (this)
         {
            case SMALL: return 14;
            case FOOTNOTE: return 12;
            case SCRIPT: return 10;
            case TINY: return 8;
            case LARGE: return 20;
            case XLARGE: return 25;
            case XXLARGE: return 29.86;
            case HUGE: return 35.83;
            case XHUGE: return 42.99;
            case XXHUGE: return 50;
         }
      }
      else if (normal == 20)
      {
         switch (this)
         {
            case SMALL: return 17;
            case FOOTNOTE: return 14;
            case SCRIPT: return 12;
            case TINY: return 10;
            case LARGE: return 25;
            case XLARGE: return 29.86;
            case XXLARGE: return 35.83;
            case HUGE: return 42.99;
            case XHUGE: return 51.59;
            case XXHUGE: return 63;
         }
      }
// a0poster
      else if (normal == 25)
      {
         switch (this)
         {
            case NORMAL: return 24.88;
            case SMALL: return 20.74;
            case FOOTNOTE: return 17.28;
            case SCRIPT: return 14.4;
            case TINY: return 12;
            case LARGE: return 29.86;
            case XLARGE: return 35.83;
            case XXLARGE: return 43;
            case HUGE: return 51.6;
            case XHUGE: return 61.92;
            case XXHUGE: return 74.3;// veryHuge
            case XXXHUGE: return 89.16;// VeryHuge
            case XXXXHUGE: return 107;// VERYHuge
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
         case XXXHUGE: return 2.4*normal;
         case XXXXHUGE: return 2.6*normal;
      }

      return normal;
   }

   private final int id;
}

