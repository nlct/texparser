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

public class GlsType extends TextualContentCommand
{
   public GlsType(Glossary glossary)
   {
      this("glstype", glossary.getType(), glossary);
   }

   public GlsType(GlossaryEntry entry)
   {
      this("glstype", entry.getType(), null);
   }

   public GlsType(String name, String label)
   {
      super(name, label, null);
   }

   public GlsType(String name, String label, Glossary glossary)
   {
      super(name, label, glossary);
   }

   @Override
   public Object clone()
   {
      return new GlsType(getName(), getLabel(), getGlossary());
   }

   public String getLabel()
   {
      return getText();
   }

   public Glossary getGlossary()
   {
      return (Glossary)getData();
   }

   public void refresh(GlossariesSty sty)
   {
      Glossary glossary = getGlossary();

      if (glossary == null)
      {
         data = sty.getGlossary(getLabel());
      }
   }
}
