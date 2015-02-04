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

public class DimenRegister extends Register implements TeXDimension
{
   public DimenRegister(String name)
   {
      this(name, 0);
   }

   public DimenRegister(String name, int value)
   {
      super(name);
      setValue(value);
   }

   public void setValue(int spValue)
   {
      this.value = spValue;
   }

   public float getValue()
   {
      return (float)value;
   }

   public int number()
   {
      return value;
   }

   public TeXObject the(TeXParser parser)
   {
      return parser.string(""+((float)value/65536.0f)+"pt");
   }

   public void advance()
   {
      advance(1);
   }

   public void advance(int increment)
   {
      value += increment;
   }

   public void divide(int divisor)
   {
      value /= divisor;
   }

   public void multiply(int factor)
   {
      value *= factor;
   }

   public void process(TeXParser parser)
      throws IOException
   {
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
   }

   public Object clone()
   {
      return new DimenRegister(getName(), value);
   }

   public TeXUnit getUnit()
   {
      return unit;
   }

   private int value = 0;

   // TODO find some way to implement em and ex
   private static TeXUnit unit = new TeXUnit(TeXUnit.UNIT_SP);
}
