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

public class Idxn extends AbstractGlsCommand
{
   public Idxn(GlossariesSty sty)
   {
      this("idxn", sty);
   }

   public Idxn(String name, GlossariesSty sty)
   {
      this(name, "name", sty);
   }

   public Idxn(String name, String field, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
   }

   @Override
   public Object clone()
   {
      return new Idxn(getName(), field, getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      KeyValList options = popOptKeyValList(parser, stack, false);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String dualLabel = "dual."+glslabel.getLabel();

      GlossaryEntry dualEntry = sty.getEntry(dualLabel);

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();
      expanded.add(listener.getControlSequence("gls"+field));

      if (options != null)
      {
         expanded.add(listener.getOther('['));
         expanded.add(options);
         expanded.add(listener.getOther(']'));
      }

      Group grp = listener.createGroup();
      expanded.add(grp);

      if (dualEntry == null)
      {
         String idxLabel = "idx."+glslabel.getLabel();

         GlossaryEntry idxEntry = sty.getEntry(idxLabel);

         if (idxEntry == null)
         {
            grp.add(glslabel);
         }
         else
         {
            grp.add(new GlsLabel("nlctdoc@idxlabel", idxLabel, idxEntry));
         }
      }
      else
      {
         grp.add(new GlsLabel("nlctdoc@duallabel", dualLabel, dualEntry));
      }

      return expanded;
   }

   protected String field;
}
