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
package com.dickimawbooks.texparserlib.latex.shortvrb;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class VerbChar extends ActiveChar
{
   public VerbChar(int codePoint)
   {
      this(codePoint, false);
   }

   public VerbChar(int codePoint, boolean showSpaces)
   {
      this.codePoint = codePoint;
      this.showSpaces = showSpaces;
   }

   public Object clone()
   {
      return new VerbChar(codePoint, showSpaces);
   }

   public int getCharCode()
   {
      return codePoint;
   }

   public boolean visibleSpaces()
   {
      return showSpaces;
   }

   public String toString(TeXParser parser)
   {
      return toString();
   }

   public String toString()
   {
      return new String(Character.toChars(codePoint));
   }

   public TeXObjectList expandonce(TeXParser parser)
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObjectList list = parser.popRemainingVerb(codePoint);

      parser.getListener().verb("verb", showSpaces, codePoint, 
        list.toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      if (stack == null || stack == parser)
      {
         process(parser);
         return;
      }

      StringBuilder builder = new StringBuilder();

      while (stack.size() > 0)
      {
         TeXObject object = stack.pop();

         if (object instanceof ActiveChar
         && ((ActiveChar)object).getCharCode() == codePoint)
         {
            break;
         }

         if (object instanceof CharObject
         && ((CharObject)object).getCharCode() == codePoint)
         {
            break;
         }

         if (object instanceof Comment)
         {
            builder.appendCodePoint(parser.getCommentChar());

            TeXObjectList list = new TeXObjectList();
            parser.scan(((Comment)object).getText(), list);

            stack.addAll(0, list);
         }
         else
         {
            builder.append(object.toString(parser));
         }
      }

      parser.getListener().verb("verb", showSpaces, codePoint, 
        builder.toString());
   }

   private int codePoint;

   private boolean showSpaces;
}
