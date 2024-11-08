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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class InlineGlsDef extends AbstractGlsCommand
{
   public InlineGlsDef(GlossariesSty sty)
   {
      this("inlineglsdef", sty);
   }

   public InlineGlsDef(String name, GlossariesSty sty)
   {
      this(name, "", sty);
   }

   public InlineGlsDef(String name, String prefix, GlossariesSty sty)
   {
      this(name, prefix, null, false, sty);
   }

   public InlineGlsDef(String name, String prefix, CaseChange caseChange,
     GlossariesSty sty)
   {
      this(name, prefix, null, false, caseChange, sty);
   }

   public InlineGlsDef(String name, String prefix, String field,
     boolean doUnset, GlossariesSty sty)
   {
      this(name, prefix, field, doUnset, CaseChange.NO_CHANGE, sty);
   }

   public InlineGlsDef(String name, String prefix, String field,
     boolean doUnset, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      setEntryLabelPrefix(prefix);
      this.field = field;
      this.doUnset = doUnset;
      this.caseChange = caseChange;
   }

   @Override
   public Object clone()
   {
      return new InlineGlsDef(getName(), getEntryLabelPrefix(),
        field, doUnset, caseChange, getSty());
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
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject optArg = popOptArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      parser.startGroup();

      TeXObjectList content = listener.createStack();

      if (caseChange == CaseChange.SENTENCE)
      {

         content.add(listener.getControlSequence("let"));

         if (field == null)
         {
            content.add(new TeXCsRef("glossentryname"));
            content.add(listener.getControlSequence("Glossentryname"));
         }
         else
         {
            content.add(new TeXCsRef("glossentrynameother"));
            content.add(listener.getControlSequence("Glossentrynameother"));
         }
      }

      content.add(listener.getControlSequence("glsadd"));
      content.add(glslabel);

      if (field == null)
      {
         content.add(listener.getControlSequence("glsxtrglossentry"));
         content.add(glslabel);
      }
      else
      {
         content.add(listener.getControlSequence("glsxtrglossentryother"));
         content.add(listener.createGroup());
         content.add(glslabel);
         content.add(listener.createGroup(field));
      }

      if (doUnset)
      {
         content.add(listener.getControlSequence("glsunset"));
         content.add(glslabel);
      }

      TeXParserUtils.process(content, parser, stack);

      if (parser.isDebugMode(TeXParser.DEBUG_SETTINGS))
      {
         parser.logMessage("ENDING GROUP AFTER PROCESSING "
          + toString() + " REMAINING STACK: "+stack);
      }

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected String field;
   protected boolean doUnset;
   protected CaseChange caseChange;
}
