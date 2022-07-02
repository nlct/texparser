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

public class GlsXtrTitledNameRefLink extends Command
{
   public GlsXtrTitledNameRefLink()
   {
      this("glsxtrtitlednamereflink");
   }

   public GlsXtrTitledNameRefLink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrTitledNameRefLink(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String format = popLabelString(parser, stack);
      TeXObject loc = popArg(parser, stack);
      TeXObject title = popArg(parser, stack);
      TeXObject file = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      expanded.add(listener.getControlSequence("glsxtrnamereflink"));
      expanded.add(listener.createGroup(format));
      expanded.add(TeXParserUtils.createGroup(listener, loc));
      expanded.add(listener.getControlSequence("glsxtrrecentanchor"));
      expanded.add(TeXParserUtils.createGroup(listener, file));

      return expanded;
   }

}
