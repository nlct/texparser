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

public class UserBoolean implements TeXBoolean
{
   public UserBoolean(boolean isTrue)
   {
      value = isTrue;
   }

   public Object clone()
   {
      return new UserBoolean(value);
   }

   public boolean booleanValue()
   {
      return value;
   }

   public void setValue(boolean newValue)
   {
      value = newValue;
   }

   public String format()
   {
      return String.format("\\if%s", value);
   }

   public String toString()
   {
      return String.format("%s[value=%d]",
         getClass().getSimpleName(), value);
   }

   public String toString(TeXParser parser)
   {
      return String.format("%sif%s", 
        new String(Character.toChars(parser.getEscChar())), value);
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.getListener().createString(
        String.format("%sif%s", 
          new String(Character.toChars(parser.getEscChar())), value));
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.add(0, new TeXCsRef("if"+value));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.add(0, new TeXCsRef("if"+value));
   }

   public boolean isPar()
   {
      return false;
   }

   private boolean value;
}
