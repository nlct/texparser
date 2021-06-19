/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

// A marker representing the end of a group
public class GroupMarker extends StackMarker
{
   public GroupMarker()
   {
      super();
   }

   protected GroupMarker(long markerIdx)
   {
      super(markerIdx);
   }

   @Override
   public TeXObject expandMarker(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return this;
   }

   @Override
   public void expandAfter(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      // this is the first token following \expandafter. Pop the
      // next token and push both back

      TeXObject secondArg = parser.popNextTokenResolveReference(stack, 
         PopStyle.DEFAULT);

      if (secondArg instanceof StackMarker)
      {
         secondArg = ((StackMarker)secondArg).expandMarker(parser, stack);
      }
      else
      {
         secondArg = parser.expandOnce(secondArg, stack);
      }

      stack.push(secondArg);
      stack.push(this);
   }

   @Override
   public Object clone()
   {
      return new GroupMarker(getMarkerIndex());
   }
}

