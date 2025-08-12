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

public class AtGlsAtAtLink extends AbstractGlsCommand
{
   public AtGlsAtAtLink(GlossariesSty sty)
   {
      this("@gls@@link", sty, false, false);
   }

   public AtGlsAtAtLink(String name, GlossariesSty sty, boolean checkModifier)
   {
      this(name, sty, checkModifier, false);
   }

   public AtGlsAtAtLink(String name, GlossariesSty sty, boolean checkModifier, boolean doUnset)
   {
      this(name, sty, checkModifier, doUnset, CaseChange.NO_CHANGE);
   }

   public AtGlsAtAtLink(String name, GlossariesSty sty, boolean checkModifier, boolean doUnset, CaseChange caseChange)
   {
      super(name, sty);
      this.checkModifier = checkModifier;
      this.doUnset = doUnset;
      this.caseChange = caseChange;
   }

   public Object clone()
   {
      AtGlsAtAtLink cs = new AtGlsAtAtLink(getName(), getSty(),
         checkModifier, doUnset, caseChange);

      cs.setEntryLabelPrefix(getEntryLabelPrefix());
      cs.setDefaultOptions(defaultOptions);

      return cs;
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
   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   protected TeXObject getLinkText(GlsLabel glslabel, 
      TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return popArg(parser, stack);
   }

   // leave indexing/recording to TeX
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

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

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject linkText = getLinkText(glslabel, parser, stack);

      TeXObjectList substack;

      switch (caseChange)
      {
         case SENTENCE:
           substack = listener.createStack();
           substack.add(listener.getControlSequence("glssentencecase"));
           substack.add(TeXParserUtils.createGroup(listener, linkText));
           linkText = substack;
         break;
         case TO_UPPER:
           substack = listener.createStack();
           substack.add(listener.getControlSequence("mfirstucMakeUppercase"));
           substack.add(TeXParserUtils.createGroup(listener, linkText));
           linkText = substack;
         break;
      }

      substack = listener.createStack();

      if (glslabel.getEntry() == null)
      {
         sty.undefWarnOrError(stack, GlossariesSty.ENTRY_NOT_DEFINED, 
           glslabel.getLabel());
      }
      else
      {
         // \let\do@gls@link@checkfirsthyper\relax
         parser.putControlSequence(true, 
            new AssignedControlSequence("do@gls@link@checkfirsthyper", new Relax()));

         if (sty.isExtra())
         {
            parser.putControlSequence(true,
              new GenericCommand(true, "glscustomtext", null, 
                    (TeXObject)linkText.clone()));

            substack.add(listener.getControlSequence("@glsxtr@field@linkdefs"));
         }

         substack.add(listener.getControlSequence("@gls@link"));

         if (keyValList != null)
         {
            substack.add(listener.getOther('['));
            substack.add(keyValList);
            substack.add(listener.getOther(']'));
         }

         substack.add(glslabel);

         Group grp = listener.createGroup();
         substack.add(grp);
         grp.add(linkText);

         if (doUnset)
         {
            substack.add(listener.getControlSequence("glsunset"));
            substack.add(glslabel);
         }

      }

      substack.add(new TeXCsRef("glspostlinkhook"));

      TeXParserUtils.process(substack, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean checkModifier, doUnset;
   protected CaseChange caseChange;
   private KeyValList defaultOptions;
}
