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

public class Param implements ParameterToken
{
   public Param(int digit)
   {
      setDigit(digit);
   }

   public Object clone()
   {
      return new Param(digit);
   }

   public int getDigit()
   {
      return digit;
   }

   public void setDigit(int digit)
   {
      this.digit = digit;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (digit <= 0)
      {
         TeXObject nextToken = stack.peek();

         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_BAD_PARAM,
            nextToken == null ? "" : nextToken.toString(parser));
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      if (digit <= 0)
      {
         TeXObject nextToken = parser.peekStack();

         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_BAD_PARAM,
            nextToken == null ? "" : nextToken.toString(parser));
      }
   }

   public String toString(TeXParser parser)
   {
      String charStr = new String(Character.toChars(parser.getParamChar()));

      return digit <= 0 ? String.format("%s", charStr)
                         : String.format("%s%d", charStr, digit);
   }

   public String format()
   {
      return (digit <= 0 ? "#" : "#"+digit);
   }

   public String toString()
   {
      return String.format("%s[%s]", 
        getClass().getName(),
        (digit <= 0 ? "#" : "#"+digit));
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(parser.getParamChar()));

      if (digit != -1)
      {
         list.add(parser.getListener().getOther(0x30+digit));
      }

      return list;
   }

   public boolean isPar()
   {
      return false;
   }

   public ParameterToken next()
   {
      return null;
   }

   public Param tail()
   {
      return this;
   }

   // -1 indicates #{ 
   // 0 indicates # not followed by digit or {
   private int digit;
}

