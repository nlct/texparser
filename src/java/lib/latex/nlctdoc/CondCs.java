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

public class CondCs extends AbstractGlsCommand
{
   public CondCs(String name, String suffix, GlossariesSty sty)
   {
      super(name, sty);
      this.suffix = suffix;
   }

   @Override
   public Object clone()
   {
      return new CondCs(getName(), suffix, getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject options = popOptArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry(sty);

      TeXObjectList expanded = listener.createStack();
      TeXObjectList grp = listener.createGroup();
      expanded.add(grp);

      grp.add(new TeXCsRef("def"));
      grp.add(new TeXCsRef("explTFsuffix"));
      grp.add(listener.createGroup(suffix));

      grp.add(new TeXCsRef("gls"));

      if (options != null && !options.isEmpty())
      {
         grp.add(listener.getOther('['));
         grp.add(options, true);
         grp.add(listener.getOther(']'));
      }

      grp.add(glslabel);

      return expanded;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      parser.startGroup();

      TeXParserListener listener = parser.getListener();

      TeXObject options = popOptArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      parser.putControlSequence(true,
        new TextualContentCommand("explTFsuffix", suffix));

      GlossaryEntry entry = glslabel.getEntry(sty);

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

      if (parser.isDebugMode(TeXParser.DEBUG_SETTINGS))
      {
         parser.logMessage("ENDING GROUP AFTER PROCESSING "
          + toString() + " REMAINING STACK: "+stack);
      }

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected String suffix;
}
