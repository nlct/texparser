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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

// \active isn't actually a primitive but since its value shouldn't
// change, this seems more suitable
public class Active extends Primitive implements Expandable,TeXNumber
{
   public Active()
   {
      this("active");
   }

   public Active(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Active(getName());
   }

   @Override
   public int getValue()
   {
      return value.getValue();
   }

   @Override
   public long longValue()
   {
      return value.longValue();
   }

   @Override
   public double doubleValue()
   {
      return value.doubleValue();
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
   }

   @Override
   public void divide(int divisor)
   {
   }

   @Override
   public void multiply(int factor)
   {
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return value.number(parser);
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      TeXObjectList list = parser.getListener().createStack();

      list.add(value);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      value.process(parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      value.process(parser);
   }

   private static final UserNumber value = new UserNumber(13);
}
