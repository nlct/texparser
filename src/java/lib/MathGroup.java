/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

   @Override
   public boolean isMathGroup()
   {
      return true; 
   }

   public boolean isInLine()
   {
      return isinline;
   }

   public void setInLine(boolean isInLine)
   {
      isinline = isInLine;
   }

   @Override
   public TeXObjectList createList()
   {
      MathGroup math = new MathGroup(capacity());
      math.setInLine(isinline);
      return math;
   }

   @Override
   public String format()
   {
      StringBuilder builder = new StringBuilder();

      String delim = (isInLine() ? "$" : "$$");

      builder.append(delim);

      for (TeXObject object : this)
      {
         builder.append(object.format());
      }

      builder.append(delim);

      return builder.toString();
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(getClass().getSimpleName());

      String delim = (isInLine() ? "$" : "$$");

      builder.append(delim);

      for (TeXObject object : this)
      {
         builder.append(object.toString());
      }

      builder.append(delim);

      return builder.toString();
   }

   @Override
   public void startGroup(TeXParser parser)
    throws IOException
   {
      super.startGroup(parser);

      TeXSettings settings = parser.getSettings();

      settings.setMode(isinline ? TeXMode.INLINE_MATH :
         TeXMode.DISPLAY_MATH);
   }

   @Override
   public TeXObject getBegin(TeXParser parser)
   {
      return new MathBg(parser.getMathChar(), isInLine());
   }

   @Override
   public TeXObject getEnd(TeXParser parser)
   {
      return new MathEg(parser.getMathChar(), isInLine());
   }

   private boolean isinline;
}

