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

public class Comment extends Ignoreable
{
   public Comment()
   {
      builder = new StringBuilder();
   }

   public void appendText(String comment)
   {
      builder.append(comment);
   }

   public void appendCodePoint(int codePoint)
   {
      builder.appendCodePoint(codePoint);
   }

   public String getText()
   {
      return builder.toString();
   }

   public boolean isEmpty()
   {
      return builder.length() == 0;
   }

   public Object clone()
   {
      Comment obj = new Comment();
      obj.appendText(getText());

      return obj;
   }

   public String toString(TeXParser parser)
   {
      return String.format("%s%s%n",
         new String(Character.toChars(parser.getCommentChar())), 
         builder.toString());
   }

   public String toString()
   {
      return String.format("%s[comment=%s]%n", getClass().getName(),
         builder.toString());
   }

   public String format()
   {
      return String.format("%c%s%n", '%', builder.toString());
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(parser.getListener().getOther(parser.getCommentChar()));

      parser.scan(builder.toString(), list);

      return list; 
   }

   private StringBuilder builder;
}

