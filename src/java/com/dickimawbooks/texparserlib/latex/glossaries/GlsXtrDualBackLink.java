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

public class GlsXtrDualBackLink extends AbstractGlsCommand
{
   public GlsXtrDualBackLink(GlossariesSty sty)
   {
      this("GlsXtrDualBackLink", sty);
   }

   public GlsXtrDualBackLink(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrDualBackLink(getName(), getSty());
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
      TeXObject textArg = popArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      ControlSequence hyperlinkCs = parser.getControlSequence("glshyperlink");

      TeXObjectList substack = parser.getListener().createStack();

      if (hyperlinkCs == null)
      {
         substack.add(textArg, true);
      }
      else
      {
         String fieldLabel;

         ControlSequence fieldLabelCs = parser.getControlSequence("GlsXtrDualField");

         if (fieldLabelCs == null)
         {
            fieldLabel = "dual";
         }
         else
         {
            fieldLabel = parser.expandToString(fieldLabelCs, stack);
         }

         TeXObject val = getFieldValue(glslabel, fieldLabel);

         if (val == null || val.isEmpty())
         {
            substack.add(textArg, true);
         }
         else
         {
            substack.add(hyperlinkCs);
            substack.add(parser.getListener().getOther('['));
            substack.add(textArg, true);
            substack.add(parser.getListener().getOther(']'));
            substack.add(TeXParserUtils.createGroup(parser.getListener(), val));
         }
      }

      TeXParserUtils.process(substack, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
