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

public class Dglsfield extends AtGlsAtAtLink
{
   public Dglsfield(GlossariesSty sty)
   {
      this("dglsfield", sty, true, false, CaseChange.NO_CHANGE);
   }

   public Dglsfield(String name, GlossariesSty sty)
   {
      this(name, sty, true, false, CaseChange.NO_CHANGE);
   }
   
   public Dglsfield(String name, GlossariesSty sty, CaseChange caseChange)
   {
      this(name, sty, true, false, caseChange, null);
   }

   public Dglsfield(String name, GlossariesSty sty, CaseChange caseChange, String field)
   {
      this(name, sty, true, false, caseChange, field);
   }

   public Dglsfield(String name, GlossariesSty sty, boolean checkModifier,
     boolean doUnset, CaseChange caseChange)
   {
      this(name, sty, checkModifier, doUnset, caseChange, null);
   }

   public Dglsfield(String name, GlossariesSty sty, boolean checkModifier,
     boolean doUnset, CaseChange caseChange, String field)
   {
      super(name, sty, checkModifier, doUnset, caseChange);
      this.field = field;
      this.popField = (field == null);
   }

   public Object clone()
   {
      return new Dglsfield(getName(), getSty(), checkModifier, doUnset, 
        caseChange, popField ? null : field);
   }

   @Override
   protected GlsLabel popEntryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String label = popLabelString(parser, stack);

      if (popField)
      {
         field = popLabelString(parser, stack);
      }

      GlossaryEntry entry = sty.getDualEntry(label, field);

      if (entry == null)
      {
         return new GlsLabel("@@glslabel@"+label, label);
      }
      else
      {
         return new GlsLabel("@@glslabel@"+label, entry.getLabel(), entry);
      }
   }

   protected TeXObject getLinkText(GlsLabel glslabel,
      TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject fieldVal = glslabel.getField(field);

      if (fieldVal == null)
      {
         return parser.getListener().createStack();
      }
      else
      {
         return fieldVal;
      }
   }

   protected boolean popField = true;
   protected String field;
}
