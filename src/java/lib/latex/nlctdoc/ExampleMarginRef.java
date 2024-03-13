/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

public class ExampleMarginRef extends Command
{
   public ExampleMarginRef()
   {
      this("examplemarginref");
   }

   public ExampleMarginRef(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ExampleMarginRef(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String label = popLabelString(parser, stack);

      TeXObject ref = listener.getReference(label);

      TeXObjectList expanded = listener.createStack();

      expanded.add(listener.getControlSequence("marginpar"));

      Group grp = listener.createGroup();
      expanded.add(grp);

      grp.add(listener.createAnchor(label+"-backref",
        listener.getControlSequence("codesym")));

      TeXObjectList list = listener.createStack();

      if (ref == null)
      {
         ref = listener.createUnknownReference(label);
      }

      list.add(ref, true);

      grp.add(listener.createLink(label, list));

      return expanded;
   }
}
