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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class L2HSpacer extends Spacer
{
   public L2HSpacer(Direction direction, TeXDimension size)
   {
      super(direction, size);
   }

   public L2HSpacer(Direction direction, TeXDimension size, boolean inline)
   {
      super(direction, size, inline);
   }

   @Override
   public Object clone()
   {
      return new L2HSpacer(direction, 
        size == null ? null : (TeXDimension)size.clone(), isInLine());
   }


   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      parser.getListener().getWriteable().writeliteral("<div style=\"");

      if (direction == Direction.HORIZONTAL)
      {
         parser.getListener().getWriteable().writeliteral(
           "width: " + getSize().format() + "; ");
      }
      else
      {
         parser.getListener().getWriteable().writeliteral(
           "height: " + getSize().format() + "; ");
      }

      if (isInLine())
      {
         parser.getListener().getWriteable().writeliteral("display: inline-block; ");
      }

      parser.getListener().getWriteable().writeliteral("\"></div>");
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      process(parser);
   }
}

