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

public class StackMarker extends Ignoreable
{
   public StackMarker()
   {
      index = ++lastIndex;
   }

   private StackMarker(long index)
   {
      this.index = index;
      this.lastIndex = index;
   }

   // Does nothing
   public void process(TeXParser parser)
      throws IOException
   {
   }

   public void process(TeXParser parser, TeXObjectList stack) 
      throws IOException
   {
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return new TeXObjectList();
   } 

   public boolean isPar()
   {
      return false;
   }

   public String toString(TeXParser parser)
   {
      return "";
   }

   public String format()
   {
      return "";
   }

   public String toString()
   {
      return String.format("%s[index=%d]",
        getClass().getSimpleName(), index);
   }

   public Object clone()
   {
      return new StackMarker(index);
   }

   public boolean equals(Object object)
   {
      if (object == null || !(object instanceof StackMarker))
      {
        return false;
      }

      return index == ((StackMarker)object).index;
   }

   private long index=-1;
   private static long lastIndex=-1;
}

