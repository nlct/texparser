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

public class TeXGlue implements TeXDimension
{
   public TeXGlue()
   {
      this(new UserDimension());
   }

   public TeXGlue(TeXDimension dimen)
   {
      setFixed(dimen);
   }

   public TeXGlue(TeXDimension dimen, TeXDimension plus, TeXDimension minus)
   {
      setFixed(dimen);
      setStretch(plus);
      setShrink(minus);
   }

   public Object clone()
   {
      TeXGlue glue = new TeXGlue((TeXDimension)dimen.clone());

      glue.stretch = (stretch == null ? null : (TeXDimension)stretch.clone());
      glue.shrink = (shrink == null ? null : (TeXDimension)shrink.clone());

      return glue;
   }

   public float getValue()
   {
      return dimen.getValue();
   }

   public TeXUnit getUnit()
   {
      return dimen.getUnit();
   }

   public void setValue(TeXDimension dimen)
   {
      this.dimen = dimen;

      if (dimen instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)dimen;

         this.shrink = glue.shrink;
         this.stretch = glue.stretch;
      }
      else
      {
         this.shrink = null;
         this.stretch = null;
      }
   }

   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      dimen.advance(parser, increment);

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
      dimen.divide(divisor);

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
      dimen.multiply(factor);

      if (shrink != null)
      {
         shrink.multiply(factor);
      }

      if (stretch != null)
      {
         stretch.multiply(factor);
      }
   }

   public void setFixed(TeXDimension dimen)
   {
      if (dimen instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)dimen;

         this.dimen = glue.dimen;

         if (glue.stretch != null)
         {
            stretch = glue.stretch;
         }

         if (glue.shrink != null)
         {
            shrink = glue.shrink;
         }

         return;
      }

      this.dimen = dimen;
   }

   public int number(TeXParser parser)
    throws TeXSyntaxException
   {
      return dimen.number(parser);
   }

   public String toString(TeXParser parser)
   {
      String str = dimen.toString(parser);

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

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.getListener().getWriteable().write(toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
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

   public void setShrink(TeXDimension shrink)
   {
      if (shrink instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)shrink;
         this.shrink = glue.dimen;
         return;
      }

      this.shrink = shrink;
   }

   public void setStretch(TeXDimension stretch)
   {
      if (stretch instanceof TeXGlue)
      {
         TeXGlue glue = (TeXGlue)stretch;
         this.stretch = glue.dimen;
         return;
      }

      this.stretch = stretch;
   }

   public TeXDimension getFixed()
   {
      return dimen;
   }

   private TeXDimension dimen, stretch, shrink;
}
