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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class XrSectionRef extends ControlSequence
{
   public XrSectionRef()
   {
      this("xrsectionref");
   }

   public XrSectionRef(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new XrSectionRef(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String anchor = popLabelString(parser, stack);
      String filename = popLabelString(parser, stack)+".html";
      TeXObject title = popArg(parser, stack);

      TeXObjectList content = listener.createStack();

      TeXObject ref = listener.getReference(anchor);

      content.add(listener.getControlSequence("href"));
      content.add(listener.createGroup(filename+"#"+anchor));

      Group grp = listener.createGroup();
      content.add(grp);

      if (ref != null)
      {
         grp.add(listener.getOther(0xA7));
         grp.add(ref);
         grp.add(listener.getSpace());
      }

      grp.add(title);

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
