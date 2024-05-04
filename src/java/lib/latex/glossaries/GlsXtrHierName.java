/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsXtrHierName extends AbstractGlsCommand
{
   public GlsXtrHierName(GlossariesSty sty)
   {
      this("glsxtrhiername", CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrHierName(String name, CaseChange caseChange,
      boolean caseChangeRootOnly, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
      this.caseChangeRootOnly = caseChangeRootOnly;
   }

   @Override
   public Object clone()
   {
      return new GlsXtrHierName(name, caseChange, caseChangeRootOnly, sty);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      addEntry(glslabel.getEntry(), expanded, parser, stack);

      return expanded;
   }

   protected void addEntry(GlossaryEntry entry, TeXObjectList list,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      if (entry != null)
      {
         GlossaryEntry parentEntry = entry.getParent(stack);

         boolean hasShort = entry.hasField("short");

         if (!list.isEmpty())
         {
            list.push(listener.getControlSequence("glsxtrhiernamesep"));
         }

         String csname;

         if (parentEntry != null && caseChangeRootOnly)
         {
            csname = hasShort ? "glsfmtshort" : "glsfmtname";
         }
         else
         {
            switch (caseChange)
            {
               case SENTENCE:
                  csname = hasShort ? "Glsfmtshort" : "Glsfmtname";
               break;
               case TO_UPPER:
                  csname = hasShort ? "GLSfmtshort" : "GLSfmtname";
               break;
               default:
                  csname = hasShort ? "glsfmtshort" : "glsfmtname";
            }
         }

         list.push(listener.createGroup(entry.getLabel()));
         list.push(listener.getControlSequence(csname));

         if (parentEntry != null)
         {
            addEntry(parentEntry, list, parser, stack);
         }
      }
   }

   protected CaseChange caseChange;
   protected boolean caseChangeRootOnly = false;
}
