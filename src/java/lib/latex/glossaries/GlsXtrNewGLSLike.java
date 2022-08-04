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

public class GlsXtrNewGLSLike extends AbstractGlsCommand
{
   public GlsXtrNewGLSLike(GlossariesSty sty)
   {
      this("glsxtrnewGLSlike", sty);
   }

   public GlsXtrNewGLSLike(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsXtrNewGLSLike(getName(), getSty());
   }
   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList defaultOptions = TeXParserUtils.popOptKeyValList(parser, stack);
      String prefix = popLabelString(parser, stack);
      ControlSequence glsCs = popControlSequence(parser, stack);
      ControlSequence glsplCs = popControlSequence(parser, stack);

      Gls gls = new Gls(glsCs.getName(), CaseChange.TO_UPPER, false, getSty());

      gls.setEntryLabelPrefix(prefix);
      gls.setDefaultOptions(defaultOptions);

      sty.registerControlSequence(gls);

      gls = new Gls(glsplCs.getName(), CaseChange.TO_UPPER, true, getSty());

      gls.setEntryLabelPrefix(prefix);
      gls.setDefaultOptions(defaultOptions);

      sty.registerControlSequence(gls);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
