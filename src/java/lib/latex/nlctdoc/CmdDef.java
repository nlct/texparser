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

public class CmdDef extends StandaloneDef
{
   public CmdDef(TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("cmddef", taggedBox, rightBox, noteBox, sty);
   }

   public CmdDef(String name, TaggedColourBox taggedBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      super(name, taggedBox, rightBox, noteBox, sty);
   }

   @Override
   public Object clone()
   {
      return new CmdDef(getName(), taggedBox, rightBox, noteBox, getSty());
   }

   @Override
   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      TeXObject syntax = glslabel.getEntry().get("syntax");

      if (syntax != null)
      {
         list.add(syntax, true);
      }
   }

   @Override
   protected TeXObject getRightBoxContent(GlsLabel glslabel, TeXParser parser)
   {
      TeXObjectList list = null;

      TeXObject providedby = glslabel.getEntry().get("providedby");

      if (providedby != null)
      {
         list = parser.getListener().createString("provided by ");
         list.add(providedby, true);
      }

      return list;
   }

}
