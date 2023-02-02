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
import com.dickimawbooks.texparserlib.latex.etoolbox.EtoolboxList;

public class GlsTableChildEntries extends AbstractGlsCommand
{
   public GlsTableChildEntries(GlossariesSty sty)
   {
      this("glstableChildEntries", sty);
   }

   public GlsTableChildEntries(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsTableChildEntries(getName(), getSty());
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

         int childCount = entry.getInt("childcount", parser);

         if (childCount > 0)
         {
            TeXObjectList content = listener.createStack();

            TeXObject childlist = entry.get("childlist");

            content.add(listener.getControlSequence("glstablePreChildren"));
            content.add(listener.getControlSequence("begin"));
            content.add(listener.createGroup("glstablesubentries"));

            // should be an EtoolboxList
            if (childlist instanceof EtoolboxList)
            {
               EtoolboxList list = (EtoolboxList)childlist;

               for (int i = 0, n = list.size(); i < n; i++)
               {
                  if (i > 0)
                  {
                     content.add(listener.getControlSequence(
                       "glstableblocksubentrysep"));
                  }

                  content.add(listener.getControlSequence("glstableblocksubentry"));
                  content.add(TeXParserUtils.createGroup(listener, list.get(i)));
               }
            }
            else
            {
               String[] list = parser.expandToString(childlist,
                  stack).split("|");

               for (int i = 0; i < list.length; i++)
               {
                  if (i > 0)
                  {
                     content.add(listener.getControlSequence(
                       "glstableblocksubentrysep"));
                  }

                  content.add(listener.getControlSequence("glstableblocksubentry"));
                  content.add(listener.createGroup(list[i]));
               }
            }

            content.add(listener.getControlSequence("end"));
            content.add(listener.createGroup("glstablesubentries"));

            TeXParserUtils.process(content, parser, stack);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
