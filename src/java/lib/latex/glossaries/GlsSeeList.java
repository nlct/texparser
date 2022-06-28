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

public class GlsSeeList extends Command
{
   public GlsSeeList()
   {
      this("glsseeformat");
   }

   public GlsSeeList(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsSeeList(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String[] labels = popLabelString(parser, stack).trim().split(" *, *");

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      ControlSequence itemCs = parser.getControlSequence("@glsxtr@seeitem");

      if (itemCs == null)
      {
         itemCs = listener.getControlSequence("glsseeitem");
      }

      ControlSequence firstitemCs = parser.getControlSequence("@glsxtr@seefirstitem");

      if (firstitemCs == null)
      {
         firstitemCs = itemCs;
      }

      ControlSequence sepCs = listener.getControlSequence("glsseesep");
      ControlSequence lastSepCs = listener.getControlSequence("glsseelastsep");
      ControlSequence lastOxfordSepCs = parser.getControlSequence(
         "glsseelastoxfordsep");

      if (lastOxfordSepCs == null)
      {
         lastOxfordSepCs = lastSepCs;
      }

      for (int i = 0; i < labels.length; i++)
      {
         if (i > 0)
         {
            if (i == labels.length-1)
            {
               if (i == 1)
               {
                  expanded.add(lastSepCs);
               }
               else
               {
                  expanded.add(lastOxfordSepCs);
               }
            }
            else
            {
               expanded.add(sepCs);
            }

            expanded.add(itemCs);
         }
         else
         {
            expanded.add(firstitemCs);
         }

         expanded.add(listener.createGroup(labels[i]));
      }

      return expanded;
   }

}
