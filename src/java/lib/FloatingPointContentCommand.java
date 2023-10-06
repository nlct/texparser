/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

/*
 * A command that simply expands to a floating point number.
 */
public class FloatingPointContentCommand extends TextualContentCommand
 implements TeXNumber
{
   public FloatingPointContentCommand(String name, double num)
   {
      this(name, num, false);
   }

   public FloatingPointContentCommand(String name, double num, boolean isConstant)
   {
      this(name, ""+num, new TeXFloatingPoint(num), isConstant);
   }

   protected FloatingPointContentCommand(String name, String text, TeXFloatingPoint num)
   {
      this(name, text, num, false);
   }

   protected FloatingPointContentCommand(String name, String text, TeXFloatingPoint num, boolean isConstant)
   {
      super(name, text, num);
      this.isConstant = isConstant;
   }

   @Override
   public Object clone()
   {
      return isConstant ? this :
        new FloatingPointContentCommand(getName(), getText(),
          (TeXFloatingPoint)getNumber().clone());
   }

   @Override
   public TextualContentCommand duplicate(String newcsname)
   {
      return new FloatingPointContentCommand(newcsname, getText(),
         new TeXFloatingPoint(getValue()), false);
   }

   @Override
   public int getValue()
   {
      return getNumber().getValue();
   }

   @Override
   public double doubleValue()
   {
      return getNumber().doubleValue();
   }

   public void setValue(int val)
   {
      setValue((double)val);
   }

   public void setValue(double val)
   {
      if (!isConstant)
      {
         text = ""+val;
         getNumber().setValue(val);
      }
   }

   public TeXFloatingPoint getNumber()
   {
      return (TeXFloatingPoint)data;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return getNumber().number(parser);
   }

   @Override
   public void multiply(int factor)
   {
      if (!isConstant)
      {
         getNumber().multiply(factor);
         text = ""+getValue();
      }
   }

   @Override
   public void divide(int divisor)
   {
      if (!isConstant)
      {
         getNumber().divide(divisor);
         text = ""+getValue();
      }
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      if (isConstant)
      {
         throw new TeXSyntaxException(parser,
          TeXSyntaxException.ERROR_CANT_CHANGE_CONSTANT, toString(parser));
      }
      else
      {
         getNumber().advance(parser, increment);
         text = ""+getValue();
      }
   }

   protected boolean isConstant = false;
}
