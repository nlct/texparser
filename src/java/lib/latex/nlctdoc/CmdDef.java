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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class CmdDef extends AbstractGlsCommand
{
   public CmdDef(FrameBox fbox, GlossariesSty sty)
   {
      this("cmddef", fbox, sty);
   }

   public CmdDef(String name, FrameBox fbox, GlossariesSty sty)
   {
      super(name, sty);
      this.fbox = fbox;
   }

   @Override
   public Object clone()
   {
      return new CmdDef(getName(), fbox, getSty());
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
      popModifier(parser, stack, '*');

      GlsLabel glslabel = popEntryLabel("glscurrententrylabel", parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      GlossaryEntry entry = glslabel.getEntry();

      if (entry != null)
      {
         list.add(listener.getControlSequence("glstarget"));
         list.add(glslabel);

         Group grp = listener.createGroup();
         list.add(grp);

         grp.add(listener.getControlSequence("glossentryname"));
         grp.add(glslabel);

         TeXObject syntax = entry.get("syntax");

         if (syntax != null)
         {
            list.add(syntax, true);
         }
      }

      list.add(new EndFrameBox(fbox));

      stack.push(list, true);

      StartFrameBox startBox = new StartFrameBox(fbox);

      if (parser == stack)
      {
         startBox.process(parser);
      }
      else 
      {
         startBox.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected FrameBox fbox;
}
