/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public class ExpandAfter extends Primitive implements Expandable
{
   public ExpandAfter()
   {
      this("expandafter");
   }

   public ExpandAfter(String name)
   {
      super(name, true);
   }

   @Override
   public Object clone()
   {
      return new ExpandAfter(getName());
   }

   protected void pushTo(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      PopStyle popStyle = PopStyle.DEFAULT;

      TeXObject firstArg = parser.popNextTokenResolveReference(stack, popStyle);

      if (firstArg instanceof StackMarker)
      {
         ((StackMarker)firstArg).expandAfter(parser, stack);
         return;
      }

      TeXObject secondArg = parser.popNextTokenResolveReference(stack, popStyle);

      secondArg = parser.expandOnce(secondArg, stack);

      stack.push(secondArg);
      stack.push(firstArg);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      pushTo(parser, stack);

      return new TeXObjectList();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      pushTo(parser, parser);

      return new TeXObjectList();
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      pushTo(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      pushTo(parser, parser);
   }
}
