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

public class GlossEntry extends AbstractGlsCommand
{
   public GlossEntry(GlossariesSty sty)
   {
      this("glossentry", sty);
   }

   public GlossEntry(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlossEntry(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
System.out.println("FALLBACK!!");
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      TeXObjectList list = listener.createStack();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject locationList = popArg(parser, stack);

      list.add(new TeXCsRef("item"));
      list.add(listener.getOther('['));

      GlossaryEntry entry = glslabel.getEntry();

      if (entry != null)
      {
         TeXObject name = entry.get("name");

         if (name != null)
         {
            list.add((TeXObject)name.clone());
         }
      }

      list.add(listener.getOther(']'));

      if (entry != null)
      {
         TeXObject desc = entry.get("description");

         if (desc != null)
         {
            list.add((TeXObject)desc.clone());
         }
      }

      return list;
   }

}
