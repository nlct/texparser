/*
    Copyright (C) 2015 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.image;

import java.awt.Color;
import java.awt.geom.*;

public class TeXGraphicsFillAttributes
{
   public TeXGraphicsFillAttributes()
   {
   }

   public void setFillColor(Color col)
   {
      fillCol = col;
   }

   public Color getFillColor()
   {
      return fillCol;
   }

   public void setWindingRule(int rule)
   {
      windingRule = rule;

/*
      if (currentPath != null)
      {
         currentPath.setWindingRule(rule);
      }
*/
   }

   public int getWindingRule()
   {
      return windingRule;
   }

   public Object clone()
   {
      TeXGraphicsFillAttributes attr = new TeXGraphicsFillAttributes();

      attr.fillCol = fillCol;
      attr.windingRule = windingRule;

      return attr;
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;

      if (obj == this) return true;

      if (!(obj instanceof TeXGraphicsFillAttributes)) return false;

      TeXGraphicsFillAttributes attr = (TeXGraphicsFillAttributes)obj;

      if (!fillCol.equals(attr.fillCol)) return false;

      return windingRule == attr.windingRule;
   }

   private Color fillCol = Color.BLACK;

   private int windingRule = GeneralPath.WIND_NON_ZERO;
}

