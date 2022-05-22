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

public class GlsTarget extends Command
{
   public GlsTarget()
   {
      this("glstarget");
   }

   public GlsTarget(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsTarget(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject labelArg = popArg(parser, stack);
      TeXObject textArg = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("@glstarget"));

      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(listener.getControlSequence("glolinkprefix"));
      grp.add(labelArg);

      grp = listener.createGroup();
      list.add(grp);

      grp.add(textArg);

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject labelArg = popArg(parser, stack);

      Group grp = listener.createGroup();
      grp.add(listener.getControlSequence("glolinkprefix"));
      grp.add(labelArg);

      stack.push(grp);

      stack.push(listener.getControlSequence("@glstarget"));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
