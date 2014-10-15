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

public abstract class CharObject implements TeXObject
{
   public CharObject(int charCode)
   {
      setCharCode(charCode);
   }

   public abstract Object clone();

   public String toString(TeXParser parser)
   {
      return toString();
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder(2);
      builder.appendCodePoint(getCharCode());
      return builder.toString();
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(this);
      return list;
   }

   public int getCharCode()
   {
      return charCode;
   }

   public void setCharCode(int charCode)
   {
      this.charCode = charCode;
   }

   public void process(TeXParser parser) throws IOException
   {
      int c = parser.getSettings().getCharCode(charCode);

      parser.getListener().getWriteable().writeCodePoint(c == -1 ? charCode : c);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   protected int charCode;
}
