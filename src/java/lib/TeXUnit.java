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

public abstract class TeXUnit implements TeXObject
{
   // convert value in other unit to this unit
   public abstract float toUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException;

   // convert value in this unit to other unit
   public abstract float fromUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException;

   public abstract Object clone();

   public float toPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return toUnit(parser, value, FixedUnit.PT);
   }

   public float fromPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return fromUnit(parser, value, FixedUnit.PT);
   }

   public int toSp(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return (int)toUnit(parser, value, FixedUnit.SP);
   }

   public float fromSp(TeXParser parser, int value)
      throws TeXSyntaxException
   {
      return fromUnit(parser, value, FixedUnit.SP);
   }

   public boolean isPar()
   {
      return false;
   }

   public static float muToEm(float muValue)
   {
      return muValue/18f;
   }

   public static float emToMu(float emValue)
   {
     return 18f*emValue;
   }

   public String toString()
   {
      return String.format("%s[unit=%s]", getClass().getSimpleName(),
        format());
   }

   public static final FixedUnit PT = new FixedUnit(FixedUnit.UNIT_PT);
   public static final FixedUnit SP = new FixedUnit(FixedUnit.UNIT_SP);
   public static final FixedUnit BP = new FixedUnit(FixedUnit.UNIT_BP);
   public static final EmUnit EM = new EmUnit();
   public static final ExUnit EX = new ExUnit();
   public static final MuUnit MU = new MuUnit();
   public static final FillUnit FIL = new FillUnit(1);
   public static final FillUnit FILL = new FillUnit(2);
   public static final FillUnit FILLL = new FillUnit(3);
}
