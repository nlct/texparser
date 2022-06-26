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

public class GlsXtrPostDescription extends AbstractGlsCommand
{
   public GlsXtrPostDescription(GlossariesSty sty)
   {
      this("glsxtrpostdescription", sty);
   }

   public GlsXtrPostDescription(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsXtrPostDescription(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = parser.getListener().createStack();

      ControlSequence labelCs = parser.getListener().getControlSequence(
        "glscurrententrylabel");

      GlossaryEntry entry = null;

      if (labelCs instanceof GlsLabel)
      {
         entry = ((GlsLabel)labelCs).getEntry();
      }

      if (entry == null)
      {
         String label = parser.expandToString(labelCs, stack);
         entry = sty.getEntry(label);
      }

      String category = null;

      if (entry != null)
      {
         category = entry.getCategory();
      }

      if (category != null)
      {
         ControlSequence cs = parser.getControlSequence("glsxtrpostdesc"
           + category);

         if (cs != null)
         {
            list.add(cs);
         }
      }

      return list;
   }

}
