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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

/**
 * Represents a horizontal or vertical space.
 */
public class Spacer implements TeXObject
{
   public Spacer(Direction direction, TeXDimension size)
   {
      this.direction = direction;
      this.size = size;
   }

   @Override
   public String toString()
   {
      return String.format("%s[direction=%s,size=%s]", 
       getClass().getSimpleName(), direction, size);
   }

   public TeXDimension getSize()
   {
      return size;
   }

   public Direction getDirection()
   {
      return direction;
   }


   @Override
   public Object clone()
   {
      return new Spacer(direction, 
        size == null ? null : (TeXDimension)size.clone());
   }

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%s%s %s",
         new String(Character.toChars(parser.getEscChar())),
         direction == Direction.HORIZONTAL ? "hskip" : "vskip",
         size == null ? "0pt" : size.toString(parser)
        );
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return parser.getListener().createString(" ");
   }

   @Override
   public String format()
   {
      return String.format("\\%s %s", 
         direction == Direction.HORIZONTAL ? "hskip" : "vskip",
         size == null ? "0pt" : size.format());
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      if (direction == Direction.HORIZONTAL)
      {
         parser.getListener().getWriteable().write(" ");
      }
      else
      {
         parser.getListener().getWriteable().writeln("");
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (direction == Direction.HORIZONTAL)
      {
         parser.getListener().getWriteable().write(" ");
      }
      else
      {
         parser.getListener().getWriteable().writeln("");
      }
   }

   protected Direction direction;
   protected TeXDimension size;
}

