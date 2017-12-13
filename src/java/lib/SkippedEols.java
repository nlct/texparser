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

public class SkippedEols extends Ignoreable
{
   public SkippedEols()
   {
      contents = new Vector<Eol>();
   }

   public SkippedEols(Eol eol)
   {
      this();
      add(eol);
   }

   public int size()
   {
      return contents.size();
   }

   public void add(Eol eol)
   {
      contents.add(eol);
   }

   public Eol get(int idx)
   {
      return contents.get(idx);
   }

   public Enumeration<Eol> elements()
   {
      return contents.elements();
   }

   public Vector<Eol> getContents()
   {
      return contents;
   }

   public Object clone()
   {
      SkippedEols obj = new SkippedEols();

      obj.contents.addAll(contents);

      return obj;
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      for (Eol eol : contents)
      {
         builder.append(eol.toString(parser));
      }

      return builder.toString();
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("%s[", getClass().getName()));

      for (Eol eol : contents)
      {
         builder.append(eol.toString());
      }

      builder.append("]");

      return builder.toString();
   }

   public String format()
   {
      StringBuilder builder = new StringBuilder();

      for (Eol eol : contents)
      {
         builder.append(eol.format());
      }

      return builder.toString();
   }

   private Vector<Eol> contents;
}
