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

public class GlsEntryField extends AbstractGlsCommand
{
   public GlsEntryField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, sty);
   }

   public GlsEntryField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
      this.caseChange = caseChange;
   }

   public Object clone()
   {
      return new GlsEntryField(getName(), getField(), getCaseChange(), getSty());
   }

   public TeXObject getFieldValue(GlsLabel glslabel, String fieldLabel)
   {
      if (glslabel == null) return null;

      GlossaryEntry entry = glslabel.getEntry();

      if (entry == null) return null;

      TeXObject value = entry.get(fieldLabel);

      if (value != null)
      {
         value = (TeXObject)value.clone();
      }

      return value;
   }

   protected TeXObjectList expand(GlsLabel glslabel, String fieldLabel,
     CaseChange caseChange, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject value = getFieldValue(glslabel, fieldLabel);

      TeXObjectList list = new TeXObjectList();

      if (value != null)
      {
         switch (caseChange)
         {
            case SENTENCE:
              list.add(new TeXCsRef("makefirstuc"));
              Group grp = parser.getListener().createGroup();
              grp.add(value);
              list.add(grp);
            break;
            case TITLE:
              list.add(new TeXCsRef("glsxtrfieldtitlecasecs"));
              grp = parser.getListener().createGroup();
              grp.add(value);
              list.add(grp);
            break;
            case TO_UPPER:
              list.add(new TeXCsRef("mfirstucMakeUppercase"));
              grp = parser.getListener().createGroup();
              grp.add(value);
              list.add(grp);
            break;
            default:
              list.add(value);
         }
      }

      return list;
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

      return expand(glslabel, fieldLabel, caseChange, parser, stack);
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (caseChange != CaseChange.NO_CHANGE)
      {
         if (parser == stack || stack == null)
         {
            return expandonce(parser).expandfully(parser);
         }
         else
         {
            return expandonce(parser, stack).expandfully(parser, stack);
         }
      }

      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = popLabelString(parser, stack);
      }

      TeXObject value = getFieldValue(glslabel, fieldLabel);

      if (value == null || !(value instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();

         if (value != null)
         {
            list.add(value);
         }

         return list;
      }

      TeXObjectList expanded;

      if (parser == stack || stack == null)
      {
         expanded = ((Expandable)value).expandfully(parser, stack);
      }
      else
      {
         expanded = ((Expandable)value).expandfully(parser);
      }

      if (expanded == null)
      {
         TeXObjectList list = new TeXObjectList();

         list.add(value);

         return list;
      }
      else
      {
         return expanded;
      }
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = popLabelString(parser, stack);
      }

      TeXObject value = getFieldValue(glslabel, fieldLabel);

      if (value != null)
      {
         switch (caseChange)
         {
            case SENTENCE:
              Group grp = parser.getListener().createGroup();
              grp.add(value, true);
              stack.push(grp);
              stack.push(new TeXCsRef("makefirstuc"));
            break;
            case TITLE:
              grp = parser.getListener().createGroup();
              grp.add(value, true);
              stack.push(grp);
              stack.push(new TeXCsRef("glsxtrfieldtitlecasecs"));
            break;
            case TO_UPPER:
              grp = parser.getListener().createGroup();
              grp.add(value);
              stack.push(grp);
              stack.push(new TeXCsRef("mfirstucMakeUppercase"));
            break;
            default:
              stack.push(value);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
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
