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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class L2LActiveChar extends ActiveChar
{
   public L2LActiveChar(int charCode)
   {
      this.charCode = charCode;
   }

   public L2LActiveChar(char c)
   {
      this((int)c);
   }

   public int getCharCode()
   {
      return charCode;
   }

   public String toString(TeXParser parser)
   {
      return new String(Character.toChars(charCode));
   }

   public Object clone()
   {
      return new L2LActiveChar(charCode);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.write(toString(parser));

      while (stack.size() > 0)
      {
         TeXObject nextObj = stack.pop();

         if (!(nextObj instanceof Ignoreable))
         {
            nextObj.process(parser, stack);
            break;
         }

         writeable.write(nextObj.toString(parser));
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.write(toString(parser));

      TeXObject nextObj = parser.popStack();

      while (nextObj instanceof Ignoreable)
      {
         writeable.write(nextObj.toString(parser));
         nextObj = parser.popStack();
      }

      if (nextObj != null)
      {
         nextObj.process(parser);
      }
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return null;
   }

   private int charCode;
}

