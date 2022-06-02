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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsAcrAtDispStyleAtLongShort extends AbstractGlsCommand
{
   public AtGlsAcrAtDispStyleAtLongShort(GlossariesSty sty)
   {
      this("@glsacr@dispstyle@long-short", sty);
   }

   public AtGlsAcrAtDispStyleAtLongShort(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new AtGlsAcrAtDispStyleAtLongShort(getName(), getSty());
   }

   public boolean hasLong(GlsLabel glslabel)
   {
      if (glslabel == null) return false;

      GlossaryEntry entry = glslabel.getEntry();

      if (entry == null) return false;

      return entry.get("long") != null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("glslabel");
      GlsLabel glslabel;

      if (cs instanceof GlsLabel)
      {
         glslabel = (GlsLabel)cs;
      }
      else
      {
         String label = parser.expandToString(cs, stack);

         glslabel = new GlsLabel("@@glslabel", label, getEntry(label));
      }

      TeXObjectList expanded = parser.getListener().createStack();

      if (hasLong(glslabel))
      {
         expanded.add(parser.getListener().getControlSequence("glsgenacfmt"));
      }
      else
      {
         expanded.add(parser.getListener().getControlSequence("glsgenentryfmt"));
      }

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence("glslabel");
      GlsLabel glslabel;

      if (cs instanceof GlsLabel)
      {
         glslabel = (GlsLabel)cs;
      }
      else
      {
         String label = parser.expandToString(cs, stack);

         glslabel = new GlsLabel("@@glslabel", label, getEntry(label));
      }

      if (hasLong(glslabel))
      {
         cs = parser.getListener().getControlSequence("glsgenacfmt");
      }
      else
      {
         cs = parser.getListener().getControlSequence("glsgenentryfmt");
      }

      if (parser == stack || stack == null)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
