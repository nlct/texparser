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

public class SummaryClassBox extends SummaryBox
{
   public SummaryClassBox(FrameBox frameBox,
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      this("summaryglossentryclass", frameBox, rightBox, noteBox, sty);
   }

   public SummaryClassBox(String name, FrameBox frameBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, frameBox, rightBox, noteBox, sty);
   }

   @Override
   public Object clone()
   {
      return new SummaryClassBox(getName(), frameBox, rightBox, noteBox, getSty());
   }

   @Override
   protected void addPreEntryName(TeXObjectList list, GlsLabel glslabel,
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      super.addPreEntryName(list, glslabel, parser, stack);

      TeXParserListener listener = parser.getListener();

      list.add(listener.getControlSequence("cmd"));
      list.add(listener.createGroup("documentclass"));

      GlossaryEntry entry = glslabel.getEntry();

      TeXObject syntax = entry.get("syntax");

      if (syntax != null)
      {
         list.add(listener.getOther('['));
         list.add(syntax);
         list.add(listener.getOther(']'));
      }

      list.add(listener.getOther('{'));
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      list.add(parser.getListener().getOther('}'));
   }

   protected FrameBox frameBox, rightBox, noteBox;
}
