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

public class OptionValDef extends StandaloneDef
{
   public OptionValDef(FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      this("optionvaldef", outerBox, rightBox, noteBox, sty);
   }

   public OptionValDef(String name, FrameBoxEnv outerBox, FrameBox rightBox,
     FrameBox noteBox, GlossariesSty sty)
   {
      super(name, outerBox, rightBox, noteBox, sty);
   }

   @Override
   public Object clone()
   {
      return new OptionValDef(getName(), outerBox, rightBox, noteBox, getSty());
   }

   @Override
   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return parser.getControlSequence("optnotefmt");
   }

   @Override
   protected void preArgHook(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      setEntryLabelPrefix("opt.");

      parentLabel = popEntryLabel(parser, stack);

      setEntryLabelPrefix("optval."+parentLabel.getLabel().substring(4)+".");
   }

   @Override
   protected void addRow(TeXObjectList list, GlsLabel glslabel, 
      TeXParser parser, Vector<GlsLabel> modList)
   throws IOException
   {
      list.add(parser.getListener().getControlSequence("gls"));
      list.add(parentLabel);
      list.add(parser.getListener().getOther('='));
      super.addRow(list, glslabel, parser, modList);
   }

   protected GlsLabel parentLabel;
}
