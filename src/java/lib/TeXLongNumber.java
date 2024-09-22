/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

public class TeXLongNumber extends AbstractTeXObject implements TeXNumber
{
   public TeXLongNumber()
   {
      this(0L);
   }

   public TeXLongNumber(long num)
   {
      value = num;
   }

   public TeXLongNumber(TeXParser parser, String string)
     throws TeXSyntaxException
   {
      try
      {
         value = Long.parseLong(string);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, string);
      }
   }

   @Override
   public Object clone()
   {
      return new TeXLongNumber(value);
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return getValue();
   }

   @Override
   public int getValue()
   {
      return (int)value;
   }

   @Override
   public double doubleValue()
   {
      return (double)value;
   }

   @Override
   public long longValue()
   {
      return value;
   }

   public void setValue(int newValue)
   {
      value = newValue;
   }

   public void setValue(long newValue)
   {
      value = newValue;
   }

   public void setValue(double newValue)
   {
      value = (long)newValue;
   }

   public void setValue(float newValue)
   {
      value = (long)newValue;
   }

   @Override
   public void multiply(int factor)
   {
      value = value * (long)factor;
   }

   @Override
   public void divide(int divisor)
   {
      value = value / (long)divisor;
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      if (increment instanceof TeXNumber)
      {
         advance((TeXNumber)increment);
      }
      else
      {
         value += increment.number(parser);
      }
   }

   public void advance(TeXNumber num)
    throws TeXSyntaxException
   {
      value += num.longValue();
   }

   public void advance()
   {
      value += 1L;
   }

   public void add(long num)
   {
      value += num;
   }

   public void subtract(long num)
   {
      value -= num;
   }

   public void multiple(long num)
   {
      value *= num;
   }

   public void divide(long num)
   {
      value /= num;
   }

   @Override
   public String format()
   {
      return ""+value;
   }

   @Override
   public String toString()
   {
      return String.format("%s[value=%f]",
         getClass().getSimpleName(), value);
   }

   @Override
   public String toString(TeXParser parser)
   {
      return ""+value;
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(""+value);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   private long value;
}
