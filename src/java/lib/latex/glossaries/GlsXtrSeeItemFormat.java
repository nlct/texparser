/*
    Copyright (C) 2025 Nicola L.C. Talbot
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

public class GlsXtrSeeItemFormat extends AbstractGlsCommand
{
   public GlsXtrSeeItemFormat(GlossariesSty sty)
   {
      this("glsxtrseeitemformat", sty);
   }

   public GlsXtrSeeItemFormat(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsXtrSeeItemFormat(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      expanded.add(listener.getControlSequence("ifglshasparent"));
      expanded.add(TeXParserUtils.createGroup(listener, glslabel));

      Group grp = listener.createGroup();
      expanded.add(grp);

      grp.add(TeXParserUtils.createGroup(listener, 
        listener.getControlSequence("glsxtrhiername"),
        TeXParserUtils.createGroup(listener, glslabel)));

      grp = listener.createGroup();
      expanded.add(grp);

      grp.add(listener.getControlSequence("ifglshasshort"));
      grp.add(TeXParserUtils.createGroup(listener, glslabel));

      Group subGrp = listener.createGroup();
      grp.add(subGrp);

      subGrp.add(listener.getControlSequence("glsfmttext"));
      subGrp.add(TeXParserUtils.createGroup(parser, glslabel));

      subGrp = listener.createGroup();
      grp.add(subGrp);

      subGrp.add(listener.getControlSequence("glsfmtname"));
      subGrp.add(TeXParserUtils.createGroup(listener, glslabel));

      return expanded;
   }

}
