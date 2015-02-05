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

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

public class UserDimension implements TeXDimension
{
   public UserDimension()
   {
      this(0, FixedUnit.PT);
   }

   public UserDimension(float num, TeXUnit texUnit)
   {
      value = num;
      unit = texUnit;
   }

   public UserDimension(Float num, TeXUnit texUnit)
   {
      this(num.floatValue(), texUnit);
   }

   public UserDimension(TeXNumber num, TeXUnit texUnit)
   {
      value = num.getValue();
      unit = texUnit;
   }

   public UserDimension(TeXParser parser, String string)
    throws TeXSyntaxException
   {
      Matcher m = DIMENSION_PATTERN.matcher(string);

      if (!m.matches())
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED, string);
      }

      String valueString = m.group(1);
      String unitString = m.group(2);

      try
      {
         value = Float.parseFloat(valueString);
      }
      catch (NumberFormatException e)
      {
         // this shouldn't happen
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED, string);
      }

      unit = parser.getListener().createUnit(unitString);
   }

   public Object clone()
   {
      return new UserDimension(value, unit);
   }

   public int number(TeXParser parser)
     throws TeXSyntaxException
   {
      return (int)unit.toSp(parser, value);
   }

   public float getValue()
   {
      return value;
   }

   public TeXUnit getUnit()
   {
      return unit;
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

   public String toString(TeXParser parser)
   {
      return String.format("%f%s", value, unit.toString(parser));
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   public void process(TeXParser parser) throws IOException
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

   private float value;

   private TeXUnit unit;

   public static final Pattern DIMENSION_PATTERN 
     = Pattern.compile("\\s*(\\d*(?:\\.\\d+)?\\d)\\s*([a-z]{2})\\s*");
}
