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

public class GlsXtrIfHasField extends AbstractGlsCommand
{
   public GlsXtrIfHasField(GlossariesSty sty)
   {
      this("glsxtrifhasfield", sty);
   }

   public GlsXtrIfHasField(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrIfHasField(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean addScope = popModifier(parser, stack, '*') == -1;

      String fieldLabel = sty.getFieldName(popLabelString(parser, stack));

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject value = getFieldValue(glslabel, fieldLabel);

      boolean hasField = (value != null && !value.isEmpty());

      TeXObject trueCode = popArg(parser, stack);
      TeXObject falseCode = popArg(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();
      TeXObjectList content = expanded;

      if (addScope)
      {
         content = parser.getListener().createGroup();
      }

      content.add(new TeXCsRef("def"));
      content.add(new TeXCsRef("glscurrentfieldvalue"));

      Group grp = parser.getListener().createGroup();
      content.add(grp);

      if (hasField)
      {
         grp.add(value);
         content.add(trueCode, true);
      }
      else
      {
         content.add(falseCode, true);
      }

      return expanded;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean addScope = popModifier(parser, stack, '*') == -1;

      String fieldLabel = sty.getFieldName(popLabelString(parser, stack));

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject value = getFieldValue(glslabel, fieldLabel);

      boolean hasField = (value != null && !value.isEmpty());

      TeXObject trueCode = popArg(parser, stack);
      TeXObject falseCode = popArg(parser, stack);

      if (addScope)
      {
         parser.startGroup();
      }

      if (hasField)
      {
         parser.putControlSequence(true, new GenericCommand(true,
           "glscurrentfieldvalue", null, value));
      }
      else
      {
         parser.putControlSequence(true, new GenericCommand(true,
           "glscurrentfieldvalue"));
      }

      if (hasField)
      {
         TeXParserUtils.process(trueCode, parser, stack);
      }
      else
      {
         TeXParserUtils.process(falseCode, parser, stack);
      }

      if (addScope)
      {
         parser.endGroup();
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
