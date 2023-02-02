/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class TeXFontShapeDeclaration extends TeXFontDeclaration
{
   public TeXFontShapeDeclaration(String name, int shapeID)
   {
      super(name);
      this.orgWeight = TeXFontWeight.INHERIT;
      this.orgFamily = TeXFontFamily.INHERIT;
      this.orgShape = TeXFontShape.INHERIT;

      switch (shapeID)
      {
         case TeXSettings.INHERIT:
           shape = TeXFontShape.INHERIT;
         break;
         case TeXSettings.SHAPE_UP:
           shape = TeXFontShape.UP;
         break;
         case TeXSettings.SHAPE_IT:
           shape = TeXFontShape.IT;
         break;
         case TeXSettings.SHAPE_SL:
           shape = TeXFontShape.SL;
         break;
         default:
            throw new IllegalArgumentException("Invalid font shape ID "+shapeID);
      }
   }

   public TeXFontShapeDeclaration(String name, TeXFontShape shape)
   {
      super(name);
      this.shape = shape;
      this.orgWeight = TeXFontWeight.INHERIT;
      this.orgFamily = TeXFontFamily.INHERIT;
      this.orgShape = TeXFontShape.INHERIT;
   }
   @Override
   public Object clone()
   {
      return new TeXFontShapeDeclaration(getName(), shape);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      orgWeight = settings.getCurrentFontWeight();
      orgShape = settings.getCurrentFontShape();
      orgFamily = settings.getCurrentFontFamily();

      settings.setFontWeight(TeXFontWeight.MD);
      settings.setFontShape(shape);
      settings.setFontFamily(TeXFontFamily.RM);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontWeight(orgWeight);
      settings.setFontShape(orgShape);
      settings.setFontFamily(orgFamily);
   }

   private TeXFontShape shape, orgShape;
   private TeXFontWeight orgWeight;
   private TeXFontFamily orgFamily;
}
