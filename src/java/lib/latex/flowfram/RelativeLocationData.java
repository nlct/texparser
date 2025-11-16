/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import com.dickimawbooks.texparserlib.TeXDimension;

public class RelativeLocationData
{
   public RelativeLocationData(final String label)
   {
      this.label = label;
   }

   public String getLabel()
   {
      return label;
   }

   public void calculate()
   {
      float xVal1, yVal1, widthVal1, heightVal1;
      float xVal2, yVal2, widthVal2, heightVal2;

      if (pageNum % 2 == 0)
      {
         xVal1 = evenX1.getValue();
         yVal1 = evenY1.getValue();

         xVal2 = evenX2.getValue();
         yVal2 = evenY2.getValue();
      }
      else
      {
         xVal1 = x1.getValue();
         yVal1 = y1.getValue();

         xVal2 = x2.getValue();
         yVal2 = y2.getValue();
      }

      widthVal1 = width1.getValue();
      heightVal1 = height1.getValue();

      widthVal2 = width2.getValue();
      heightVal2 = height2.getValue();

      isRight = (xVal1 > xVal2 + widthVal2);
      isLeft = (xVal1 + widthVal1 < xVal2);

      isAbove = (yVal1 > yVal2 + widthVal2);
      isBelow = (yVal1 + widthVal1 < yVal2);
   }

   public String getPlaceholderName()
   {
      if (isRight)
      {
         if (isAbove)
         {
            return "FFaboveright";
         }
         else if (isBelow)
         {
            return "FFbelowright";
         }
         else
         {
            return "FFright";
         }
      }
      else if (isLeft)
      {
         if (isAbove)
         {
            return "FFaboveleft";
         }
         else if (isBelow)
         {
            return "FFbelowleft";
         }
         else
         {
            return "FFleft";
         }
      }
      else if (isAbove)
      {
         return "FFabove";
      }
      else if (isBelow)
      {
         return "FFbelow";
      }
      else
      {
         return "FFoverlap";
      }
   }

   public boolean isRight()
   {
      return isRight;
   }

   public boolean isLeft()
   {
      return isLeft;
   }

   public boolean isAbove()
   {
      return isAbove;
   }

   public boolean isBelow()
   {
      return isBelow;
   }

   final String label;

   public int pageNum;

   public TeXDimension x1, y1, evenX1, evenY1, width1, height1;
   public TeXDimension x2, y2, evenX2, evenY2, width2, height2;

   boolean isRight=false, isLeft=false, isAbove=false, isBelow=false;
}
