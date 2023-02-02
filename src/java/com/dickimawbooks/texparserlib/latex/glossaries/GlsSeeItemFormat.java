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

public class GlsSeeItemFormat extends AbstractGlsCommand
{
   public GlsSeeItemFormat(GlossariesSty sty)
   {
      this("glsseeitemformat", sty);
   }

   public GlsSeeItemFormat(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsSeeItemFormat(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = parser.getListener().createStack();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      if (sty.isExtra())
      {
         expanded.add(parser.getListener().getControlSequence("ifglshasshort"));
         expanded.add(TeXParserUtils.createGroup(parser, glslabel));

         Group grp = parser.getListener().createGroup();
         expanded.add(grp);

         grp.add(parser.getListener().getControlSequence("glsfmttext"));
         grp.add(TeXParserUtils.createGroup(parser, glslabel));

         grp = parser.getListener().createGroup();
         expanded.add(grp);

         grp.add(parser.getListener().getControlSequence("glsfmtname"));
         grp.add(TeXParserUtils.createGroup(parser, glslabel));
      }
      else
      {
         TeXObject val = glslabel.getField("text");

         if (val != null)
         {
            expanded.add(val, true);
         }
      }

      return expanded;
   }

}
