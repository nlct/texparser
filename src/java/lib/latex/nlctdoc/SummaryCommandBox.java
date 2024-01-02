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

public class SummaryCommandBox extends SummaryBox
{
   public SummaryCommandBox(FrameBox frameBox,
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      this("summaryglossentrycommand", frameBox, rightBox, noteBox, sty);
   }

   public SummaryCommandBox(String name, FrameBox frameBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, frameBox, rightBox, noteBox, sty);
   }

   @Override
   public Object clone()
   {
      return new SummaryCommandBox(getName(), frameBox, rightBox, noteBox, getSty());
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel,
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      GlossaryEntry entry = glslabel.getEntry();

      TeXObject syntax = entry.get("syntax");

      if (syntax != null)
      {
         list.add(parser.getListener().getControlSequence("code"));
         Group grp = parser.getListener().createGroup();
         list.add(grp);
         grp.add(syntax);
      }
   }

   @Override
   protected void initHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      parser.putControlSequence(true, new GenericCommand(true, 
       "explTFsuffix", null, 
        TeXParserUtils.createStack(parser, 
          new TeXCsRef("@explboolsyntaxfmt"),
          parser.getListener().createGroup("TF"))));
   }

   @Override
   protected void preNote(TeXObjectList content, GlsLabel glslabel,
     TeXParser parser)
    throws IOException
   {
      GlossaryEntry entry = glslabel.getEntry();

      if (entry != null)
      {
         TeXObject value = entry.get("explsuffix");

         if (value != null && !value.isEmpty())
         {
            Group grp = parser.getListener().createGroup();
            content.add(grp);

            grp.add(parser.getListener().getControlSequence("def"));
            grp.add(new TeXCsRef("explsuffix"));
            grp.add(parser.getListener().createGroup(
              parser.expandToString(value, parser)));

            grp.add(parser.getListener().getControlSequence("def"));
            grp.add(new TeXCsRef("explTFsuffix"));
            grp.add(parser.getListener().createGroup());

            grp.add(parser.getListener().getControlSequence("def"));
            grp.add(new TeXCsRef("TFsuffix"));
            grp.add(parser.getListener().createGroup());

            grp.add(parser.getListener().getControlSequence("newline"));
            grp.add(parser.getListener().getControlSequence("code"));

            grp.add(TeXParserUtils.createGroup(parser,
             entry.get("name"), entry.get("syntax")));
         }
      }
   }

   protected FrameBox frameBox, rightBox, noteBox;
}
