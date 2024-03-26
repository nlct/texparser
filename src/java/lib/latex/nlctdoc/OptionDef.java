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

public class OptionDef extends StandaloneDef
{
   public OptionDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("optiondef", outerBox, rightBox, noteBox, sty);
   }

   public OptionDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      this("optiondef", outerBox, rightBox, noteBox, sty, prefix);
   }

   public OptionDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this(name, outerBox, rightBox, noteBox, sty, "opt.");
   }

   public OptionDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
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
      return new OptionDef(getName(), outerBox, rightBox, noteBox, getSty());
   }

   @Override
   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return parser.getControlSequence("optnotefmt");
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      TeXObject syntax = glslabel.getField("syntax");

      if (syntax != null)
      {
         list.add(parser.getListener().getOther('='));
         list.add(syntax, true);
      }
   }

   @Override
   protected void preArgHook(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      // ignore bookmark level option
      popModifier(parser, stack, '+');
      popOptArg(parser, stack);
   }

   @Override
   protected void postArgHook(GlsLabel glslabel,
    TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserUtils.process(
        parser.getListener().getControlSequence("optiondefhook"),
        parser, stack);

      if (outerBox instanceof TaggedColourBox)
      {
         TaggedColourBox taggedBox = (TaggedColourBox)outerBox;

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

         TeXObject statusVal = glslabel.getField("status");

         if (statusVal != null)
         {
            String status = parser.expandToString(statusVal, parser);

            if (!status.equals("default"))
            {
               title.add(parser.getListener().getControlSequence("icon"));
               title.add(parser.getListener().createGroup(status));
            }
         }

         taggedBox.setTitle(title);
      }
   }

}
