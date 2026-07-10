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

import java.util.Vector;

/**
 * A paragraph break identified by a blank line.
 * (For <code>\par</code> see
 * {@link com.dickimawbooks.texparserlib.generic.ParCs})
 */
public class Par extends AbstractTeXObject
{
   public Par()
   {
      spaces = new Vector<WhiteSpace>();
   }

   public Object clone()
   {
      Par par = new Par();

      par.spaces.addAll(spaces);

      return par;
   }

   public String toString(TeXParser parser)
   {
      if (spaces.isEmpty())
      {
         return String.format("%n%n");
      }
      
      StringBuilder builder = new StringBuilder();

      for (WhiteSpace sp : spaces)
      {
         builder.append(sp.toString(parser));
      }

      return builder.toString();
   }

   @Override
   public String format()
   {
      if (spaces.isEmpty())
      {
         return String.format("%n%n");
      }
      
      StringBuilder builder = new StringBuilder();

      for (WhiteSpace sp : spaces)
      {
         builder.append(sp.format());
      }

      return builder.toString();
   }

   public String toString()
   {
      return String.format("%s[spaces=%s]",
         getClass().getSimpleName(), spaces);
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return parser.string(toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList list)
      throws IOException
   {
   }

   public void process(TeXParser parser)
      throws IOException
   {
   }

   @Override
   public boolean isPar()
   {
      return true;
   }

   public void add(WhiteSpace space)
   {
      spaces.add(space);
   }

   public void add(SkippedSpaces skipped)
   {
      spaces.addAll(skipped.getContents());
   }

   public Vector<WhiteSpace> getContents()
   {
      return spaces;
   }

   Vector<WhiteSpace> spaces;
}

