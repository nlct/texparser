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

public class CountRegister extends Register implements TeXNumber
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

   public void setValue(TeXParser parser, Numerical numerical)
    throws TeXSyntaxException
   {
      setValue(numerical.number(parser));
   }

   public int getValue()
   {
      return value;
   }

   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return value;
   }

   public TeXObject the(TeXParser parser)
   {
      return parser.string(""+value);
   }

   public void advance()
   {
      value++;
   }

   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      value += increment.number(parser);
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
      TeXObject object = parser.popStack(parser, true);

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser);

         if (expanded != null)
         {
            parser.addAll(expanded);
            object = parser.popStack(parser, true);
         }
      }

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == '=')
      {
         object = parser.popStack(parser, true);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser);

            if (expanded != null)
            {
               parser.addAll(expanded);
               object = parser.popStack(parser, true);
            }
         }
      }

      if (object instanceof Register)
      {
         value = ((Register)object).number(parser);
         return;
      }

      parser.push(object);

      TeXNumber num = parser.popNumber();

      value = num.getValue();
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object = stack.popStack(parser, true);

      if (object instanceof Expandable)
      {
         TeXObjectList expanded =
            ((Expandable)object).expandfully(parser, stack);

         if (expanded != null)
         {
            stack.addAll(expanded);
            object = stack.popStack(parser, true);
         }
      }

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == '=')
      {
         object = stack.popStack(parser, true);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded =
               ((Expandable)object).expandfully(parser, stack);

            if (expanded != null)
            {
               stack.addAll(expanded);
               object = stack.popStack(parser, true);
            }
         }
      }

      if (object instanceof Register)
      {
         value = ((Register)object).number(parser);
         return;
      }

      stack.push(object);

      TeXNumber num = stack.popNumber(parser);

      value = num.getValue();
   }

   public Object clone()
   {
      CountRegister reg = new CountRegister(getName(), value);

      reg.allocation = allocation;

      return reg;
   }

   private int value = 0;
}
