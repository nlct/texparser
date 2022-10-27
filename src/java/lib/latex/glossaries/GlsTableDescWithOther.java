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

public class GlsTableDescWithOther extends AbstractGlsCommand
{
   public GlsTableDescWithOther(GlossariesSty sty)
   {
      this("glstableDescWithOther", sty);
   }

   public GlsTableDescWithOther(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsTableDescWithOther(getName(), getSty());
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
      GlsLabel glslabel = popEntryLabel(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      if (entry == null)
      {
         sty.undefWarnOrError(stack,
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList content = listener.createStack();

         boolean hasOther = getSty().glsTableHasOtherField(entry, stack);

         if (entry.hasField("description"))
         {
            if (hasOther)
            {
               content.add(listener.getControlSequence("glstableOther"));
               content.add(glslabel);
               content.add(listener.getControlSequence("glstableOtherSep"));
            }

            content.add(listener.getControlSequence("glstableDescFmt"));
            content.add(glslabel);
         }
         else if (hasOther)
         {
            content.add(listener.getControlSequence("glstableOther"));
            content.add(glslabel);
         }

         TeXParserUtils.process(content, parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
