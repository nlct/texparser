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

public class GlsTableIfHasOtherField extends AbstractGlsCommand
{
   public GlsTableIfHasOtherField(GlossariesSty sty)
   {
      this("glstableifhasotherfield", sty);
   }

   public GlsTableIfHasOtherField(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsTableIfHasOtherField(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);
      TeXObject trueArg = popArg(parser, stack);
      TeXObject falseArg = popArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      TeXObjectList content;

      TeXParserListener listener = parser.getListener();

      if (entry == null)
      {
         sty.undefWarnOrError(stack,
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());

         content = listener.createStack();
      }
      else
      {
         TeXObject arg;

         if (getSty().glsTableHasOtherField(entry, stack))
         {
            arg = trueArg;
         }
         else
         {
            arg = falseArg;
         }

         if (parser.isStack(arg))
         {
            return (TeXObjectList)arg;
         }

         content = listener.createStack();
         content.add(arg);
      }

      return content;
   }

}
