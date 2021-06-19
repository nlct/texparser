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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public abstract class StackMarker implements TeXObject
{
   public StackMarker()
   {
      index = ++LAST_INDEX;
   }

   protected StackMarker(long index)
   {
      this.index = index;
   }

   public long getMarkerIndex()
   {
      return index;
   }

   public abstract TeXObject expandMarker(TeXParser parser, TeXObjectList stack)
      throws IOException;

   public abstract void expandAfter(TeXParser parser, TeXObjectList stack)
      throws IOException;

   // markers shouldn't be processed
   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_MARKER_FOUND,
        toString());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) 
      throws IOException
   {
      throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_MARKER_FOUND,
        toString());
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      return this.equals(marker);
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return new TeXObjectList();
   } 

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return false;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public String stripToString(TeXParser parser)
     throws IOException
   {
      return "";
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }

   @Override
   public int getTeXCategory()
   {
      return TYPE_OBJECT;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   @Override
   public String format()
   {
      return "";
   }

   @Override
   public String toString()
   {
      return String.format("%s[index=%d]",
        getClass().getSimpleName(), index);
   }

   @Override
   public boolean equals(Object object)
   {
      if (object == null || !(object instanceof StackMarker))
      {
        return false;
      }

      return index == ((StackMarker)object).index;
   }

   @Override
   public abstract Object clone();

   private long index=-1;
   private static long LAST_INDEX=-1;
}

