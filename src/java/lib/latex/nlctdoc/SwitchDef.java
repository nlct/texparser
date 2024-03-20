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

public class SwitchDef extends StandaloneDef
{
   public SwitchDef(TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("switchdef", taggedBox, rightBox, noteBox, sty);
   }

   public SwitchDef(TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      this("switchdef", taggedBox, rightBox, noteBox, sty, prefix);
   }

   public SwitchDef(String name, TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this(name, taggedBox, rightBox, noteBox, sty, "switch.");
   }

   public SwitchDef(String name, TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      super(name, taggedBox, rightBox, noteBox, sty);

      if (prefix != null)
      {
         setEntryLabelPrefix("switch.");
      }
   }

   @Override
   public Object clone()
   {
      return new SwitchDef(getName(), taggedBox, rightBox, noteBox, getSty());
   }

   @Override
   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return parser.getControlSequence("switchnotefmt");
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      TeXObject syntax = glslabel.getField("syntax");

      if (syntax != null)
      {
         list.add(parser.getListener().getSpace());
         list.add(syntax, true);
      }
   }

   @Override
   protected void postArgHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject syntax = glslabel.getField("syntax");

      TeXObjectList title = parser.getListener().createStack();
      title.add(parser.getListener().getControlSequence("icon"));

      if (syntax == null)
      {
         title.add(parser.getListener().createGroup("novaluesetting"));
      }
      else
      {
         String syntaxVal = syntax.toString(parser);

         if (syntaxVal.equals("\\meta{boolean}"))
         {
            TeXObject val = glslabel.getField("initvalue");
            String toggle = "off";

            if (val != null)
            {
               if (val.toString(parser).equals("true"))
               {
                  toggle = "on";
               }
            }

            title.add(parser.getListener().createGroup("toggle"+toggle+"setting"));
         }
         else
         {
            title.add(parser.getListener().createGroup("valuesetting"));
         }
      }

      taggedBox.setTitle(title);
   }
}
