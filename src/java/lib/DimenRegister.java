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

   public DimenRegister(String name, int spValue)
   {
      this(name, spValue, FixedUnit.SP);
   }

   public DimenRegister(String name, float value, TeXUnit unit)
   {
      super(name);
      setValue(value, unit);
   }

   public void setValue(float value, TeXUnit unit)
   {
      this.value = value;
      this.unit = unit;
   }

   public void setValue(TeXParser parser, Numerical numerical)
    throws TeXSyntaxException
   {
      if (!(numerical instanceof TeXDimension))
      {
         setValue(numerical.number(parser), FixedUnit.SP);

         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_DIMEN_EXPECTED, 
           numerical.toString(parser));
      }

      TeXDimension dimen = (TeXDimension)numerical;
      setValue(dimen.getValue(), dimen.getUnit());
   }

   public float getValue()
   {
      return value;
   }

   public TeXUnit getUnit()
   {
      return unit;
   }

   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return unit.toSp(parser, value);
   }

   public TeXObject the(TeXParser parser)
    throws TeXSyntaxException
   {
      return parser.string(String.format("%fpt", unit.toPt(parser, value)));
   }

   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      if (!(increment instanceof TeXDimension))
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_DIMEN_EXPECTED,
           increment.toString(parser));
      }

      TeXDimension dimen = (TeXDimension)increment;

      TeXUnit otherUnit = dimen.getUnit();

      if (unit.equals(otherUnit))
      {
         value += dimen.getValue();
         return;
      }

      if (!(unit instanceof FixedUnit))
      {
         if (otherUnit instanceof FixedUnit)
         {
            // if this unit isn't fixed but the other is,
            // convert to other unit

            value = otherUnit.fromUnit(parser, value, unit)
                  + dimen.getValue();
            unit = dimen.getUnit();
            return;
         }

         // neither unit are fixed, but they're not the same unit,
         // so convert to pt

         value = unit.toPt(parser, value);
         unit = FixedUnit.PT;
      }

      value += unit.toUnit(parser, dimen.getValue(), otherUnit);
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
      return new DimenRegister(getName(), value, unit);
   }

   private float value = 0f;

   private TeXUnit unit;
}
