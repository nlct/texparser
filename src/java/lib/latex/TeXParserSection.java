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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class TeXParserSection extends ControlSequence
{
   public TeXParserSection()
   {
      this("texparser@section", "section");
   }

   public TeXParserSection(String name, String sectionCsname)
   {
      super(name);
      this.sectionCsname = sectionCsname;
   }

   @Override
   public Object clone()
   {
      return new TeXParserSection(getName(), sectionCsname);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject title = popArg(parser, stack);
      String label = popLabelString(parser, stack);

      TeXParserListener listener = parser.getListener();
      TeXObjectList substack = listener.createStack();

      substack.add(listener.getControlSequence(sectionCsname));
      substack.add(listener.getOther('*'));
      substack.add(listener.getControlSequence("label"));
      substack.add(listener.createGroup(label));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   protected String sectionCsname;
}
