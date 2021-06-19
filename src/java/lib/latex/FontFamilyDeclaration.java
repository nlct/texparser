/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public class FontFamilyDeclaration extends RobustDeclaration
{
   public FontFamilyDeclaration(String name, int family)
   {
      super(name);
      this.family = family;
      this.orgFamily = TeXSettings.INHERIT;
   }

   @Override
   public Object clone()
   {
      return new FontFamilyDeclaration(getName(), family);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      pushEnd(parser);
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
   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontFamily(orgFamily);
      settings.removeDeclaration(this);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public int getFamily()
   {
      return family;
   }

   public boolean equals(Object object)
   {
      if (!(object instanceof FontFamilyDeclaration) || !super.equals(object))
      { 
         return false;
      }

      FontFamilyDeclaration dec = (FontFamilyDeclaration)object;

      return dec.family == family;
   }

   private int family, orgFamily;
}
