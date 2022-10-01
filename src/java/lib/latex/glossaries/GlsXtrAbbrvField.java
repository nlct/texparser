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

public class GlsXtrAbbrvField extends AbstractGlsCommand
{
   public GlsXtrAbbrvField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrAbbrvField(String name, String field, boolean isPlural, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsXtrAbbrvField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, field, caseChange, false, sty);
   }

   public GlsXtrAbbrvField(String name, String field, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      this(name, field, caseChange, isPlural, field.startsWith("short"), sty);
   }

   public GlsXtrAbbrvField(String name, String field, CaseChange caseChange, boolean isPlural, boolean isShortForm, GlossariesSty sty)
   {
      super(name, sty);
      this.field = field;
      this.caseChange = caseChange;
      this.isPlural = isPlural;
      this.isShortForm = isShortForm;
   }

   @Override
   public Object clone()
   {
      return new GlsXtrAbbrvField(getName(), getField(), getCaseChange(),
         isPlural(), isShortForm(), getSty());
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

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   protected KeyValList createDefaultOptions(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean localUnset = false;

      KeyValList defOptions = createDefaultOptions(parser, stack);

      KeyValList keyValList = popOptKeyValList(stack, true);

      if (keyValList == null)
      {
         if (defOptions == null)
         {
            keyValList = new KeyValList();
         }
         else
         {
            keyValList = defOptions;
         }
      }
      else if (defOptions != null)
      {
         defOptions.putAll(keyValList);
         keyValList = defOptions;
      }

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject insert = popOptArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList substack = listener.createStack();

      if (entry == null)
      {
         sty.undefWarnOrError(stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         listener.putControlSequence(true, glslabel.duplicate("glslabel"));

         listener.putControlSequence(true, new AtSecondOfTwo("glsxtrifwasfirstuse"));

         if (isPlural)
         {
            listener.putControlSequence(true, new AtFirstOfTwo("glsifplural"));
         }
         else
         {
            listener.putControlSequence(true, new AtSecondOfTwo("glsifplural"));
         }

         listener.putControlSequence(true, new GenericCommand("glsinsert"));

         String csname = "gls";

         switch (caseChange)
         {
            case SENTENCE:
              listener.putControlSequence(true,
                new AtNumberOfNumber("glscapscase", 2, 3));
              csname = "Gls";
            break;
            case TO_UPPER:
              listener.putControlSequence(true,
                new AtNumberOfNumber("glscapscase", 3, 3));
              csname = "GLS";
            break;
            default:
              listener.putControlSequence(true,
                 new AtNumberOfNumber("glscapscase", 1, 3));

              if (insert != null && !insert.isEmpty())
              {
                 TeXObjectList list = listener.createStack();
                 list.add(listener.getControlSequence("mfirstucMakeUppercase"));
                 Group grp = listener.createGroup();
                 list.add(grp);
                 grp.add(insert);
                 insert = list;
              }
         }

         TeXObjectList linktext = listener.createStack();

         csname += "xtr";

         if (isShortForm())
         {
            csname += "short";
         }
         else
         {
            csname += "long";
         }

         if (isPlural)
         {
            csname += "pl";
         }

         csname += "format";

         linktext.add(listener.getControlSequence(csname));

         linktext.add(glslabel);

         Group grp = listener.createGroup();
         linktext.add(grp);

         if (insert != null && !insert.isEmpty())
         {
            grp.add(insert, true);
         }

         if (isShortForm())
         {
            linktext.add(listener.getControlSequence("glsabbrvfont"));
         }
         else
         {
            linktext.add(listener.getControlSequence("glslongfont"));
         }

         listener.putControlSequence(true, new GenericCommand("glscustomtext",
           null, linktext));

         ControlSequence entryFmtCs = parser.getControlSequence(
           "gls@"+entry.getType()+"@entryfmt");

         if (entryFmtCs == null)
         {
            entryFmtCs = listener.getControlSequence("glsentryfmt");
         }

         substack.add(listener.getControlSequence("glssetabbrvfmt"));
         substack.add(listener.createGroup(entry.getCategory()));

         substack.add(listener.getControlSequence("glsxtrsaveinsert"));
         substack.add(glslabel);

         grp = listener.createGroup();

         if (insert != null && !insert.isEmpty())
         {
            grp.add(insert, true);
         }

         substack.add(grp);

         if (!isShortForm())
         {
            ControlSequence cs = parser.getControlSequence("glsxtrsetlongfirstuse");

            if (cs != null)
            {
               substack.add(cs);
               substack.add(glslabel);
            }
         }

         substack.add(listener.getControlSequence("@gls@link"));
         substack.add(keyValList);
         substack.add(glslabel);
         substack.add(entryFmtCs);
      }

      substack.add(listener.getControlSequence("glspostlinkhook"));

      TeXParserUtils.process(substack, parser, stack);
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

   public boolean isPlural()
   {
      return isPlural;
   }

   public boolean isShortForm()
   {
      return isShortForm;
   }

   protected CaseChange caseChange;
   protected boolean isPlural, isShortForm;
   protected String field;
}
