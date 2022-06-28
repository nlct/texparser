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

public class GlsXtrSubSequentFmt extends AbstractGlsCommand
{
   public GlsXtrSubSequentFmt(GlossariesSty sty)
   {
      this("glsxtrsubsequentfmt", CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrSubSequentFmt(String name, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrSubSequentFmt(String name, boolean isPlural, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsXtrSubSequentFmt(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public GlsXtrSubSequentFmt(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   @Override
   public Object clone()
   {
      return new GlsXtrSubSequentFmt(getName(), getCaseChange(), isPlural, getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      TeXObject insert = popArg(parser, stack);

      boolean markWords = sty.isAttributeTrue(glslabel, "markshortwords");

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      boolean insertInside = TeXParserUtils.isTrue("ifglsxtrinsertinside", parser);

      String csname;

      switch (caseChange)
      {
         case SENTENCE:
            csname = "Glsaccess";
         break;
         case TO_UPPER:
            csname = "GLSaccess";
         break;
         default: 
            csname = "glsaccess";
      }

      if (!markWords)
      {
         csname += "fmt";
      }

      csname += "short";

      if (isPlural)
      {
         csname += "pl";
      }

      list.add(listener.getControlSequence("glsabbrvfont"));
      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(listener.getControlSequence(csname));

      ControlSequence fmtCs = listener.getControlSequence("glsxtrgenentrytextfmt");

      if (markWords)
      {
         grp.add(glslabel);
         Group subgrp = listener.createGroup();

         if (insertInside)
         {
            grp.add(fmtCs);
            grp.add(subgrp);
         }
         else
         {
            list.add(fmtCs);
            list.add(subgrp);
         }

         subgrp.add(insert, true);
      }
      else
      {
         Group subgrp = listener.createGroup();
         grp.add(subgrp);

         grp.add(fmtCs);
         grp.add(glslabel);

         if (insertInside)
         {
            subgrp.add(insert, true);
         }
         else
         {
            list.add(fmtCs);
            grp = listener.createGroup();
            list.add(grp);
            grp.add(insert, true);
         }
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   protected boolean isPlural;
   protected CaseChange caseChange;
}
