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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class Tab extends AbstractTeXObject implements SingleToken
{
   public Tab()
   {
      this('&');
   }

   public Tab(int charCode)
   {
      this.charCode = charCode;
   }

   public Tab(TeXParser parser)
   {
      this(parser.getTabChar());
   }

   @Override
   public Object clone()
   {
      return new Tab(charCode);
   }

   @Override
   public int getCharCode()
   {
      return charCode;
   }

   @Override
   public int getCatCode()
   {
      return TeXParser.TYPE_TAB;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return format();
   }

   @Override
   public String toString()
   {
      return getClass().getName();
   }

   @Override
   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      process(parser);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();

      settings.startColumn();
   }

   protected int charCode;
}

