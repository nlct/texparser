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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class NumberCs extends Primitive implements Expandable
{
   public NumberCs()
   {
      this("number");
   }

   public NumberCs(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new NumberCs(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      TeXObjectList list = new TeXObjectList();

      if (arg instanceof TeXNumber)
      {
         list.add((TeXNumber)arg);
      }
      else
      {
         list.add(new UserNumber(arg.number(parser)));
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      Numerical arg = parser.popNumerical();

      TeXObjectList list = new TeXObjectList();

      if (arg instanceof TeXNumber)
      {
         list.add((TeXNumber)arg);
      }
      else
      {
         list.add(new UserNumber(arg.number(parser)));
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      return parser.getListener().createString(
         String.format("%d", arg.number(parser)));
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      Numerical arg = parser.popNumerical();

      return parser.getListener().createString(
         String.format("%d", arg.number(parser)));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      parser.getListener().getWriteable().write(
         String.format("%d", arg.number(parser)));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      Numerical arg = parser.popNumerical();

      parser.getListener().getWriteable().write(
         String.format("%d", arg.number(parser)));
   }


}
