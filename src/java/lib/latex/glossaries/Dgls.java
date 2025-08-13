/*
    Copyright (C) 2022-2025 Nicola L.C. Talbot
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

public class Dgls extends Gls
{
   public Dgls(GlossariesSty sty)
   {
      this("dgls", CaseChange.NO_CHANGE, false, sty);
   }

   public Dgls(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public Dgls(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, caseChange, isPlural, sty);
   }

   public Object clone()
   {
      Dgls gls = new Dgls(getName(), getCaseChange(), isPlural(), getSty());

      gls.setEntryLabelPrefix(getEntryLabelPrefix());
      gls.setDefaultOptions(getDefaultOptions());

      return gls;
   }

   @Override
   protected GlsLabel popEntryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String label = popLabelString(parser, stack);

      GlossaryEntry entry = sty.getDualEntry(label);

      if (entry == null)
      {
         return new GlsLabel("@@glslabel@"+label, label);
      }
      else
      {
         return new GlsLabel("@@glslabel@"+label, entry.getLabel(), entry);
      }
   }

   @Override
   protected void preGlsHook(GlsLabel glslabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = parser.getControlSequence("predglshook");

      if (cs != null)
      {
         TeXObjectList substack = parser.getListener().createStack();

         substack.add(cs);
         substack.add(glslabel);

         TeXParserUtils.process(substack, parser, stack);
      }
   }

}
