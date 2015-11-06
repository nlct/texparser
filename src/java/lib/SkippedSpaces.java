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

import java.util.Vector;
import java.util.Enumeration;

public class SkippedSpaces extends Ignoreable
{
   public SkippedSpaces()
   {
      contents = new Vector<Space>();
   }

   public int size()
   {
      return contents.size();
   }

   public void add(Space eol)
   {
      contents.add(eol);
   }

   public Space get(int idx)
   {
      return contents.get(idx);
   }

   public Enumeration<Space> elements()
   {
      return contents.elements();
   }

   public Vector<Space> getContents()
   {
      return contents;
   }

   public Object clone()
   {
      SkippedSpaces obj = new SkippedSpaces();

      obj.contents.addAll(contents);

      return obj;
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      for (Space sp : contents)
      {
         builder.appendCodePoint(sp.getSpace());
      }

      return builder.toString();
   }

   public String format()
   {
      StringBuilder builder = new StringBuilder();

      for (Space sp : contents)
      {
         builder.appendCodePoint(sp.getSpace());
      }

      return builder.toString();
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(String.format("%s[", getClass().getSimpleName()));

      for (Space sp : contents)
      {
         builder.appendCodePoint(sp.getSpace());
      }

      builder.append("]");

      return builder.toString();
   }

   public boolean containsEol()
   {
      for (Space sp : contents)
      {
         if (sp.isEol())
         {
            return true;
         }
      }

      return false;
   }

   private Vector<Space> contents;
}
