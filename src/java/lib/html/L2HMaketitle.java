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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HMaketitle extends ControlSequence
{
   public L2HMaketitle()
   {
      this("maketitle");
   }

   public L2HMaketitle(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HMaketitle(getName());
   }

   public void process(TeXParser parser)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      listener.write("<div id=\"title\">");

      ControlSequence cs = listener.getControlSequence("@title");

      cs.process(parser);

      listener.write("</div>");

      listener.write("<div id=\"author\">");

      cs = listener.getControlSequence("@author");

      cs.process(parser);

      listener.write("</div>");

      listener.write("<div id=\"date\">");

      cs = listener.getControlSequence("@date");

      cs.process(parser);

      listener.write("</div>");
   }

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      process(parser);
   }
}
