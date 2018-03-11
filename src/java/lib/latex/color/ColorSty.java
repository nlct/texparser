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
package com.dickimawbooks.texparserlib.latex.color;

import java.io.IOException;
import java.util.HashMap;
import java.awt.Color;
import java.awt.color.ColorSpace;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ColorSty extends LaTeXSty
{
   public ColorSty(KeyValList options, LaTeXParserListener listener,
      boolean loadParentOptions)
    throws IOException
   {
      this(options, "color", listener, loadParentOptions);
   }

   public ColorSty(KeyValList options, String styname, 
     LaTeXParserListener listener, boolean loadParentOptions)
    throws IOException
   {
      super(options, styname, listener, loadParentOptions);
      initColors();
   }

   private void initColors()
   {
      definedColors = new HashMap<String,Color>();

      putColor("black", Color.BLACK);
      putColor("blue", Color.BLUE);
      putColor("cyan", Color.CYAN);
      putColor("gray", Color.GRAY);
      putColor("green", Color.GREEN);
      putColor("magenta", Color.MAGENTA);
      putColor("orange", Color.ORANGE);
      putColor("pink", Color.PINK);
      putColor("red", Color.RED);
      putColor("white", Color.WHITE);
      putColor("yellow", Color.YELLOW);
   }

   public void putColor(String name, Color color)
   {
      definedColors.put(name, color);
   }

   public void addDefinitions()
   {
      addDeclaration(new ColorDeclaration(this), "textcolor");
      registerControlSequence(new ColorDeclaration(this, "pagecolor", false));
      registerControlSequence(new DefineColor(this));
   }

   protected void addDeclaration(Declaration decl, String name)
   {
      registerControlSequence(decl);
      registerControlSequence(new TextBlockCommand(name, decl));
   }

   public Color getColor(TeXParser parser, String model, String specs)
     throws IOException
   {
      if (model.equals("named"))
      {
         Color col = definedColors.get(specs);

         if (col == null)
         {
            throw new LaTeXSyntaxException(parser, ColorSty.UNKNOWN, specs);
         }

         return col;
      }
      else if (model.equals("rgb"))
      {
         String[] split = specs.split(",", 3);

         if (split.length != 3)
         {
            throw new LaTeXSyntaxException(parser, 
              ColorSty.INVALID_SPECS, specs, model);
         }

         try
         {
            float red = Float.parseFloat(split[0]);
            float green = Float.parseFloat(split[1]);
            float blue = Float.parseFloat(split[2]);

            return new Color(red, green, blue);
         }
         catch (NumberFormatException e)
         {
            throw new LaTeXSyntaxException(e, parser, 
              ColorSty.INVALID_SPECS, specs, model);
         }
      }
      else if (model.equals("gray"))
      {
         try
         {
            float gray = Float.parseFloat(specs);

            return new Color(
              ColorSpace.getInstance(ColorSpace.CS_GRAY), 
              new float[] {gray}, 1.0f);
         }
         catch (NumberFormatException e)
         {
            throw new LaTeXSyntaxException(e, parser, 
              ColorSty.INVALID_SPECS, specs, model);
         }
      }

      throw new LaTeXSyntaxException(parser, 
        ColorSty.UNSUPPORTED_COLOR, model, specs);
   }

   private HashMap<String,Color> definedColors;

   public static final String UNSUPPORTED_COLOR = 
      "color.unsupported";
   public static final String INVALID_SPECS = 
      "color.invalid.specs";
   public static final String UNKNOWN = "color.unknown";
}
