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

public class MathGroup extends Group
{
   public MathGroup()
   {
      super();
   }

   public MathGroup(int capacity)
   {
      super(capacity);
   }

   public boolean isInLine()
   {
      return isinline;
   }

   public void setInLine(boolean isInLine)
   {
      isinline = isInLine;
   }

   public TeXObjectList createList()
   {
      MathGroup math = new MathGroup(capacity());
      math.setInLine(isinline);
      return math;
   }

   public String toString(TeXParser parser)
   {
      String delim = parser.getMathDelim(isinline);

      StringBuilder builder = new StringBuilder();

      builder.append(delim);

      for (TeXObject object : this)
      {
         builder.append(object.toString(parser));
      }

      builder.append(delim);

      return builder.toString();
   }

   public String toString()
   {
      String delim = (isinline ? "$" : "$$");

      StringBuilder builder = new StringBuilder();

      builder.append(delim);

      for (TeXObject object : this)
      {
         builder.append(object.toString());
      }

      builder.append(delim);

      return builder.toString();
   }

   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      parser.startGroup();
      TeXSettings settings = parser.getSettings();

      settings.setMode(isinline ? TeXSettings.MODE_INLINE_MATH :
         TeXSettings.MODE_DISPLAY_MATH);

      processList(parser, stack);

      parser.endGroup();
   }

   private boolean isinline;
}

