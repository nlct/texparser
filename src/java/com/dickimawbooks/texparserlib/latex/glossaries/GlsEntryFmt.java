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

public class GlsEntryFmt extends AbstractGlsCommand
{
   public GlsEntryFmt(GlossariesSty sty)
   {
      this("glsentryfmt", sty);
   }

   public GlsEntryFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsEntryFmt(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      if (sty.isExtra())
      {
         GlsLabel label = expandToEntryLabel("glslabel", parser, stack);
         GlossaryEntry entry = label.getEntry();

         if (entry != null)
         {
            boolean isAbbr = entry.hasField("short");

            if (isAbbr)
            {
               list.add(new TeXCsRef("glssetabbrvfmt"));
               list.add(listener.createGroup(entry.getCategory()));
            }

            if (!isAbbr || sty.isRegular(entry))
            {
               list.add(listener.getControlSequence("glsxtrregularfont"));
               list.add(listener.getControlSequence("glsgenentryfmt"));
            }
            else
            {
               list.add(listener.getControlSequence("glsxtrabbreviationfont"));
               list.add(listener.getControlSequence("glsxtrgenabbrvfmt"));
            }
         }
      }
      else
      {
         list.add(listener.getControlSequence("glsgenentryfmt"));
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }
}
