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
   public GlsEntryField(String name, GlossariesSty sty)
   {
      this(name, null, CaseChange.NO_CHANGE, sty);
   }

   public GlsEntryField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, sty);
   }

   public GlsEntryField(String name, boolean protect, GlossariesSty sty)
   {
      this(name, null, CaseChange.NO_CHANGE, protect, sty);
   }

   public GlsEntryField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, field, caseChange, false, sty);
   }

   public GlsEntryField(String name, String field, CaseChange caseChange,
     boolean protect, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
      this.caseChange = caseChange;
      this.protect = protect;
   }

   @Override
   public Object clone()
   {
      return new GlsEntryField(getName(), getField(), getCaseChange(), protect, getSty());
   }

   public AccSupp getAccSupp(GlsLabel glslabel, String fieldLabel)
   {
      if (glslabel == null) return null;

      GlossaryEntry entry = glslabel.getEntry();

      if (entry == null) return null;

      TeXObject val = entry.get(fieldLabel+"access");

      if (val == null)
      {
         if (fieldLabel.endsWith("plural"))
         {
            String singularFieldLabel;

            int idx = fieldLabel.length()-6;

            if (idx > 0)
            {
               singularFieldLabel = fieldLabel.substring(0, idx);
            }
            else
            {
               singularFieldLabel = "text";
            }

            val = entry.get(singularFieldLabel+"access");
         }
      }

      if (val == null) return null;

      String text;

      if (val instanceof TextualContentCommand)
      {
         text = ((TextualContentCommand)val).getText();
      }
      else if (val instanceof GenericCommand)
      {
         text = ((GenericCommand)val).getDefinition().toString(sty.getParser());
      }
      else
      {
         text = val.toString(sty.getParser());
      }

      return getAccSupp(glslabel, fieldLabel, text);
   }

   protected AccSupp getAccSupp(GlsLabel glslabel, String fieldLabel, String text)
   {
      if (fieldLabel.startsWith("short"))
      {
         return AccSupp.createAbbr(sty.getTarget(glslabel), text);
      }
      else
      {
         return AccSupp.createSymbol(text, sty.isFieldIcon(fieldLabel));
      }
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

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String fieldLabel = field;

      if (field == null)
      {
         fieldLabel = sty.getFieldName(popLabelString(parser, stack));
      }

      TeXObjectList expanded = 
         expand(glslabel, fieldLabel, caseChange, parser, stack);

      if (protect)
      {
         DataObjectList list = parser.getListener().createDataList(true);
         list.addAll(expanded);

         expanded = parser.getListener().createStack();
         expanded.add(list);
      }

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (protect)
      {
         return expandonce(parser, stack);
      }

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

      if (value == null || !value.canExpand())
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

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
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

   @Override
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
   protected boolean protect = false;
}
