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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class FontEncoding
{
   public FontEncoding(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public String getCharString(int charCode)
   {
      int code = getCharCode(charCode);

      if (code == CHAR_MAP_NONE)
      {
         code = charCode;
      }

      return new String(Character.toChars(code));
   }

   public int getCharCode(int charCode)
   {
      return CHAR_MAP_NONE;
   }

   public void addDefinitions(TeXSettings settings)
   {
   }

   private String name;

   public static final int CHAR_MAP_NONE=-1;
   public static final int CHAR_MAP_COMPOUND=-2;
}
