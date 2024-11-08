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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsXtrGlossEntry extends AbstractGlsCommand
{
   public AtGlsXtrGlossEntry(GlossariesSty sty)
   {
      this("@glsxtrglossentry", CaseChange.NO_CHANGE, sty);
   }

   public AtGlsXtrGlossEntry(String name, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
   }

   @Override
   public Object clone()
   {
      return new AtGlsXtrGlossEntry(getName(), caseChange, getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXObjectList content = listener.createStack();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      if (caseChange == CaseChange.SENTENCE)
      {
         content.add(listener.getControlSequence("GlsXtrStandaloneEntryHeadNameFirstUc"));
      }
      else
      {
         content.add(listener.getControlSequence("GlsXtrStandaloneEntryHeadName"));
      }

      content.add(glslabel);

      TeXParserUtils.process(content, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private CaseChange caseChange = CaseChange.NO_CHANGE;
}
