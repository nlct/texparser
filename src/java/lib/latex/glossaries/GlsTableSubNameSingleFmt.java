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

public class GlsTableSubNameSingleFmt extends AbstractGlsCommand
{
   public GlsTableSubNameSingleFmt(GlossariesSty sty)
   {
      this("glstableSubNameSingleFmt", sty);
   }

   public GlsTableSubNameSingleFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsTableSubNameSingleFmt(getName(), getSty());
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

         content.add(listener.getControlSequence("glstableSubNameTarget"));
         content.add(glslabel);

         boolean hasOther = getSty().glsTableHasOtherField(entry, stack);

         if (entry.hasField("description"))
         {
            content.add(listener.getControlSequence("glstableNameSinglePostSubName"));
            content.add(listener.getControlSequence("glstableNameSingleSubSuppl"));
            Group grp = listener.createGroup();
            content.add(grp);

            if (entry.hasField("symbol"))
            {
               grp.add(listener.getControlSequence("glstableSubSymbolFmt"));
               grp.add(glslabel);

               grp.add(listener.getControlSequence("glstableNameSingleSymSep"));
            }

            if (hasOther)
            {
               grp.add(listener.getControlSequence("glstableSubOther"));
               grp.add(glslabel);
               grp.add(listener.getControlSequence("glstableOtherSep"));
            }

            grp.add(listener.getControlSequence("glstableSubDescFmt"));
            grp.add(glslabel);
         }
         else
         {
            if (entry.hasField("symbol"))
            {
               content.add(listener.getControlSequence("glstableNameSinglePostSubName"));
               content.add(listener.getControlSequence("glstableNameSingleSubSuppl"));
               Group grp = listener.createGroup();
               content.add(grp);

               grp.add(listener.getControlSequence("glstableSubSymbolFmt"));
               grp.add(glslabel);

               if (hasOther)
               {
                  grp.add(listener.getControlSequence("glstableNameSingleSymSep"));
                  grp.add(listener.getControlSequence("glstableSubOther"));
                  grp.add(glslabel);
               }
            }
            else if (hasOther)
            {
               content.add(listener.getControlSequence("glstableNameSingleSubPostName"));
               content.add(listener.getControlSequence("glstableNameSinglePostSubName"));
               content.add(listener.getControlSequence("glstableNameSingleSubSuppl"));
               Group grp = listener.createGroup();
               content.add(grp);

               grp.add(listener.getControlSequence("glstableSubOther"));
               grp.add(glslabel);
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
