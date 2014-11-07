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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class TeXFontWeightDeclaration extends TeXFontDeclaration
{
   public TeXFontWeightDeclaration(String name, int weight)
   {
      super(name);
      this.weight = weight;
      this.orgWeight = TeXSettings.INHERIT;
      this.orgFamily = TeXSettings.INHERIT;
      this.orgShape = TeXSettings.INHERIT;
   }

   public Object clone()
   {
      return new TeXFontWeightDeclaration(getName(), weight);
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      orgWeight = settings.getCurrentFontWeight();
      orgShape = settings.getCurrentFontShape();
      orgFamily = settings.getCurrentFontFamily();

      settings.setFontWeight(weight);
      settings.setFontShape(TeXSettings.SHAPE_UP);
      settings.setFontFamily(TeXSettings.FAMILY_RM);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontWeight(orgWeight);
      settings.setFontShape(orgShape);
      settings.setFontFamily(orgFamily);
   }

   private int weight, orgWeight, orgFamily, orgShape;
}
