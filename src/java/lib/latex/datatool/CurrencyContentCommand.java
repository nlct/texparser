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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/*
 * A command that simply expands to a currency value.
 */
public class CurrencyContentCommand extends Command
  implements TeXNumber
{
   public CurrencyContentCommand(String name, TeXObject symbol, double value)
   {
      super(name);
      this.symbol = symbol;
      this.value = value;
   }

   @Override
   public Object clone()
   {
      return new CurrencyContentCommand(name, (TeXObject)symbol.clone(), value);
   }

   @Override
   public double doubleValue()
   {
      return value;
   }

   @Override
   public int getValue()
   {
      return (int)value;
   }

   public void setValue(double val)
   {
      this.value = val;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return getValue();
   }

   @Override
   public void multiply(int factor)
   {
      value = value * factor;
   }

   @Override
   public void divide(int divisor)
   {
      value = value / divisor;
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      value += increment.number(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add((TeXObject)symbol.clone(), true);
      expanded.add(new TeXFloatingPoint(value));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add((TeXObject)symbol.clone(), true);
      expanded.addAll(parser.getListener().createString(""+value));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public String toString()
   {
      return String.format("%s[name=%s,symbol=%s,value=%f]",
       getName(), symbol.toString(), value);
   }

   private TeXObject symbol;
   private double value;
}
