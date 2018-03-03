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

public class Par implements TeXObject
{
   public Par()
   {
      this("par");
   }

   public Par(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public Object clone()
   {
      return new Par(getName());
   }

   public String toString(TeXParser parser)
   {
      return String.format("%n%n");
   }

   public String format()
   {
      return String.format("\\%s ", name);
   }

   public String toString()
   {
      return String.format("%s%n", getClass().getName());
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return parser.string(String.format("%s%s", 
        new String(Character.toChars(parser.getEscChar())), name));
   }

   public void process(TeXParser parser, TeXObjectList list)
      throws IOException
   {
   }

   public void process(TeXParser parser)
      throws IOException
   {
   }

   public boolean isPar()
   {
      return true;
   }

   private String name;
}

