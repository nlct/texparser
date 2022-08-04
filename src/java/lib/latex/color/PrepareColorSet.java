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
package com.dickimawbooks.texparserlib.latex.color;

import java.io.IOException;
import java.util.Vector;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class PrepareColorSet extends ControlSequence
{
   public PrepareColorSet(ColorSty sty)
   {
      this(sty, "definecolor");
   }

   public PrepareColorSet(ColorSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new PrepareColorSet(sty, getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      String type = popOptLabelString(parser, stack);
      String model = popLabelString(parser, stack).trim();
      TeXObject head = popArg(parser, stack);
      TeXObject tail = popArg(parser, stack);
      String set = popLabelString(parser, stack).trim();

      String[] specs = set.split(" *; *");

      for (String spec : specs)
      {
         String[] params = spec.split(" *, *", 1);

         Color color = sty.getColor(parser, model, params[1]);

         sty.putColor(params[0], color);
      }
   }

   private ColorSty sty;
}
