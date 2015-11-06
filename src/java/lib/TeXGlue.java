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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class TeXGlue implements TeXDimension, Expandable
{
   public TeXGlue()
   {
      fixed = new UserDimension();
      shrink = null;
      stretch = null;
   }

   public TeXGlue(TeXParser parser, TeXDimension dimen)
    throws TeXSyntaxException
   {
      fixed = new UserDimension();
      setDimension(parser, dimen);
   }

   public TeXGlue(TeXParser parser, TeXDimension dimen, 
     TeXDimension plus, TeXDimension minus)
   throws TeXSyntaxException
   {
      fixed = new UserDimension();
      setFixed(parser, dimen);
      setStretch(parser, plus);
      setShrink(parser, minus);
   }

   public Object clone()
   {
      TeXGlue glue = new TeXGlue();

      try
      {
         glue.setFixed(null, fixed);
         glue.setShrink(null, shrink);
         glue.setStretch(null, stretch);
      }
      catch (TeXSyntaxException e)
      {// shouldn't happen
      }

      return glue;
   }

   public float getValue()
   {
      return fixed.getValue();
   }

   public TeXUnit getUnit()
   {
      return fixed.getUnit();
   }

   public void setDimension(TeXParser parser, TeXDimension dimen)
     throws TeXSyntaxException
   {
      if (dimen instanceof DimenRegister)
      {
         setDimension(parser, ((DimenRegister)dimen).getDimension());
      }
      else if (dimen instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)dimen;

         this.fixed.setDimension(parser, glue.fixed);

         if (this.shrink == null)
         {
            this.shrink = (glue.shrink == null ? null : 
              (TeXDimension)glue.shrink.clone());
         }
         else if (glue.shrink == null)
         {
            this.shrink = null;
         }
         else
         {
            this.shrink.setDimension(parser, glue.shrink);
         }

         if (this.stretch == null)
         {
            this.stretch = (glue.stretch == null ? null : 
              (TeXDimension)glue.stretch.clone());
         }
         else if (glue.stretch == null)
         {
            this.stretch = null;
         }
         else
         {
            this.stretch.setDimension(parser, glue.stretch);
         }
      }
      else
      {
         setFixed(parser, dimen);
         this.shrink = null;
         this.stretch = null;
      }
   }

   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      fixed.advance(parser, increment);

      if (increment instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)increment;

         if (glue.stretch != null)
         {
            if (stretch == null)
            {
               stretch = (TeXDimension)glue.stretch.clone();
            }
            else
            {
               stretch.advance(parser, glue.stretch);
            }
         }

         if (glue.shrink != null)
         {
            if (shrink == null)
            {
               shrink = (TeXDimension)glue.shrink.clone();
            }
            else
            {
               shrink.advance(parser, glue.shrink);
            }
         }
      }
   }

   public void divide(int divisor)
   {
      fixed.divide(divisor);

      if (shrink != null)
      {
         shrink.divide(divisor);
      }

      if (stretch != null)
      {
         stretch.divide(divisor);
      }
   }

   public void multiply(int factor)
   {
      fixed.multiply(factor);

      if (shrink != null)
      {
         shrink.multiply(factor);
      }

      if (stretch != null)
      {
         stretch.multiply(factor);
      }
   }

   public void multiply(float factor)
   {
      fixed.multiply(factor);

      if (shrink != null)
      {
         shrink.multiply(factor);
      }

      if (stretch != null)
      {
         stretch.multiply(factor);
      }
   }

   public void setFixed(TeXParser parser, TeXDimension dimen)
     throws TeXSyntaxException
   {
      if (dimen instanceof DimenRegister)
      {
         setFixed(parser, ((DimenRegister)dimen).getDimension());
      }
      else if (dimen instanceof TeXGlue)
      {
         setFixed(parser, ((TeXGlue)dimen).getFixed());
      }
      else
      {
         if (dimen.getUnit() instanceof FillUnit)
         {
            throw new TeXSyntaxException(parser,
              TeXSyntaxException.ERROR_MISSING_UNIT, 
              dimen.getUnit().toString());
         }

         fixed.setDimension(parser, dimen);
      }
   }

   public int number(TeXParser parser)
    throws TeXSyntaxException
   {
      return fixed.number(parser);
   }

   public String toString(TeXParser parser)
   {
      String str = fixed.toString(parser);

      if (stretch != null)
      {
         str = str + " plus "+stretch.toString(parser);
      }

      if (shrink != null)
      {
         str = str + " minus "+shrink.toString(parser);
      }

      return str;
   }

   public String format()
   {
      String str = fixed.format();

      if (stretch != null)
      {
         str = str + " plus "+stretch.format();
      }

      if (shrink != null)
      {
         str = str + " minus "+shrink.format();
      }

      return str;
   }

   public String toString()
   {
      return String.format("%s[fixed:%s,stretch=%s,shrink=%s]",
        getClass().getName(), fixed, stretch, shrink);
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return string(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return string(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.addAll(0, string(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.addAll(0, string(parser));
   }

   public boolean isPar()
   {
      return false;
   }

   public TeXDimension getStretch()
   {
      return stretch;
   }

   public TeXDimension getShrink()
   {
      return shrink;
   }

   public void setShrink(TeXParser parser, TeXDimension shrink)
    throws TeXSyntaxException
   {
      if (shrink instanceof DimenRegister)
      {
         setShrink(parser, ((DimenRegister)shrink).getDimension());
      }
      else if (shrink instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)shrink;

         setShrink(parser, glue.fixed);
      }
      else
      {
         if (this.shrink == null)
         {
            if (shrink != null)
            {
               this.shrink = (TeXDimension)shrink.clone();
            }
         }
         else
         {
            this.shrink.setDimension(parser, shrink);
         }
      }
   }

   public void setStretch(TeXParser parser, TeXDimension stretch)
    throws TeXSyntaxException
   {
      if (stretch instanceof DimenRegister)
      {
         setStretch(parser, ((DimenRegister)stretch).getDimension());
      }
      else if (stretch instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)stretch;

         setStretch(parser, glue.fixed);
      }
      else
      {
         if (this.stretch == null)
         {
            if (stretch != null)
            {
               this.stretch = (TeXDimension)stretch.clone();
            }
         }
         else
         {
            this.stretch.setDimension(parser, stretch);
         }
      }
   }

   public TeXDimension getFixed()
   {
      return fixed;
   }

   private TeXDimension fixed, stretch, shrink;
}
