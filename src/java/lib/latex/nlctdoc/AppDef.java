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

public class AppDef extends StandaloneDef
{
   public AppDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("appdef", outerBox, rightBox, noteBox, sty);
   }

   public AppDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty, String prefix)
   {
      this("appdef", outerBox, rightBox, noteBox, sty, prefix);
   }

   public AppDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this(name, outerBox, rightBox, noteBox, sty, "app.");
   }

   public AppDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
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
      return new AppDef(getName(), outerBox, rightBox, noteBox, getSty());
   }

   @Override
   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return parser.getControlSequence("appnotefmt");
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

}
