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

public class GlsHyperlink extends Command
{
   public GlsHyperlink()
   {
      this("glshyperlink");
   }

   public GlsHyperlink(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsHyperlink(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      TeXObject linkText = popOptArg(parser, stack);
      String label = popLabelString(parser, stack);

      if (linkText == null)
      {
         linkText = listener.createStack();

         ((TeXObjectList)linkText).add(new TeXCsRef("glsentrytext"));
         ((TeXObjectList)linkText).add(listener.createGroup(label));
      }

      expanded.add(new TeXCsRef("@glslink"));

      Group grp = listener.createGroup();
      expanded.add(grp);

      grp.add(new TeXCsRef("glolinkprefix"));
      grp.add(listener.createString(label), true);

      grp = listener.createGroup();
      expanded.add(grp);

      grp.add(linkText);

      return expanded;
   }

}
