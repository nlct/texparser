/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

   @Override
   public Object clone()
   {
      return new Par(getName());
   }

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return popStyle.isIgnoreLeadingPar();
   }

   @Override
   public int getTeXCategory()
   {
      return TYPE_OBJECT;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%n%n");
   }

   @Override
   public String format()
   {
      return String.format("\\%s ", name);
   }

   @Override
   public String stripToString(TeXParser parser)
     throws IOException
   {
      return toString(parser);
   }

   @Override
   public String toString()
   {
      return String.format("%s%n", getClass().getName());
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return parser.string(String.format("%s%s", 
        new String(Character.toChars(parser.getEscChar())), name));
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list)
      throws IOException
   {
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      return false;
   }

   @Override
   public boolean isPar()
   {
      return true;
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }

   private String name;
}

