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

public class GlsNavHyperLink extends ControlSequence
{
   public GlsNavHyperLink()
   {
      this("glsnavhyperlink");
   }

   public GlsNavHyperLink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsNavHyperLink(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String type = popOptLabelString(parser, stack);
      String grpLabel = popLabelString(parser, stack);
      TeXObject title = popArg(parser, stack);

      parser.putControlSequence(true, 
            new TextualContentCommand("gls@grplabel", grpLabel));

      ControlSequence titleCs 
         = new GenericCommand(true, "@gls@grptitle", null, title);

      parser.putControlSequence(true, titleCs);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("@glslink"));
      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(listener.getControlSequence("glsnavhyperlinkname"));
      grp.add(listener.createGroup(type));
      grp.add(listener.createGroup(grpLabel));

      list.add(titleCs);

      if (parser == stack || stack == null)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
