/*
    Copyright (C) 2025 Nicola L.C. Talbot
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

public class PGls extends AbstractGlsCommand
{
   public PGls(GlossariesSty sty)
   {
      this("pgls", false, CaseChange.NO_CHANGE, false, sty);
   }

   public PGls(String name, boolean isFirst, CaseChange caseChange,
     GlossariesSty sty)
   {
      this(name, isFirst, caseChange, false, sty);
   }

   public PGls(String name, boolean isFirst, CaseChange caseChange,
      boolean isPlural, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.isPlural = isPlural;
      this.isFirst = isFirst;
   }

   public Object clone()
   {
      return new PGls(getName(), isFirst(), getCaseChange(),
        isPlural(), getSty());
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = popOptKeyValList(stack, true);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      boolean isUnset = glslabel.isUnset();

      String prefixField = "prefix";

      if (isFirst)
      {
         prefixField += "first";
      }

      if (isPlural)
      {
         prefixField += "plural";
      }

      TeXObjectList expanded = parser.getListener().createStack();

      TeXObject prefix = glslabel.getField(prefixField);

      String glscs = "gls";

      if (prefix == null)
      {
         switch (caseChange)
         {
            case TO_UPPER:
               glscs = "GLS";
            break;
            case SENTENCE:
               glscs = "Gls";
            break;
         }
      }
      else
      {
         TeXObject object = (TeXObject)prefix.clone();

         switch (caseChange)
         {
            case TO_UPPER:
               glscs = "GLS";
               object = TeXParserUtils.createStack(parser,
                new TeXCsRef("uppercase"),
                TeXParserUtils.createGroup(parser, object)
               );
            break;
            case SENTENCE:
               object = TeXParserUtils.createStack(parser,
                new TeXCsRef("makefirstuc"),
                TeXParserUtils.createGroup(parser, object)
               );
            break;
         }

         expanded.add(object, true);
         expanded.add(new TeXCsRef("glsprefixsep"));
      }

      expanded.add(new TeXCsRef(glscs));

      if (options != null)
      {
         expanded.add(parser.getListener().getOther('['));
         expanded.add(options);
         expanded.add(parser.getListener().getOther(']'));
      }

      expanded.add(TeXParserUtils.createGroup(parser, glslabel));

      return expanded;
   }

   public CaseChange getCaseChange()
   {
      return caseChange;
   }

   public boolean isPlural()
   {
      return isPlural;
   }

   public boolean isFirst()
   {
      return isFirst;
   }

   protected CaseChange caseChange;
   protected boolean isPlural, isFirst;
}
