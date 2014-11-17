/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
   public FontFamilyDeclaration(String name, int family)
   {
      super(name);
      this.family = family;
      this.orgFamily = TeXSettings.INHERIT;
   }

   public Object clone()
   {
      return new FontFamilyDeclaration(getName(), family);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      orgFamily = settings.getCurrentFontFamily();

      settings.setFontFamily(family);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontFamily(orgFamily);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }

   public int getFamily()
   {
      return family;
   }

   private int family, orgFamily;
}
