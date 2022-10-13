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

public class GlsLabel extends TextualContentCommand
{
   public GlsLabel(GlossaryEntry entry)
   {
      this("glslabel", entry.getLabel(), entry);
   }

   public GlsLabel(String label, GlossaryEntry entry)
   {
      this("glslabel", label, entry);
   }

   public GlsLabel(String name, String label)
   {
      this(name, label, null);
   }

   public GlsLabel(String name, String label, GlossaryEntry entry)
   {
      super(name, label, entry);
   }

   @Override
   public Object clone()
   {
      return new GlsLabel(getName(), getLabel(), getEntry());
   }

   public String getLabel()
   {
      return getText();
   }

   public GlossaryEntry getEntry()
   {
      return (GlossaryEntry)getData();
   }

   public GlossaryEntry getEntry(GlossariesSty sty)
   {
      if (getData() == null)
      {
         refresh(sty);
      }

      return (GlossaryEntry)getData();
   }

   public String getCategory()
   {
      GlossaryEntry entry = getEntry();
      return entry == null ? null : entry.getCategory();
   }

   public boolean hasParent()
   throws IOException
   {
      GlossaryEntry entry = getEntry();
      return entry == null ? false : entry.hasParent();
   }

   public GlossaryEntry getParent(TeXObjectList stack)
   throws IOException
   {
      GlossaryEntry entry = getEntry();
      return entry == null ? null : entry.getParent(stack);
   }

   public TeXObject getField(String fieldLabel)
   {
      GlossaryEntry entry = getEntry();
      return entry == null ? null : entry.get(fieldLabel);
   }

   public int getLevel()
   {
      GlossaryEntry entry = getEntry();
      return entry == null ? -1 : entry.getLevel();
   }

   public void refresh(GlossariesSty sty)
   {
      GlossaryEntry entry = getEntry();

      if (entry == null)
      {
         data = sty.getEntry(getLabel());
      }
   }

   @Override
   public boolean equals(Object other)
   {
      return (other instanceof GlsLabel)
          && getLabel().equals(((GlsLabel)other).getLabel());
   }
}
