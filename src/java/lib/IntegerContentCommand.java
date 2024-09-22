/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
 * A command that simply expands to an integer.
 */
public class IntegerContentCommand extends TextualContentCommand implements TeXNumber
{
   public IntegerContentCommand(String name, int num)
   {
     this(name, num, false);
   }

   public IntegerContentCommand(String name, int num, boolean isConstant)
   {
      this(name, ""+num, new UserNumber(num), isConstant);
   }

   protected IntegerContentCommand(String name, String text, UserNumber num)
   {
      this(name, text, num, false);
   }

   protected IntegerContentCommand(String name, String text, UserNumber num, boolean isConstant)
   {
      super(name, text, num);
      this.isConstant = isConstant;
   }

   @Override
   public Object clone()
   {
      return isConstant ? this :
        new IntegerContentCommand(getName(), getText(),
          (UserNumber)getNumber().clone());
   }

   @Override
   public TextualContentCommand duplicate(String newcsname)
   {
      return new IntegerContentCommand(newcsname, getText(),
         new UserNumber(getValue()), false);
   }

   @Override
   public int getValue()
   {
      return getNumber().getValue();
   }

   @Override
   public long longValue()
   {
      return (long)getValue();
   }

   @Override
   public double doubleValue()
   {
      return (double)getValue();
   }

   public void setValue(int val)
   {
      if (!isConstant)
      {
         text = ""+val;
         getNumber().setValue(val);
      }
   }

   public UserNumber getNumber()
   {
      return (UserNumber)data;
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
