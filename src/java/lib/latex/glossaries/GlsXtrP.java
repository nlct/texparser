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
import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsXtrP extends AbstractGlsCommand
{
   public GlsXtrP(GlossariesSty sty)
   {
      this("@glsxtrp", sty);
   }

   public GlsXtrP(String name, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, sty);
   }

   public GlsXtrP(String name, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
   }

   public Object clone()
   {
      return new GlsXtrP(getName(), caseChange, getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      GlsLabel glslabel = popEntryLabel(parser, stack);
      String field = popLabelString(parser, stack);

      String gls = (caseChange == CaseChange.SENTENCE ? "Gls" : "gls");

      ControlSequence cs = parser.getControlSequence(gls+field);

      if (cs == null)
      {
         cs = parser.getControlSequence(gls+"xtr"+field);
      }

      if (cs == null)
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.UNRECOGNISED, field, getName());
      }
      else
      {
         parser.startGroup();

         TeXObjectList content = listener.createStack();

         parser.putControlSequence(true, new Relax("glspostlinkhook"));

         content.add(cs);

         ControlSequence optCs = parser.getControlSequence("@glsxtrp@opt");

         if (optCs != null)
         {
            content.add(listener.getOther('['));

            content.add(parser.expandonce(optCs, stack));

            content.add(listener.getOther(']'));
         }

         content.add(glslabel);

         TeXParserUtils.process(content, parser, stack);

         parser.endGroup();
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected CaseChange caseChange;
}
