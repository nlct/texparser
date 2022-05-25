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

public class Skip extends Primitive implements Expandable,SpacingObject
{
   public Skip(String name, Direction direction)
   {
      super(name);
      this.direction = direction;
   }

   @Override
   public Object clone()
   {
      return new Skip(getName(), direction);
   }

   @Override
   public TeXDimension getSize(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (parser == stack || stack == null)
      {
         return stack.popDimension(parser);
      }
      else
      {
         return parser.popDimension();
      }
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXDimension dim = stack.popDimension(parser);

      Spacer spacer = parser.getListener().getSpacer(direction, dim,
       direction == Direction.HORIZONTAL);

      TeXObjectList list = parser.getListener().createStack();
      list.add(spacer);

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXDimension dim = parser.popDimension();

      Spacer spacer = parser.getListener().getSpacer(direction, dim,
       direction == Direction.HORIZONTAL);

      TeXObjectList list = parser.getListener().createStack();
      list.add(spacer);

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXDimension dim = stack.popDimension(parser);

      Spacer spacer = parser.getListener().getSpacer(direction, dim,
       direction == Direction.HORIZONTAL);

      spacer.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      TeXDimension dim = parser.popDimension();

      Spacer spacer = parser.getListener().getSpacer(direction, dim,
       direction == Direction.HORIZONTAL);

      spacer.process(parser);
   }

   public Direction getDirection()
   {
      return direction;
   }

   protected Direction direction;
}
