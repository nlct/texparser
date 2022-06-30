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
import java.io.File;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HAtStartToc extends ControlSequence
{
   public L2HAtStartToc()
   {
      this("@starttoc");
   }

   public L2HAtStartToc(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HAtStartToc(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      String ext = popLabelString(parser, stack);

      stack.push(new HtmlTag(String.format("<!-- end of %s -->", ext)));
      stack.push(new EndElement("div"));
      stack.push(listener.getControlSequence("endgroup"));

      File tocFile = listener.getAuxFile(ext);

      if (tocFile.exists())
      {
         stack.push(TeXParserActionObject.createInputAction(tocFile));
      }

      stack.push(listener.getControlSequence("makeatletter"));
      stack.push(listener.getControlSequence("begingroup"));
      StartElement elem = new StartElement("div");
      elem.putAttribute("class", ext);
      stack.push(elem);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
