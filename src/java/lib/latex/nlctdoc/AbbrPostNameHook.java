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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class AbbrPostNameHook extends AbstractGlsCommand
{
   public AbbrPostNameHook(GlossariesSty sty)
   {
      this("abbrpostnamehook", sty);
   }

   public AbbrPostNameHook(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new AbbrPostNameHook(getName(), getSty());
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

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      ControlSequence labelCs = listener.getControlSequence("glscurrententrylabel");

      GlossaryEntry entry = null;

      if (labelCs instanceof GlsLabel)
      {
         entry = ((GlsLabel)labelCs).getEntry();
      }

      if (entry == null)
      {
         entry = sty.getEntry(parser.expandToString(labelCs, stack));
      }

      if (entry != null)
      {
         TeXObject longVal = entry.get("long");

         if (longVal == null)
         {
            TeXObject dualVal = entry.get("dual");

            if (dualVal != null)
            {
               String dual = parser.expandToString(dualVal, stack);

               GlossaryEntry dualEntry = sty.getEntry(dual);

               if (dualEntry != null)
               {
                  longVal = dualEntry.get("long");
               }
            }
         }

         if (longVal != null)
         {
            TeXObjectList list = listener.createStack();

            list.add(listener.getControlSequence("space"));
            list.add(listener.getOther('('));
            list.add(longVal, true);
            list.add(listener.getOther(')'));

            TeXParserUtils.process(list, parser, stack);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
