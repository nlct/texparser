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

import com.dickimawbooks.texparserlib.*;

public class Verb extends Command
{
   public Verb()
   {
      this("verb");
   }

   public Verb(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Verb(getName());
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

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.pop();

      boolean isStar = false;

      if (object instanceof CharObject
        && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         object = stack.pop();
      }

      String text = object.toString(parser);

      char delim = text.charAt(0);

      text = text.substring(1, text.length()-1);

      parser.getListener().verb(getName(), isStar, delim, text);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject object = parser.pop();

      boolean isStar = false;

      if (object instanceof CharObject
        && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         object = parser.pop();
      }

      String text = object.toString(parser);

      char delim = text.charAt(0);

      text = text.substring(1, text.length()-1);

      parser.getListener().verb(getName(), isStar, delim, text);
   }

}
