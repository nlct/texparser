/*
    Copyright (C) 2022-2024 Nicola L.C. Talbot
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

public class ClsDef extends StandaloneDef
{
   public ClsDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("clsdef", outerBox, rightBox, noteBox, sty);
   }

   public ClsDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      this("clsdef", outerBox, rightBox, noteBox, sty, prefix);
   }

   public ClsDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this(name, outerBox, rightBox, noteBox, sty, "cls.");
   }

   public ClsDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      super(name, outerBox, rightBox, noteBox, sty);

      if (prefix != null)
      {
         setEntryLabelPrefix(prefix);
      }
   }

   @Override
   public Object clone()
   {
      return new ClsDef(getName(), outerBox, rightBox, noteBox, getSty());
   }

   @Override
   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return parser.getControlSequence("pkgnotefmt");
   }

   @Override
   protected void addPreEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      TeXParserListener listener = parser.getListener();

      list.add(listener.getControlSequence("cmd"));
      list.add(parser.getListener().createGroup("documentclass"));

      TeXObject syntax = glslabel.getField("syntax");

      if (syntax != null)
      {
         list.add(listener.getOther('['));
         list.add(syntax, true);
         list.add(listener.getOther(']'));
      }

      list.add(listener.getOther('{'));
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      list.add(parser.getListener().getOther('}'));
   }

   @Override
   protected void postArgHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      if (outerBox instanceof TaggedColourBox)
      {
         TaggedColourBox taggedBox = (TaggedColourBox)outerBox;

         TeXObject statusVal = glslabel.getField("status");
         TeXObjectList title = null;

         if (statusVal != null)
         {
            String status = parser.expandToString(statusVal, parser);

            if (!status.equals("default"))
            {
               title = parser.getListener().createStack();
               title.add(parser.getListener().getControlSequence("icon"));
               title.add(parser.getListener().createGroup(status));
            }
         }

         if (title == null)
         {
            taggedBox.restoreTitle();
         }
         else
         {
            taggedBox.setTitle(title);
         }
      }
   }

}
