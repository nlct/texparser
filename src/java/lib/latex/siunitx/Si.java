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
package com.dickimawbooks.texparserlib.latex.siunitx;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

// Fairly primitive approximation
public class Si extends ControlSequence
{
   public Si(SIunitxSty sty)
   {
      this("si", sty);
   }

   public Si(String name, SIunitxSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new Si(getName(), sty);
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject optArg = parser.popNextArg('[', ']');

      sty.parseUnit(parser, parser.popNextArg()).process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject optArg = stack.popArg(parser, '[', ']');

      sty.parseUnit(parser, stack.popArg(parser)).process(parser, stack);
   }


   private SIunitxSty sty;
}
