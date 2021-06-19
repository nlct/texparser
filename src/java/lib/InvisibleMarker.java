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

public class InvisibleMarker extends StackMarker
{
   public InvisibleMarker()
   {
      super();
   }

   protected InvisibleMarker(long markerIndex)
   {
      super(markerIndex);
   }

   @Override
   public TeXObject expandMarker(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();
      expanded.add(this);

      TeXObject nextToken = parser.popNextTokenResolveReference(stack, 
         PopStyle.DEFAULT);

      if (nextToken instanceof StackMarker)
      {
         nextToken = ((StackMarker)nextToken).expandMarker(parser, stack);
      }
      else
      {
         nextToken = parser.expandOnce(nextToken, stack);
      }

      if (nextToken instanceof TeXObjectList)
      {
         expanded.addAll((TeXObjectList)nextToken);
      }

      return expanded;
   }

   public void expandAfter(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      // This is the first token following \expandafter but it
      // should be skipped so pop next two tokens and push
      // everything back.

      TeXObject firstArg = parser.popNextTokenResolveReference(stack, 
         PopStyle.DEFAULT);

      if (firstArg instanceof StackMarker)
      {
         ((StackMarker)firstArg).expandAfter(parser, stack);
         stack.push(this);
         return;
      }

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
      stack.push(firstArg);
      stack.push(this);
   }

   @Override
   public Object clone()
   {
      return new InvisibleMarker(getMarkerIndex());
   }
}

