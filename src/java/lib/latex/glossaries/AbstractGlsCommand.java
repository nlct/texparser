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

public abstract class AbstractGlsCommand extends ControlSequence
{
   public AbstractGlsCommand(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   protected GlossaryEntry getEntry(String label)
   {
      return sty.getEntry(label);
   }

   protected Glossary getGlossary(String label)
   {
      return sty.getGlossary(label);
   }

   protected GlsLabel popEntryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg instanceof GlsLabel)
      {
         ((GlsLabel)arg).refresh(sty);

         return (GlsLabel)arg;
      }

      String label = parser.expandToString(arg, stack);

      GlossaryEntry entry = getEntry(label);

      return new GlsLabel("@@glslabel", label, entry);
   }

   protected GlsType popGlossaryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg instanceof GlsType)
      {
         ((GlsType)arg).refresh(sty);

         return (GlsType)arg;
      }

      String label = parser.expandToString(arg, stack);

      Glossary glossary = getGlossary(label);

      return new GlsType("@@glstype", label, glossary);
   }

   public GlossariesSty getSty()
   {
      return sty;
   }

   protected GlossariesSty sty;
}
