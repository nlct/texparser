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

      putColor("brown", new Color(0.75f,0.5f,0.25f));
      putColor("lime", new Color(0.75f,1.0f,0.0f));
      putColor("purple", new Color(0.75f,0.0f,0.25f));
      putColor("teal", new Color(0.0f,0.5f,0.5f));
      putColor("violet", new Color(0.5f,0.0f,0.5f));

      putColor("olive", new Color(0.5f,0.5f,0.0f));

      putColor("darkgray", new Color(0.25f,0.25f,0.25f));
      putColor("lightgray", new Color(0.75f,0.75f,0.75f));
   }

   public void putColor(String name, Color color)
   {
      definedColors.put(name, color);
   }

   public Color getDefinedColor(String name)
   {
      return definedColors.get(name);
   }

   @Override
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
      return getColor(parser, model, specs, true);
   }

   public Color getColor(TeXParser parser, String model, String specs,
      boolean checkMixture)
     throws IOException
   {
      if (checkMixture)
      {
         int idx = specs.lastIndexOf("!!");

         if (idx >= 0)
         {// ignoring postfix
            specs = specs.substring(0, idx);
         }

         boolean complement = false;

         if (specs.startsWith("-"))
         {
            complement = true;

            for (int i = 1; i < specs.length(); i++)
            {
               if (specs.charAt(i) != '-')
               {
                  if (i%2 == 1)
                  {
                     complement = false;
                  }

                  specs = specs.substring(i+1);

                  break;
               }
            }
         }

         String[] splitSpecs = specs.split("!");

         // this only covers simple cases

         Color col = getColor(parser, model, splitSpecs[0], false);

         if (complement)
         {
            col = new Color(255-col.getRed(), 255-col.getGreen(), 255-col.getBlue());
         }

         float red = col.getRed()/255.0f;
         float green = col.getGreen()/255.0f;
         float blue = col.getBlue()/255.0f;


         for (int i = 1; i < splitSpecs.length; i+=2)
         {
            try
            {
               float p = Float.parseFloat(splitSpecs[i]) * 0.01f;

               red = red * p;
               green = green * p;
               blue = blue * p;

               col = getColor(parser, model, splitSpecs[i+1], false);

               p = 1.0f-p;

               red = 0.5f*(red + p*col.getRed()/255.0f);
               green = 0.5f*(green + p*col.getGreen()/255.0f);
               blue = 0.5f*(blue + p*col.getBlue()/255.0f);
            }
            catch (NumberFormatException e)
            {
               throw new LaTeXSyntaxException(e, parser, 
                 ColorSty.INVALID_SPECS, specs, model);
            }
         }

         return new Color(red, green, blue);
      }

      // model lists not yet implemented
      String[] models = model.split("/");
      model = models[0];

      String[] specList = specs.split("/");
      specs = specList[0];

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
      else if (model.equals("cmyk"))
      {
         String[] split = specs.split(",", 4);

         if (split.length != 4)
         {
            throw new LaTeXSyntaxException(parser, 
              ColorSty.INVALID_SPECS, specs, model);
         }

         try
         {
            float components[] = 
             {
               Float.parseFloat(split[0]),
               Float.parseFloat(split[1]),
               Float.parseFloat(split[2]),
               Float.parseFloat(split[3])
             };

            return new Color(ColorSpace.getInstance(ColorSpace.TYPE_CMYK), components, 1.0f);
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
