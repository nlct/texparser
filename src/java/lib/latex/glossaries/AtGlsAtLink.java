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
import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsAtLink extends AbstractGlsCommand
{
   public AtGlsAtLink(GlossariesSty sty)
   {
      this("@gls@link", sty, false);
   }

   public AtGlsAtLink(String name, GlossariesSty sty, boolean checkModifier)
   {
      super(name, sty);
      this.checkModifier = checkModifier;
   }

   public Object clone()
   {
      return new AtGlsAtLink(getName(), getSty(), checkModifier);
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   // leave indexing/recording to TeX
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      KeyValList options = popOptKeyValList(parser, stack, checkModifier);

      GlsLabel glslabel = popEntryLabel(parser, stack);
      GlossaryEntry entry = glslabel.getEntry();

      parser.putControlSequence(true, glslabel.duplicate("glslabel"));

      TeXObject linkText = popArg(parser, stack);

      parser.putControlSequence(true, glslabel.duplicate("@gls@link@label"));

      // ignore indexing stuff (location counter and format)
   
      if (entry == null)
      {
         parser.putControlSequence(true, new GlsType("glstype", "main"));
      }
      else
      {
         parser.putControlSequence(true, new GlsType(entry.getGlossary()));
      }

      TeXObjectList list = new TeXObjectList();
      //list.add(listener.getControlSequence("@gls@setdefault@glslink@opts"));
      list.add(listener.getControlSequence("do@glsdisablehyperinlist"));
      //list.add(listener.getControlSequence("do@gls@link@checkfirsthyper"));

      if (options != null && !options.isEmpty())
      {
         list.add(listener.getControlSequence("setkeys"));
         list.add(listener.createGroup("glslink"));
         list.add(options);
      }

      list.add(listener.getControlSequence("glslinkpostsetkeys"));

      list.process(parser);

      ControlSequence cs = parser.getControlSequence("ifKV@glslink@hyper");

      if (cs instanceof TeXBoolean && ((TeXBoolean)cs).booleanValue())
      {
         list.add(new TeXCsRef("@glslink"));
      }
      else
      {
         list.add(new TeXCsRef("glsdonohyperlink"));
      }

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(new TeXCsRef("glolinkprefix"));
      grp.add(new TeXCsRef("glslabel"));

      grp = listener.createGroup();
      list.add(grp);
      grp.add(new TeXCsRef("glstextformat"));

      Group subgrp = listener.createGroup();
      grp.add(subgrp);
      subgrp.add(linkText);

      if (parser == stack || stack == null)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean checkModifier = false;
}
