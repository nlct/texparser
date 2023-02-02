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

public class GlsXtrLocationHyperLink extends ControlSequence
{
   public GlsXtrLocationHyperLink()
   {
      this("glsxtrlocationhyperlink");
   }

   public GlsXtrLocationHyperLink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrLocationHyperLink(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getControlSequence("glsxtrsupplocationurl");

      TeXParserListener listener = parser.getListener();

      TeXObjectList substack = listener.createStack();

      if (cs == null || cs.isEmpty())
      {
         substack.add(listener.getControlSequence("GlsXtrInternalLocationHyperlink"));
      }
      else
      {
         String counter = popLabelString(parser, stack);
         String prefix = popLabelString(parser, stack);
         TeXObject loc = popArg(parser, stack);

         substack.add(listener.getControlSequence("hyperref"));
         substack.add(cs);
         substack.add(listener.createGroup());
         substack.add(listener.createGroup(String.format("%s%s%s",
          counter, prefix, parser.expandToString(loc, stack))));

         Group grp = listener.createGroup();
         substack.add(grp);

         grp.add(loc);
      }

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
