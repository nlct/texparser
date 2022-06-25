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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsTarget extends AbstractGlsCommand
{
   public GlsTarget(GlossariesSty sty)
   {
      this("glstarget", sty);
   }

   public GlsTarget(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsTarget(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String label = popLabelString(parser, stack);
      TeXObject textArg = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      String target = parser.expandToString(
            listener.getControlSequence("glolinkprefix"), stack)
          + label;

      Vector<String> existingTargets = sty.getTargets(label);

      if (existingTargets == null || !existingTargets.contains(target))
      {
         list.add(listener.getControlSequence("@glstarget"));

         list.add(listener.createGroup(target));

         Group grp = listener.createGroup();
         list.add(grp);

         grp.add(textArg);
      }
      else
      {
         list.add(textArg, true);
      }

      sty.registerTarget(label, target);

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String label = popLabelString(parser, stack);

      String target = parser.expandToString(
            listener.getControlSequence("glolinkprefix"), stack)
          + label;

      Vector<String> existingTargets = sty.getTargets(label);

      if (existingTargets == null || !existingTargets.contains(target))
      {
         stack.push(listener.createGroup(target));

         stack.push(listener.getControlSequence("@glstarget"));

         sty.registerTarget(label, target);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
