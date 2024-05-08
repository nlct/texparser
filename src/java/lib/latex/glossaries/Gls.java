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

public class Gls extends AbstractGlsCommand
{
   public Gls(GlossariesSty sty)
   {
      this("gls", CaseChange.NO_CHANGE, false, sty);
   }

   public Gls(String name, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, caseChange, false, sty);
   }

   public Gls(String name, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
   }

   public Object clone()
   {
      Gls gls = new Gls(getName(), getCaseChange(), isPlural(), getSty());

      gls.setEntryLabelPrefix(getEntryLabelPrefix());
      gls.setDefaultOptions(defaultOptions);

      return gls;
   }

   public void setDefaultOptions(KeyValList options)
   {
      this.defaultOptions = options;
   }

   public KeyValList getDefaultOptions()
   {
      return defaultOptions;
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
      boolean localUnset = false;

      KeyValList keyValList = null;

      if (defaultOptions != null)
      {
         keyValList = (KeyValList)defaultOptions.clone();
      }

      KeyValList options = popOptKeyValList(stack, true);

      if (options != null)
      {
         if (keyValList == null)
         {
            keyValList = options;
         }
         else
         {
            keyValList.putAll(options);
         }
      }

      if (keyValList == null)
      {
         keyValList = new KeyValList();
      }

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject insert = popOptArg(TeXObjectList.POP_SHORT, parser, stack);

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
         boolean isUnset = entry.isUnset();

         Glossary glossary = entry.getGlossary(stack);
         String type = glossary.getType();

         listener.putControlSequence(true, glslabel.duplicate("glslabel"));

         listener.putControlSequence(true, new GenericCommand("glscustomtext"));

         if (insert == null)
         {
            listener.putControlSequence(true, new GenericCommand("glsinsert"));
         }
         else
         {
            listener.putControlSequence(true, 
              new GenericCommand("glsinsert", null, insert));
         }

         if (isPlural)
         {
            listener.putControlSequence(true, new AtFirstOfTwo("glsifplural"));
         }
         else
         {
            listener.putControlSequence(true, new AtSecondOfTwo("glsifplural"));
         }

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
         }

         ControlSequence cs = parser.getControlSequence("gls@"+type+"@entryfmt");

         if (cs == null)
         {
            cs = listener.getControlSequence("glsentryfmt");
         }

         addLinkText(substack, keyValList, cs, glslabel, parser, stack);

         TeXParserUtils.process(substack, parser, stack);

         if (!isUnset)
         {
            entry.unset(localUnset);
         }
      }

      substack.add(listener.getControlSequence("glspostlinkhook"));

      TeXParserUtils.process(substack, parser, stack);
   }

   protected void addLinkText(TeXObjectList substack,
     KeyValList glslinkOptions,
     ControlSequence entryfmtCs, GlsLabel glslabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      substack.add(parser.getListener().getControlSequence("@gls@link"));

      if (glslinkOptions != null)
      {
         substack.add(glslinkOptions);
      }

      substack.add(glslabel);
      substack.add(entryfmtCs);
   }

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

   private KeyValList defaultOptions;
}
