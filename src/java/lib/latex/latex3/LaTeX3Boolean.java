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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

import com.dickimawbooks.texparserlib.*;

/**
 * Class representing a LaTeX3 boolean.
 * LaTeX3 booleans are implemented as a command that expands to
 * <code>\char"0</code> (false) or <code>\char"1</code> (true).
 * Most of the methods are unlikely to be needed as the internal
 * workings should be hidden.
 */
public class LaTeX3Boolean extends Command
  implements TeXBoolean,TeXNumber,InternalQuantity
{
   public LaTeX3Boolean(String name)
   {
      this(name, false);
   }

   public LaTeX3Boolean(String name, boolean initVal)
   {
      super(name);
      value = initVal;
   }

   @Override
   public Object clone()
   {
      return new LaTeX3Boolean(getName(), value);
   }

   @Override
   public boolean booleanValue()
   {
      return value;
   }

   @Override
   public int getValue()
   {
      return value ? 1 : 0;
   }

   @Override
   public double doubleValue()
   {
      return (double)getValue();
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return getValue();
   }

   /**
    * Incrementing doesn't make much sense for a boolean value, but
    * method needs to be provided.
    */
   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      value = (getValue() + increment.number(parser) > 0);
   }

   /**
    * Dividing doesn't make much sense for a boolean value, but
    * method needs to be provided. Does nothing.
    */
   @Override
   public void divide(int divisor)
   {
   }

   /**
    * Multiplying doesn't make much sense for a boolean value, but
    * method needs to be provided.
    */
   @Override
   public void multiply(int factor)
   {
      value = (getValue() * factor > 0);
   }

   @Override
   public void setQuantity(TeXParser parser, TeXObject quantity)
    throws TeXSyntaxException
   {
      try
      {
         if (quantity instanceof Numerical)
         {
            value = ((Numerical)quantity).number(parser) > 0;
         }
         else if (parser.isStack(quantity))
         {
            Numerical num = TeXParserUtils.popNumericalArg(parser, (TeXObjectList)quantity);
            value = num.number(parser) > 0;
         }
         else
         {
            String str = parser.expandToString(quantity, parser);
            value = str.equals("0");
         }
      }
      catch (IOException e)
      {
         throw new TeXSyntaxException(e, parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, quantity);
      }
   }

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return value ? UserNumber.ONE : UserNumber.ZERO;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   {
      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(new TeXCsRef("char"));
      expanded.add(parser.getListener().getOther('"'));
      expanded.add(parser.getListener().getOther(value ? '1' : '0'));

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   {
      return parser.getListener().createString(value ? "1" : "0");
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   {
      return expandfully(parser);
   }

   protected boolean value=false;
}
