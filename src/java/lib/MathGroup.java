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

public class MathGroup extends AbstractGroup
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

   @Override
   public StackMarker createStackMarker()
   {
      return new MathGroupMarker(isInLine());
   }

   @Override
   public AbstractTeXObjectList createList()
   {
      MathGroup math = new MathGroup();
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

      settings.setMode(isinline ? TeXSettings.MODE_INLINE_MATH :
         TeXSettings.MODE_DISPLAY_MATH);
   }

   @Override
   public BeginGroupObject getBegin(TeXParser parser)
   {
      return new MathBg(parser.getMathChar(), isInLine());
   }

   @Override
   public EndGroupObject getEnd(TeXParser parser)
   {
      return new MathEg(parser.getMathChar(), isInLine());
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }


   @Override
   public boolean equals(Object o)
   {
      if (!((o instanceof MathGroup) && super.equals(o)))
      {
         return false;
      }

      return isinline == ((MathGroup)o).isinline;
   }

   private boolean isinline;
}

