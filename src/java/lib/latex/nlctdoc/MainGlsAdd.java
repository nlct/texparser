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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class MainGlsAdd extends AbstractGlsCommand
{
   public MainGlsAdd(GlossariesSty sty)
   {
      this("mainglsadd", sty);
   }

   public MainGlsAdd(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new MainGlsAdd(getName(), getSty());
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
      TeXParserListener listener = parser.getListener();

      TeXObject optArg = popOptArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);
      String tag = popLabelString(parser, stack);

      TeXObjectList content = listener.createStack();

      if (tag.isEmpty())
      {
         parser.putControlSequence(true, 
           new TextualContentCommand("mainglsaddcounter", ""));
      }
      else
      {
         ControlSequence cs = parser.getControlSequence(tag+"counter");

         if (cs == null)
         {
            cs = listener.getControlSequence("currentcounter");
         }

         String counter = parser.expandToString(cs, stack);

         if (!counter.isEmpty())
         {
            content.add(listener.getControlSequence("glsadd"));
            content.add(listener.getOther('['));
            content.addAll(listener.createString("counter="+counter));
            content.add(listener.getOther(']'));
            content.add(glslabel);
         }
      }

      content.add(listener.getControlSequence("glsadd"));
      content.add(glslabel);

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
