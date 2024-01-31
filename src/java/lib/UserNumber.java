/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public class UserNumber extends AbstractTeXObject implements TeXNumber
{
   public UserNumber()
   {
      this(0);
   }

   public UserNumber(int num)
   {
      value = num;
   }

   public UserNumber(TeXParser parser, String string)
     throws TeXSyntaxException
   {
      try
      {
         value = Integer.parseInt(string);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, string);
      }
   }

   public UserNumber(TeXParser parser, String string, int base)
     throws TeXSyntaxException
   {
      try
      {
         value = Integer.parseInt(string, base);
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
      return new UserNumber(value);
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return value;
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

   public void setValue(int newValue)
   {
      value = newValue;
   }

   public void setValue(float newValue)
   {
      setValue((int)newValue);
   }

   @Override
   public void multiply(int factor)
   {
      setValue(value * factor);
   }

   @Override
   public void divide(int divisor)
   {
      setValue(value / divisor);
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      setValue(value + increment.number(parser));
   }

   @Override
   public String format()
   {
      return ""+value;
   }

   @Override
   public String toString()
   {
      return String.format("%s[value=%d]",
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

   private int value;

   public static final UserNumber MINUS_ONE = new UserNumber(-1);
   public static final UserNumber ZERO = new UserNumber(0);
   public static final UserNumber ONE = new UserNumber(1);
   public static final UserNumber TWO = new UserNumber(2);
   public static final UserNumber THREE = new UserNumber(3);
   public static final UserNumber FOUR = new UserNumber(4);
   public static final UserNumber FIVE = new UserNumber(5);
}
