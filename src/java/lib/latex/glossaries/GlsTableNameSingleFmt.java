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

public class GlsTableNameSingleFmt extends AbstractGlsCommand
{
   public GlsTableNameSingleFmt(GlossariesSty sty)
   {
      this("glstableNameSingleFmt", sty);
   }

   public GlsTableNameSingleFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsTableNameSingleFmt(getName(), getSty());
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

         content.add(listener.getControlSequence("glstableNameTarget"));
         content.add(glslabel);

         boolean hasOther = getSty().glsTableHasOtherField(entry, stack);

         if (!entry.isFieldEmpty("description"))
         {
            content.add(listener.getControlSequence("glstableNameSinglePostName"));
            content.add(listener.getControlSequence("glstableNameSingleSuppl"));
            Group grp = listener.createGroup();
            content.add(grp);

            if (!entry.isFieldEmpty("symbol"))
            {
               grp.add(listener.getControlSequence("glstableSymbol"));
               grp.add(glslabel);
               grp.add(listener.getControlSequence("glstableNameSingleSymSep"));
            }

            if (hasOther)
            {
               grp.add(listener.getControlSequence("glstableOther"));
               grp.add(glslabel);
               grp.add(listener.getControlSequence("glstableOtherSep"));
            }

            grp.add(listener.getControlSequence("glstableDesc"));
            grp.add(glslabel);
         }
         else
         {
            if (!entry.isFieldEmpty("symbol"))
            {
               content.add(listener.getControlSequence("glstableNameSinglePostName"));
               content.add(listener.getControlSequence("glstableNameSingleSuppl"));
               Group grp = listener.createGroup();
               content.add(grp);

               grp.add(listener.getControlSequence("glstableSymbol"));
               grp.add(glslabel);

               if (hasOther)
               {
                  grp.add(listener.getControlSequence("glstableNameSingleSymSep"));
                  grp.add(listener.getControlSequence("glstableOther"));
                  grp.add(glslabel);
               }
            }
            else
            {
               if (hasOther)
               {
                  content.add(listener.getControlSequence("glstableNameSinglePostName"));
                  content.add(listener.getControlSequence("glstableNameSingleSuppl"));
                  Group grp = listener.createGroup();
                  content.add(grp);

                  grp.add(listener.getControlSequence("glstableOther"));
                  grp.add(glslabel);
               }
            }
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
