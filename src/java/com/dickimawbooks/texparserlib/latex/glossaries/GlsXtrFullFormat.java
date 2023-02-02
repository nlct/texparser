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

public class GlsXtrFullFormat extends AbstractGlsCommand
{
   public GlsXtrFullFormat(GlossariesSty sty)
   {
      this("glsxtrfullformat", CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrFullFormat(String name, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrFullFormat(String name, boolean isPlural, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsXtrFullFormat(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public GlsXtrFullFormat(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   @Override
   public Object clone()
   {
      return new GlsXtrFullFormat(getName(), getCaseChange(), isPlural, getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      TeXObject insert = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      ControlSequence cs = listener.getControlSequence("ifglsxtrinsertinside");

      list.add(listener.getControlSequence("glsfirstlongfont"));
      Group grp = listener.createGroup();
      list.add(grp);

      String csname;

      switch (caseChange)
      {
         case SENTENCE:
            csname = "Glsaccessfmtlong";
         break;
         case TO_UPPER:
            csname = "GLSaccessfmtlong";
         break;
         default: 
            csname = "glsaccessfmtlong";
      }

      if (isPlural)
      {
         csname += "pl";
      }

      grp.add(listener.getControlSequence(csname));

      Group subgrp = listener.createGroup();
      grp.add(subgrp);

      if (cs instanceof TeXBoolean && ((TeXBoolean)cs).booleanValue())
      {
         if (!insert.isEmpty())
         {
            subgrp.add(insert);
         }

         grp.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
         grp.add(glslabel);
      }
      else
      {
         grp.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
         grp.add(glslabel);

         TeXObjectList sublist = list;

         if (caseChange == CaseChange.TO_UPPER)
         {
            list.add(listener.getControlSequence("mfirstucMakeUppercase"));
            subgrp = listener.createGroup();
            list.add(subgrp);
            sublist = subgrp;
         }

         sublist.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
         grp = listener.createGroup();
         sublist.add(grp);

         if (!insert.isEmpty())
         {
            grp.add(insert);
         }
      }

      list.add(listener.getControlSequence("glsxtrfullsep"));
      list.add(glslabel);

      list.add(listener.getControlSequence("glsxtrparen"));
      grp = listener.createGroup();

      list.add(grp);

      grp.add(listener.getControlSequence("protect"));
      grp.add(listener.getControlSequence("glsfirstabbrvfont"));

      subgrp = listener.createGroup();
      grp.add(subgrp);

      if (caseChange == CaseChange.TO_UPPER)
      {
         csname = "GLSaccessfmtshort";
      }
      else
      {
         csname = "glsaccessfmtshort";
      }

      if (isPlural)
      {
         csname += "pl";
      }

      subgrp.add(listener.getControlSequence(csname));

      subgrp.add(listener.createGroup());
      subgrp.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
      subgrp.add(glslabel);

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
