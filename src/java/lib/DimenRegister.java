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
import java.util.Vector;

public class DimenRegister extends NumericRegister implements TeXDimension
{
   public DimenRegister(String name)
   {
      this(name, 0, FixedUnit.SP);
   }

   public DimenRegister(String name, float value, TeXUnit unit)
   {
      this(name, new TeXGlue());

      try
      {
         dimension.setDimension(null, new UserDimension(value, unit));
      }
      catch (TeXSyntaxException e)
      {// shouldn't happen
      }
   }

   public DimenRegister(String name, TeXGlue dimension)
   {
      super(name);
      this.dimension = dimension;
   }

   public void setValue(TeXParser parser, Numerical numerical)
    throws TeXSyntaxException
   {
      if (!(numerical instanceof TeXDimension))
      {
         dimension.setDimension(parser,
            new UserDimension(numerical.number(parser), FixedUnit.SP));

         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_DIMEN_EXPECTED, 
           numerical.toString(parser));
      }

      setDimension(parser, (TeXDimension)numerical);
   }

   public void setDimension(TeXParser parser, TeXDimension dimen)
    throws TeXSyntaxException
   {
      dimension.setDimension(parser, dimen);
   }

   public TeXDimension getDimension()
   {
      return dimension;
   }

   public float getValue()
   {
      return dimension.getValue();
   }

   public TeXUnit getUnit()
   {
      return dimension.getUnit();
   }

   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return dimension.number(parser);
   }

   public TeXObject getContents(TeXParser parser)
    throws TeXSyntaxException
   {
      return parser.string(dimension.toString(parser));
   }

   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      dimension.advance(parser, increment);
   }

   public void multiply(int factor)
   {
      dimension.multiply(factor);
   }

   public void multiply(float factor)
   {
      dimension.multiply(factor);
   }

   public void divide(int divisor)
   {
      dimension.divide(divisor);
   }

   @Override
   protected TeXObject popValue(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return stack.popDimension(parser);
   }

   public Object clone()
   {
      return new DimenRegister(getName(), (TeXGlue)dimension.clone());
   }

   public String toString()
   {
      return String.format("%s[name=%s,value=%s]",
       getClass().getSimpleName(), getName(), dimension);
   }


   private TeXGlue dimension;
}
