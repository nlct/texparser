/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class MakeDFHeaderFooter extends ControlSequence
{
   public MakeDFHeaderFooter(FlowFramSty sty)
   {
      this("makedfheaderfooter", sty);
   }

   public MakeDFHeaderFooter(String name, FlowFramSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new MakeDFHeaderFooter(getName(), sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      DimenRegister width = parser.getDimenRegister("typeblockwidth");
      DimenRegister height = parser.getDimenRegister("headheight");
      UserDimension posX = new UserDimension();

      DimenRegister th = parser.getDimenRegister("typeblockheight");
      DimenRegister sep = parser.getDimenRegister("headsep");

      UserDimension posY = new UserDimension();
      posY.advance(parser, th);
      posY.advance(parser, sep);

      sty.newDynamicFrame("header", false, width, height, posX, posY);

      sep = parser.getDimenRegister("footskip");
      posY = new UserDimension(-sep.getValue(), sep.getUnit());

      sty.newDynamicFrame("footer", false, width, height, posX, posY);

   }

   FlowFramSty sty;
}
