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
package com.dickimawbooks.texparserlib.latex.tcilatex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

/*
 * This is an attempt to convert the definitions use by Scientific
 * Word export to LaTeX.
 */

public class QDABOVE extends ControlSequence
{
   public QDABOVE()
   {
      this("QDABOVE");
   }

   public QDABOVE(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new QDABOVE(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("genfrac");

      TeXDimension thickness = stack.popDimension(parser);

      stack.push(parser.getListener().getLetter((int)'0'));
      stack.push(thickness);
      stack.push(parser.getListener().createGroup());
      stack.push(parser.getListener().createGroup());
      cs.process(parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("genfrac");

      TeXDimension thickness = parser.popDimension(parser);

      parser.push(parser.getListener().getLetter((int)'0'));
      parser.push(thickness);
      parser.push(parser.getListener().createGroup());
      parser.push(parser.getListener().createGroup());
      cs.process(parser);
   }
}
