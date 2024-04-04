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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsDoHyperLink extends ControlSequence
{
   public GlsDoHyperLink()
   {
      this("glsdohyperlink");
   }

   public GlsDoHyperLink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsDoHyperLink(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String target = popLabelString(parser, stack);
      TeXObject linkText = popArg(parser, stack);

      TeXObjectList list = listener.createStack();

      ControlSequence cs = listener.getControlSequence("glsdohyperlinkhook");

      if (! (cs instanceof AtGobble) )
      {
         list.add(cs);
         list.add(listener.createGroup(target));

         Group grp = listener.createGroup();
         list.add(grp);
         grp.add((TeXObject)linkText.clone(), true);
      }

      cs = listener.getControlSequence("hyperlink");

      list.add(cs);
      list.add(listener.createGroup(target));

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add((TeXObject)linkText.clone(), true);

      TeXParserUtils.process(list, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
