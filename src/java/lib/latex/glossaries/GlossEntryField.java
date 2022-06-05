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

public class GlossEntryField extends GlsEntryField
{
   public GlossEntryField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, sty);
   }

   public GlossEntryField(String name, GlossariesSty sty)
   {
      this(name, null, CaseChange.NO_CHANGE, sty);
   }

   public GlossEntryField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, field, caseChange, sty);
   }

   public Object clone()
   {
      return new GlossEntryField(getName(), getField(), getCaseChange(), getSty());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = sty.getFieldName(popLabelString(parser, stack));
      }

      Category category = null;

      CaseChange change = caseChange;

      if (sty.isExtra())
      {
         category = sty.getCategory(glslabel);

         if (category != null && caseChange == CaseChange.NO_CHANGE)
         {
            String attrVal = null;

            if (field == null || fieldLabel.equals("name"))
            {
               attrVal = category.getAttribute("glossname");
            }
            else if (fieldLabel.equals("description"))
            {
               attrVal = category.getAttribute("glossdesc");
            }

            if ("firstuc".equals(attrVal))
            {
               change = CaseChange.SENTENCE;
            }
            else if ("title".equals(attrVal))
            {
               change = CaseChange.TITLE;
            }
            else if ("uc".equals(attrVal))
            {
               change = CaseChange.TO_UPPER;
            }
         }
      }

      TeXObjectList list = expand(glslabel, fieldLabel, change, parser, stack);

      if (field == null || fieldLabel.equals("name"))
      {
         String csname = "glsnamefont";

         if (category != null)
         {
            String attrVal = category.getAttribute("glossnamefont");

            if (attrVal != null)
            {
               csname = attrVal;
            }
         }

         Group grp = parser.getListener().createGroup();
         grp.addAll(list);

         list.clear();
         list.add(parser.getListener().getControlSequence(csname));
         list.add(grp);

         if (sty.isExtra())
         {
            list.add(parser.getListener().getControlSequence("glsxtrpostnamehook"));
            grp = parser.getListener().createGroup();
            list.add(grp);
            grp.add(glslabel);
         }
      }
      else if (category != null)
      {
         if (fieldLabel.equals("description"))
         {
            String csname = category.getAttribute("glossdescfont");

            if (csname != null)
            {
               Group grp = parser.getListener().createGroup();
               grp.addAll(list);

               list.clear();
               list.add(parser.getListener().getControlSequence(csname));
               list.add(grp);
            }
         }
         else if (fieldLabel.equals("symbol"))
         {
         }
      }

      return list;
   }

}
