/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public abstract class AbstractEgChar extends Macro implements EndGroupObject,Expandable
{
   public AbstractEgChar(int code)
   {
      charCode = code;
   }

   public int getCharCode()
   {
      return charCode;
   }

   @Override
   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   public abstract String toString(TeXParser parser);

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      parser.endGroup();
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }


   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      parser.endGroup();
   }

   public abstract String show(TeXParser parser)
    throws IOException;

   @Override
   public boolean matches(BeginGroupObject bg)
   {
      return bg == null ? false : (bg.getClass().equals(getBgClass()));
   }

   public abstract Class getBgClass();

   private int charCode;
}

