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

public class SummaryCommandOptionBox extends SummaryBox
{
   public SummaryCommandOptionBox(FrameBox frameBox,
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      this("summaryglossentrycommandoption", frameBox, rightBox, noteBox, sty);
   }

   public SummaryCommandOptionBox(String name, FrameBox frameBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, frameBox, rightBox, noteBox, sty);
   }

   @Override
   public Object clone()
   {
      return new SummaryCommandOptionBox(getName(), frameBox, rightBox, noteBox, getSty());
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
         list.add(parser.getListener().getOther('='));
         list.add(parser.getListener().getControlSequence("code"));
         Group grp = parser.getListener().createGroup();
         list.add(grp);
         grp.add(syntax);
      }
   }

   protected TeXObject getRightBoxContent(GlsLabel glslabel, TeXParser parser)
   throws IOException
   {
      TeXObject content = super.getRightBoxContent(glslabel, parser);
      TeXObjectList contentList;

      if (content == null)
      {
         contentList = parser.getListener().createStack();
      }
      else if (parser.isStack(content))
      {
         contentList = (TeXObjectList)content;
      }
      else
      {
         contentList = parser.getListener().createStack();
         contentList.add(content);
      }

      TeXObject syntax = null;

      if (glslabel != null && glslabel.getEntry() != null)
      {
         syntax = glslabel.getEntry().get("syntax");
      }

      contentList.add(parser.getListener().getControlSequence("glssymbol"));

      if (syntax == null)
      {
         contentList.add(parser.getListener().createGroup("sym.novaluesetting"));
      }
      else
      {
         String syntaxVal = syntax.toString(parser);

         if (syntaxVal.equals("\\meta{boolean}"))
         {
            TeXObject val = glslabel.getEntry().get("initvalue");
            String toggle = "off";

            if (val != null)
            {
               if (val.toString(parser).equals("true"))
               {
                  toggle = "on";
               }
            }

            contentList.add(parser.getListener().createGroup("sym.toggle"+toggle+"setting"));
         }
         else
         {
            contentList.add(parser.getListener().createGroup("sym.valuesetting"));
         }
      }

      return contentList;
   }

   protected FrameBox frameBox, rightBox, noteBox;
}
