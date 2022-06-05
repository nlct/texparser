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

public class SubGlossEntryWithLabel extends AbstractGlsCommand
{
   public SubGlossEntryWithLabel(GlossariesSty sty)
   {
      this("subglossentry", sty);
   }

   public SubGlossEntryWithLabel(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new SubGlossEntryWithLabel(getName(), getSty());
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Numerical level = popNumericalArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObjectList list = listener.createStack();

      DataObjectList noexpand = listener.createDataList(true);
      list.add(noexpand);

      noexpand.add(new TeXCsRef("def"));
      noexpand.add(new TeXCsRef("glscurrententrylabel"));
      noexpand.add(listener.createGroup(glslabel.getLabel()));

      list.add(listener.getControlSequence("gls@org@glossarysubentryfield"));

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(level);

      list.add(listener.createGroup(glslabel.getLabel()));

      grp = listener.createGroup();
      list.add(grp);
      grp.add(popArg(parser, stack));

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      Numerical level = popNumericalArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      listener.putControlSequence(glslabel.duplicate("glscurrententrylabel"));

      stack.push(glslabel);
      stack.push(level);

      ControlSequence cs =
         listener.getControlSequence("gls@org@glossarysubentryfield");

      if (parser == stack || stack == null)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
