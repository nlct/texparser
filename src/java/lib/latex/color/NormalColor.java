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
package com.dickimawbooks.texparserlib.latex.color;

import java.io.IOException;
import java.util.Vector;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NormalColor extends ColorDeclaration
{
   public NormalColor(ColorSty sty)
   {
      this(sty, "normalcolor", true);
   }

   public NormalColor(ColorSty sty, String name, boolean isForeground)
   {
      super(sty, name, isForeground);
      setArgTypes("");
   }

   @Override
   public Object clone()
   {
      return new NormalColor(sty, getName(), isFg);
   }

   protected Color popColor(String modelName, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      ControlSequence cs = parser.getControlSequence("default@color");

      if (cs == null) return Color.BLACK;

      TeXObject defColor = parser.expandFully(cs, stack);

      return sty.getColor(parser, modelName, 
        defColor.stripToString(parser).trim());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      String modelName = parser.popOptionalString(stack);

      if (modelName == null)
      {
         modelName = "named";
      }
      else
      {
         modelName = modelName.trim();
      }

      Color color = popColor(modelName, parser, stack);

      TeXSettings settings = parser.getSettings();

      if (isFg)
      {
         orgColor = settings.getCurrentFgColor();
         settings.setFgColor(color);
      }
      else
      {
         orgColor = settings.getCurrentBgColor();
         settings.setBgColor(color);
      }

      ((LaTeXParserListener)parser.getListener()).startColor(color, isFg);
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
      ((LaTeXParserListener)parser.getListener()).endColor(isFg);

      TeXSettings settings = parser.getSettings();

      if (isFg)
      {
         settings.setFgColor(orgColor);
      }
      else
      {
         settings.setBgColor(orgColor);
      }
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   private ColorSty sty;
   private Color orgColor;
   private boolean isFg=true;
}
