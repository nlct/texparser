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

public class UserNumber implements TeXNumber
{
   public UserNumber()
   {
      this(0);
   }

   public UserNumber(int num)
   {
      value = num;
   }

   public Object clone()
   {
      return new UserNumber(value);
   }

   public int number()
   {
      return value;
   }

   public int getValue()
   {
      return value;
   }

   public String toString()
   {
      return ""+value;
   }

   public String toString(TeXParser parser)
   {
      return ""+value;
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(""+value);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public boolean isPar()
   {
      return false;
   }

   private int value;
}
