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

public class GlsSetAttribute extends AbstractGlsCommand
{
   public GlsSetAttribute(GlossariesSty sty)
   {
      this("glssetattribute", false, sty);
   }

   public GlsSetAttribute(String name, boolean argIsCatLabel, GlossariesSty sty)
   {
      super(name, sty);
      this.argIsCatLabel = argIsCatLabel;
   }

   @Override
   public Object clone()
   {
      return new GlsSetAttribute(getName(), argIsCatLabel, getSty());
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
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String catLabel = null;

      if (argIsCatLabel)
      {
         catLabel = popLabelString(parser, stack);
      }
      else
      {
         GlsLabel glslabel = popEntryLabel(parser, stack);

         GlossaryEntry entry = glslabel.getEntry();

         if (entry != null)
         {
            catLabel = entry.getCategory();
         }
      }

      String attributeName = popLabelString(parser, stack);
      String attributeValue = popLabelString(parser, stack);

      if (catLabel == null)
      {
         return;
      }

      String[] categories = catLabel.split(" *, *");

      String[] names = attributeName.split(" *, *");

      for (String cat : categories)
      {
         for (String attr : names)
         {
            sty.setAttribute(cat, attr, attributeValue);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      process(parser, parser);
   }

   protected boolean argIsCatLabel = false;
}
