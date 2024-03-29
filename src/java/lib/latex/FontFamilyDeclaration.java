/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class FontFamilyDeclaration extends Declaration
{
   public FontFamilyDeclaration(String name, int familyId)
   {
      super(name);
      this.orgFamily = TeXFontFamily.INHERIT;

      switch (familyId)
      {
         case TeXSettings.INHERIT:
           family = TeXFontFamily.INHERIT;
         break;
         case TeXSettings.FAMILY_RM:
           family = TeXFontFamily.RM;
         break;
         case TeXSettings.FAMILY_SF:
           family = TeXFontFamily.SF;
         break;
         case TeXSettings.FAMILY_TT:
           family = TeXFontFamily.TT;
         break;
         case TeXSettings.FAMILY_CAL:
           family = TeXFontFamily.CAL;
         break;
         default:
          throw new IllegalArgumentException("Invalid family ID "+familyId);
      }
   }

   public FontFamilyDeclaration(String name, TeXFontFamily family)
   {
      super(name);
      this.family = family;
      this.orgFamily = TeXFontFamily.INHERIT;
   }

   @Override
   public Object clone()
   {
      return new FontFamilyDeclaration(getName(), family);
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
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
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
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
   public void process(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      orgFamily = settings.getCurrentFontFamily();

      settings.setFontFamily(family);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontFamily(orgFamily);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public TeXFontFamily getFamily()
   {
      return family;
   }

   private TeXFontFamily family, orgFamily;
}
