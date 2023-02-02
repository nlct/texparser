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

public class GlsXtrIndexCounterLink extends AbstractGlsCommand
{
   public GlsXtrIndexCounterLink(GlossariesSty sty)
   {
      this("GlsXtrIndexCounterLink", sty);
   }

   public GlsXtrIndexCounterLink(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrIndexCounterLink(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList substack = parser.getListener().createStack();

      TeXObject textArg = popArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      ControlSequence hyperrefCs = parser.getControlSequence("hyperref");

      if (hyperrefCs == null)
      {
         substack.add(textArg, true);
      }
      else
      {
         TeXObject val = getFieldValue(glslabel, "indexcounter");

         if (val == null || val.isEmpty())
         {
            substack.add(textArg, true);
         }
         else
         {
            substack.add(hyperrefCs);
            substack.add(parser.getListener().getOther('['));
            substack.add(parser.getListener().createString("wrglossary."), true);
            substack.add(val, true);
            substack.add(parser.getListener().getOther(']'));
            substack.add(TeXParserUtils.createGroup(parser.getListener(), textArg));
         }
      }

      return substack;
   }
}
