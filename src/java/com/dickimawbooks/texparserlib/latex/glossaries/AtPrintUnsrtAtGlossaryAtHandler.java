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

public class AtPrintUnsrtAtGlossaryAtHandler extends AbstractGlsCommand
{
   public AtPrintUnsrtAtGlossaryAtHandler(GlossariesSty sty)
   {
      this("@printunsrt@glossary@handler", sty);
   }

   public AtPrintUnsrtAtGlossaryAtHandler(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new AtPrintUnsrtAtGlossaryAtHandler(getName(), getSty());
   }

   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      GlsLabel label = popEntryLabel("glscurrententrylabel", parser, stack);

      TeXObjectList substack = parser.getListener().createStack();

      DataObjectList noexpand = parser.getListener().createDataList(true);
      substack.add(noexpand);

      noexpand.add(parser.getListener().getControlSequence("gdef"));
      noexpand.add(new TeXCsRef("glscurrententrylabel"));
      noexpand.add(parser.getListener().createGroup(label.getLabel()));

      substack.add(parser.getListener().getControlSequence(
        "printunsrtglossaryhandler"));
      substack.add(parser.getListener().createGroup(label.getLabel()));

      return substack;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      GlsLabel label = popEntryLabel("glscurrententrylabel", parser, stack);

      parser.putControlSequence(false, label);

      ControlSequence cs = parser.getListener().getControlSequence(
        "printunsrtglossaryhandler");

      TeXObjectList substack = parser.getListener().createStack();

      substack.add(cs);
      substack.add(label);

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
