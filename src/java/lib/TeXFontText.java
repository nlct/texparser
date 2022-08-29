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

import java.awt.Font;

public class TeXFontText
{
   public TeXFontText()
   {
   }

   public TeXFontText(TeXFontFamily family)
   {
      setFamily(family);
   }

   public TeXFontText(TeXFontShape shape)
   {
      setShape(shape);
   }

   public TeXFontText(TeXFontFamily family, TeXFontSize size)
   {
      setFamily(family);
      setSize(size);
   }

   public TeXFontText(TeXFontWeight weight)
   {
      setWeight(weight);
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setFamily(TeXFontFamily family)
   {
      this.family = family;
   }

   public void setShape(TeXFontShape shape)
   {
      this.shape = shape;
   }

   public void setWeight(TeXFontWeight weight)
   {
      this.weight = weight;
   }

   public void setSize(TeXFontSize size)
   {
      this.size = size;
   }

   public String getName()
   {
      return name;
   }

   public TeXFontFamily getFamily()
   {
      return family;
   }

   public TeXFontShape getShape()
   {
      return shape;
   }

   public TeXFontWeight getWeight()
   {
      return weight;
   }

   public TeXFontSize getSize()
   {
      return size;
   }

   /**
    * Gets an approximate font size or the normal size if a size
    * can't be determined.
    * @param parser the TeX parser
    * @return the size in PostScript points (rounded)
    */ 
   public int deriveSize(TeXParser parser)
     throws TeXSyntaxException
   {
      int normal = parser.getListener().getNormalFontSize();

      TeXSettings settings = parser.getSettings();

      if (size == TeXFontSize.SMALLER || size == TeXFontSize.LARGER)
      {
         TeXFontSize currentSize = settings.getFontSize();
         boolean smaller = (size == TeXFontSize.SMALLER);
         double bp;
         TeXFontSize newSize;

         switch (currentSize)
         {
            case USER:
              if (userSize != null)
              {
                 TeXUnit unit = userSize.getUnit();
                 bp = unit.toUnit(parser, userSize.getValue(), TeXUnit.BP);
              }
            // fall through
            case NORMAL:
               newSize = smaller ? TeXFontSize.SMALL : TeXFontSize.LARGE;
               bp = newSize.deriveSize(normal);
            break;
            case LARGE:
               newSize = smaller ? TeXFontSize.NORMAL : TeXFontSize.XLARGE;
               bp = newSize.deriveSize(normal);
            break;
            case XLARGE:
               newSize = smaller ? TeXFontSize.LARGE : TeXFontSize.XXLARGE;
               bp = newSize.deriveSize(normal);
            break;
            case XXLARGE:
               newSize = smaller ? TeXFontSize.XLARGE : TeXFontSize.HUGE;
               bp = newSize.deriveSize(normal);
            break;
            case SMALL:
               newSize = smaller ? TeXFontSize.FOOTNOTE : TeXFontSize.NORMAL;
               bp = newSize.deriveSize(normal);
            break;
            case FOOTNOTE:
               newSize = smaller ? TeXFontSize.SCRIPT : TeXFontSize.SMALL;
               bp = newSize.deriveSize(normal);
            break;
            case SCRIPT:
               newSize = smaller ? TeXFontSize.TINY : TeXFontSize.FOOTNOTE;
               bp = newSize.deriveSize(normal);
            break;
            case TINY:
               newSize = smaller ? TeXFontSize.TINY : TeXFontSize.SCRIPT;
               bp = newSize.deriveSize(normal);
            break;
            case HUGE:
               if (smaller)
               {
                  newSize = TeXFontSize.XXLARGE;
                  bp = newSize.deriveSize(normal);
               }
            // fall through
            default:
               bp = currentSize.deriveSize(normal)*PT_TO_BP;
         }

         if (smaller)
         {
            bp *= 0.8;
         }
         else
         {
            bp *= 1.2;
         }

         return (int)Math.round(bp);
      }

      TeXFontSize currentSize = size;

      if (size == TeXFontSize.INHERIT)
      {
         currentSize = settings.getFontSize();
      }

      if (currentSize == TeXFontSize.USER)
      {
         if (userSize == null)
         {
            return normal;
         }
         else
         {
            TeXUnit unit = userSize.getUnit();
            return (int)Math.round(unit.toUnit(parser, userSize.getValue(), TeXUnit.BP));
         }
      }

      return (int)Math.round(currentSize.deriveSize(normal)*PT_TO_BP);
   }

   public Font getFont(TeXParser parser)
    throws TeXSyntaxException
   {
      TeXSettings settings = parser.getSettings();

      TeXFontFamily currentFamily = family;

      if (family == TeXFontFamily.INHERIT)
      {
         currentFamily = settings.getFontFamily();
      }

      TeXFontShape currentShape = shape;

      if (shape == TeXFontShape.INHERIT)
      {
         currentShape = settings.getFontShape();
      }

      TeXFontWeight currentWeight = weight;

      if (weight == TeXFontWeight.INHERIT)
      {
         currentWeight = settings.getFontWeight();
      }

      int fontSize = deriveSize(parser);

      String fontName = name;

      if (name == null)
      {
         switch (currentFamily)
         {
            case SF:
               fontName = "SansSerif";
            break;
            case TT:
            case VERB:
               fontName = "Monospace";
            break;
            default:
               fontName = "Serif";
         }
      }

      int fontStyle = Font.PLAIN;

      if (currentShape == TeXFontShape.IT || currentShape == TeXFontShape.SL)
      {
         if (currentWeight.isBold())
         {
            fontStyle = Font.BOLD | Font.ITALIC;
         }
         else
         {
            fontStyle = Font.ITALIC;
         }
      }
      else if (currentWeight.isBold())
      {
         fontStyle = Font.BOLD;
      }

      return new Font(name, fontStyle, fontSize);
   }

   public String getCss(TeXParser parser)
    throws TeXSyntaxException
   {
      TeXSettings settings = parser.getSettings();

      StringBuilder builder = new StringBuilder();

      if (family != TeXFontFamily.INHERIT)
      {
         builder.append("font-family:");

         if (name != null)
         {
            if (name.matches("[^\\p{IsAlphabetic}]"))
            {
               builder.append(" '"+name+"'");
            }
            else
            {
               builder.append(name);
            }
         }

         switch (family)
         {
            case RM:
               builder.append(" serif");
            break;
            case SF:
               builder.append(" sans-serif");
            break;
            case TT:
            case VERB:
               builder.append(" monospace");
            break;
            case CAL:
               builder.append(" cursive");
            break;
         }

         builder.append("; ");
      }

      switch (shape)
      {
         case UP:
            builder.append("font-style: normal; font-variant: normal; ");
         break;
         case IT:
            builder.append("font-style: italic; font-variant: normal; ");
         break;
         case SL:
            builder.append("font-style: oblique; font-variant: normal; ");
         break;
         case EM:
            TeXFontFamily parentFamily = settings.getFontFamily();
            TeXFontShape parentShape = settings.getFontShape();

            if (parentShape == TeXFontShape.IT || parentShape == TeXFontShape.SL)
            {
               builder.append("font-style: normal; font-variant: normal; ");
            }
            else if (parentFamily == TeXFontFamily.SF)
            {
               builder.append("font-style: oblique; font-variant: normal; ");
            }
            else
            {
               builder.append("font-style: italic; font-variant: normal; ");
            }
         break;
         case SC:
            builder.append("font-style: normal; font-variant: small-caps; ");
         break;
      }

      if (weight.isBold())
      {
         builder.append("font-weight: bold; ");
      }
      else
      {
         builder.append("font-weight: normal; ");
      }

      switch (size)
      {
         case INHERIT:
         break;
         case USER:
            if (userSize != null)
            {
               TeXUnit unit = userSize.getUnit();
               builder.append(String.format("font-size: %fpt; ",
                 unit.toUnit(parser, userSize.getValue(), TeXUnit.BP)));
            }
         break;
         case NORMAL:
            builder.append("font-size: medium; ");
         break;
         case SMALL:
            builder.append("font-size: small; ");
         break;
         case FOOTNOTE:
            builder.append("font-size: x-small; ");
         break;
         case SCRIPT:
            builder.append("font-size: xx-small; ");
         break;
         case LARGE:
            builder.append("font-size: large; ");
         break;
         case XLARGE:
            builder.append("font-size: x-large; ");
         break;
         case XXLARGE:
            builder.append("font-size: xx-large; ");
         break;
         case SMALLER:
            builder.append("font-size: smaller; ");
         break;
         case LARGER:
            builder.append("font-size: larger; ");
         break;
         default:
            builder.append(String.format("font-size: %fpt; ", deriveSize(parser)));
      }

      return builder.toString();
   }

   private String name;
   private TeXFontFamily family = TeXFontFamily.INHERIT;
   private TeXFontShape shape = TeXFontShape.INHERIT;
   private TeXFontWeight weight = TeXFontWeight.INHERIT;
   private TeXFontSize size = TeXFontSize.INHERIT;
   private TeXDimension userSize=null;

   private static final double PT_TO_BP = 72/72.27;
}

