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

public class GlsAccessFmtField extends AbstractGlsCommand
{
   public GlsAccessFmtField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, sty);
   }

   public GlsAccessFmtField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
      this.caseChange = caseChange;
   }

   @Override
   public Object clone()
   {
      return new GlsAccessFmtField(getName(), getField(), getCaseChange(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject insert = popArg(parser, stack);
      TeXObject csArg = popArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      String csname;

      switch (caseChange)
      {
         case SENTENCE:
            csname = "Glsfmtfield";
         break;
         case TO_UPPER:
            csname = "GLSfmtfield";
         break;
         default:
            csname = "glsfmtfield";
      }

      list.add(listener.getControlSequence(csname));
      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(insert, true);

      list.add(csArg);
      list.add(glslabel);
      list.add(listener.createGroup(field));

      if (sty.isAccSupp())
      {
         csname = "gls" + sty.getInternalFieldName(field) 
           + "accessdisplay";

         ControlSequence cs = parser.getControlSequence(csname);

         if (cs == null)
         {
            csname = "gls" + field + "accessdisplay";
            cs = parser.getControlSequence(csname);
         }

         if (cs != null)
         {
            grp = listener.createGroup();
            grp.add(list);

            list = parser.getListener().createStack();
            list.add(cs);
            list.add(grp);

            list.add(glslabel);
         }
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   public String getField()
   {
      return field;
   }

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   protected String field;
   protected CaseChange caseChange;
}
