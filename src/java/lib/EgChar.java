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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class EgChar extends AbstractEgChar
{
   public EgChar()
   {
      this('}');
   }

   public EgChar(int code)
   {
      super(code);
   }

   public Object clone()
   {
      return new EgChar(getCharCode());
   }

   public int getTeXCategory()
   {
      return TYPE_EG;
   }

   public String toString(TeXParser parser)
   {
      return new String(Character.toChars(parser.getEgChar()));
   }


   public String show(TeXParser parser)
    throws IOException
   {
      return String.format("end-group character %s", 
       new String(Character.toChars(getCharCode())));
   }

   public Class getBgClass()
   {
      return BgChar.class;
   }
}

