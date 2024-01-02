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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class PredCs extends AbstractGlsCommand
{
   public PredCs(GlossariesSty sty)
   {
      this("predcs", sty);
   }

   public PredCs(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PredCs(getName(), getSty());
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

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      parser.startGroup();

      TeXParserListener listener = parser.getListener();

      TeXObject options = popOptArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry(sty);

      TeXObject value = (entry != null ? entry.get("explsuffix") : null);

      if (value != null && !value.isEmpty())
      {
         parser.putControlSequence(true,
          new TextualContentCommand("explsuffix",
            parser.expandToString(value, stack))
         );
      }

      TeXObjectList expanded = listener.createStack();

      expanded.add(new TeXCsRef("gls"));

      if (options != null && !options.isEmpty())
      {
         expanded.add(listener.getOther('['));
         expanded.add(options, true);
         expanded.add(listener.getOther(']'));
      }

      expanded.add(glslabel);

      TeXParserUtils.process(expanded, parser, stack);

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
