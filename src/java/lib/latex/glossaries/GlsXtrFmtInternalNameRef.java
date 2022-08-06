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

public class GlsXtrFmtInternalNameRef extends ControlSequence
{
   public GlsXtrFmtInternalNameRef()
   {
      this("glsxtrfmtinternalnameref");
   }

   public GlsXtrFmtInternalNameRef(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrFmtInternalNameRef(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject target = popArg(parser, stack);
      String csname = popLabelString(parser, stack);
      TeXObject title = popArg(parser, stack);

      TeXObjectList substack = listener.createStack();

      ControlSequence cs = parser.getControlSequence(csname);

      TeXObjectList arg;

      if (cs == null)
      {
         arg = substack;
      }
      else
      {
         substack.add(cs);
         arg = listener.createGroup();
         substack.add(arg);
      }

      arg.add(listener.getControlSequence("glsdohyperlink"));
      arg.add(TeXParserUtils.createGroup(listener, target));

      Group grp = listener.createGroup();
      arg.add(grp);

      grp.add(title);

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
