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

public class CountRegister extends NumericRegister implements TeXNumber
{
   public CountRegister(String name)
   {
      this(name, 0);
   }

   public CountRegister(String name, int value)
   {
      super(name);
      setValue(value);
   }

   public void setValue(int value)
   {
      this.value = value;
   }

   @Override
   public void setValue(TeXParser parser, Numerical numerical)
    throws TeXSyntaxException
   {
      setValue(numerical.number(parser));
   }

   @Override
   public void setContents(TeXParser parser, int value)
    throws TeXSyntaxException
   {
      setValue(value);
   }

   @Override
   public int getValue()
   {
      return value;
   }

   @Override
   public double doubleValue()
   {
      return (double)value;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return value;
   }

   @Override
   public TeXObject getContents(TeXParser parser)
   {
      return parser.string(""+value);
   }

   public void advance()
   {
      value++;
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      value += increment.number(parser);
   }

   @Override
   public void divide(int divisor)
   {
      value /= divisor;
   }

   @Override
   public void multiply(int factor)
   {
      value *= factor;
   }

   @Override
   public Object clone()
   {
      CountRegister reg = new CountRegister(getName(), value);

      reg.allocation = allocation;

      return reg;
   }

   @Override
   public String toString()
   {
      return String.format("%s[name=%s,value=%d]",
       getClass().getSimpleName(), getName(), value);
   }

   @Override
   public String format()
   {
      return ""+value;
   }

   private int value = 0;
}
