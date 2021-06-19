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

import com.dickimawbooks.texparserlib.*;

public class Verbatim extends RobustDeclaration
{
   public Verbatim()
   {
      this("verbatim");
   }

   public Verbatim(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Verbatim(getName());
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();
      orgFamily = settings.getCurrentFontFamily();
      settings.setFontFamily(TeXSettings.FAMILY_TT);

      Writeable writeable = parser.getListener().getWriteable();

      boolean isStar = (getName().endsWith("*"));

      for (TeXObject object : list)
      {
         if (isStar && (object instanceof Space))
         {
            writeable.writeCodePoint(0x2423);
         }
         else
         {
            writeable.write(object.toString(parser));
         }
      }
   }

   @Override
   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontFamily(orgFamily);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   private int orgFamily;
}
