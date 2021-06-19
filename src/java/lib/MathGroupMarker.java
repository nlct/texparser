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

// A marker representing the end of a math group
public class MathGroupMarker extends GroupMarker
{
   public MathGroupMarker(boolean isInLine)
   {
      super();
      this.isInLine = isInLine;
   }

   protected MathGroupMarker(long markerIdx, boolean isInLine)
   {
      super(markerIdx);
      this.isInLine = isInLine;
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
      if (isInLine)
      {
         super.expandAfter(parser, stack);
      }
      else
      {
         stack.push(this);
      }
   }

   @Override
   public String toString()
   {
      return String.format("%s[index=%d,inline=%s]",
        getClass().getSimpleName(), getMarkerIndex(), isInLine);
   }

   @Override
   public Object clone()
   {
      return new MathGroupMarker(getMarkerIndex(), isInLine);
   }

   public boolean isInLine()
   {
      return isInLine;
   }

   private boolean isInLine;
}

