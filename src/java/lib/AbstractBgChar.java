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

public abstract class AbstractBgChar extends Macro 
  implements BeginGroupObject,Expandable
{
   public AbstractBgChar(int code)
   {
      charCode = code;
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
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      parser.startGroup();
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
      AbstractGroup group = createGroup(parser);

      stack.popRemainingGroup(parser, group, PopStyle.DEFAULT, this);

      stack.push(group);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      AbstractGroup group = createGroup(parser);

      parser.popRemainingGroup(group, PopStyle.DEFAULT, this);

      parser.push(group);
   }

   public abstract String show(TeXParser parser)
    throws IOException;

   public int getCharCode()
   {
      return charCode;
   }

   public abstract AbstractGroup createGroup(TeXParser parser);

   private int charCode;
}

