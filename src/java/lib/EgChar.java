/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

public class EgChar extends Macro implements SingleToken
{
   public EgChar()
   {
      this('}');
   }

   public EgChar(int code)
   {
      charCode = code;
   }

   public EgChar(TeXParser parser)
   {
      this(parser.getEgChar());
   }

   @Override
   public Object clone()
   {
      return new EgChar(charCode);
   }

   @Override
   public int getCharCode()
   {
      return charCode;
   }

   @Override
   public int getCatCode()
   {
      return TeXParser.TYPE_EG;
   }

   @Override
   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   @Override
   public String toString(TeXParser parser)
   {
      return format();
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
      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_UNEXPECTED_EG);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_UNEXPECTED_EG);
   }

   public String show(TeXParser parser)
    throws IOException
   {
      return String.format("end-group character %s", format());
   }

   public boolean matches(BgChar bgChar)
   {
      return !(bgChar instanceof MathBg);
   }

   private int charCode;
}

