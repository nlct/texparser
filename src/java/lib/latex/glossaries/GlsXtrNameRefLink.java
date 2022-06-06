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

public class GlsXtrNameRefLink extends ControlSequence
{
   public GlsXtrNameRefLink()
   {
      this("glsxtrnamereflink");
   }

   public GlsXtrNameRefLink(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsXtrNameRefLink(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String csname = popLabelString(parser, stack);
      TeXObject title = popArg(parser, stack);
      String anchor = popLabelString(parser, stack);
      String externalFile = popLabelString(parser, stack);

      parser.startGroup();

      parser.putControlSequence(true, new AtFirstOfOne("glshypernumber"));

      TeXObjectList substack = listener.createStack();

      if (externalFile.isEmpty())
      {
         substack.add(listener.getControlSequence("glsxtrfmtinternalnameref"));
         substack.add(listener.createGroup(anchor));
         substack.add(listener.createGroup(csname));

         Group grp = listener.createGroup();
         substack.add(grp);

         grp.add(title);
      }
      else
      {
         substack.add(listener.getControlSequence("glsxtrfmtexternalnameref"));
         substack.add(listener.createGroup(anchor));
         substack.add(listener.createGroup(csname));

         Group grp = listener.createGroup();
         substack.add(grp);

         grp.add(title);

         grp = listener.createGroup();
         substack.add(grp);

         grp.add(listener.createGroup(externalFile));
      }

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
