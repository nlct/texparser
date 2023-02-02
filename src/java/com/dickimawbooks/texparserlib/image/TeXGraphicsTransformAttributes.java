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

import java.awt.geom.AffineTransform;

import com.dickimawbooks.texparserlib.*;

public class TeXGraphicsTransformAttributes
{
   public TeXGraphicsTransformAttributes()
   {
      this(new AffineTransform(), new UserDimension(1f, TeXUnit.PT),
       new UserDimension(1f, TeXUnit.PT));
   }

   public TeXGraphicsTransformAttributes(AffineTransform af,
      TeXDimension unitWidth, TeXDimension unitHeight)
   {
      this.transform = af;
      this.unitWidth = unitWidth;
      this.unitHeight = unitHeight;
   }

   public AffineTransform getTransform()
   {
      return transform;
   }

   public TeXDimension getUnitWidth()
   {
      return unitWidth;
   }

   public TeXDimension getUnitHeight()
   {
      return unitHeight;
   }

   public void setUnitWidth(TeXDimension dim)
   {
      unitWidth = dim;
   }

   public void setUnitHeight(TeXDimension dim)
   {
      unitHeight = dim;
   }

   public void setUnitLength(TeXDimension dim)
   {
      unitWidth = dim;
      unitHeight = dim;
   }

   public Object clone()
   {
      return new TeXGraphicsTransformAttributes(
         (AffineTransform)transform.clone(),
         (TeXDimension)unitWidth.clone(),
         (TeXDimension)unitHeight.clone());
   }

   public float toBpX(TeXParser parser, float value) throws TeXSyntaxException
   {
      return toBp(parser, value, unitWidth);
   }

   public float toBpY(TeXParser parser, float value) throws TeXSyntaxException
   {
      return toBp(parser, value, unitHeight);
   }

   public static float toBp(TeXParser parser, float value, TeXDimension dim)
    throws TeXSyntaxException
   {
      return dim.getUnit().toUnit(parser, value, FixedUnit.BP);
   }

   private AffineTransform transform;

   private TeXDimension unitWidth;
   private TeXDimension unitHeight;

}

