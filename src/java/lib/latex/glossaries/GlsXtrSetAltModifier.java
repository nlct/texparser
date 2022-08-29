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

public class GlsXtrSetAltModifier extends ControlSequence
{
   public GlsXtrSetAltModifier(GlossariesSty sty)
   {
      this("GlsXtrSetAltModifier", sty);
   }

   public GlsXtrSetAltModifier(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new GlsXtrSetAltModifier(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String modifier = popLabelString(parser, stack).trim();

      KeyValList options = TeXParserUtils.popKeyValList(parser, stack);

      sty.setModifier(parser.getListener().getOther(modifier.codePointAt(0)), options);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected GlossariesSty sty;
}
