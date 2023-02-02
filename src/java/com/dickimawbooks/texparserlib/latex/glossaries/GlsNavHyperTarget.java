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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsNavHyperTarget extends ControlSequence
{
   public GlsNavHyperTarget()
   {
      this("glsnavhypertarget");
   }

   public GlsNavHyperTarget(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsNavHyperTarget(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String type = popOptLabelString(parser, stack);
      String grpLabel = popLabelString(parser, stack);
      TeXObject title = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("@glsnavhypertarget"));
      list.add(listener.createGroup(type));
      list.add(listener.createGroup(grpLabel));
      list.add(TeXParserUtils.createGroup(listener, title));

      TeXParserUtils.process(list, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
