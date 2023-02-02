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

public class GlsXtrFull extends AbstractGlsCommand
{
   public GlsXtrFull(String name, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrFull(String name, boolean isPlural, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsXtrFull(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public GlsXtrFull(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   @Override
   public Object clone()
   {
      return new GlsXtrFull(getName(), getCaseChange(), isPlural(), getSty());
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

         if (isPlural)
         {
            listener.putControlSequence(true, new AtFirstOfTwo("glsifplural"));
         }
         else
         {
            listener.putControlSequence(true, new AtSecondOfTwo("glsifplural"));
         }

         listener.putControlSequence(true, new GenericCommand("glsinsert"));

         switch (caseChange)
         {
            case SENTENCE:
              listener.putControlSequence(true,
                new AtNumberOfNumber("glscapscase", 2, 3));
            break;
            case TO_UPPER:
              listener.putControlSequence(true,
                new AtNumberOfNumber("glscapscase", 3, 3));
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
         linktext.add(listener.getControlSequence("glsxtrinlinefullformat"));
         linktext.add(glslabel);

         Group grp = listener.createGroup();
         linktext.add(grp);

         if (insert != null && !insert.isEmpty())
         {
            grp.add(insert, true);
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

         substack.add(listener.getControlSequence("glsxtrfullsaveinsert"));
         substack.add(glslabel);

         grp = listener.createGroup();

         if (insert != null && !insert.isEmpty())
         {
            grp.add(insert, true);
         }

         substack.add(grp);

         substack.add(new TeXCsRef("glsxtrsetupfulldefs"));
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

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   public boolean isPlural()
   {
      return isPlural;
   }

   protected CaseChange caseChange;
   protected boolean isPlural;
}
