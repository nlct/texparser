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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;

import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXObjectList;

public class EndGraf extends Primitive
{
   public EndGraf()
   {
      this("endgraf");
   }

   public EndGraf(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new EndGraf(getName());
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
      parser.getListener().getPar().process(parser, list);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.getListener().getPar().process(parser);
   }

   public boolean isPar()
   {// Don't cause a problem in the argument of short commands
      return false;
   }
}

