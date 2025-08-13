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

public class Dglslink extends AtGlsAtAtLink
{
   public Dglslink(GlossariesSty sty)
   {
      this("dglslink", sty, true, false);
   }

   public Dglslink(String name, boolean doUnset, GlossariesSty sty)
   {
      this(name, sty, true, doUnset);
   }
   
   public Dglslink(String name, GlossariesSty sty, boolean checkModifier,
     boolean doUnset)
   {
      super(name, sty, checkModifier, doUnset);
   }

   public Object clone()
   {
      return new Dglslink(getName(), getSty(), checkModifier, doUnset);
   }

   @Override
   protected GlsLabel popEntryLabel(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String label = popLabelString(parser, stack);

      GlossaryEntry entry = sty.getDualEntry(label);

      if (entry == null)
      {
         return new GlsLabel("@@glslabel@"+label, label);
      }
      else
      {
         return new GlsLabel("@@glslabel@"+label, entry.getLabel(), entry);
      }
   }

   @Override
   protected void preGlsHook(GlsLabel glslabel,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = parser.getControlSequence("predglslinkhook");

      if (cs != null)
      {
         TeXObjectList substack = parser.getListener().createStack();

         substack.add(cs);
         substack.add(glslabel);

         TeXParserUtils.process(substack, parser, stack);
      }
   }

}
