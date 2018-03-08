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

public class PercentUnit extends TeXUnit
{
   public PercentUnit()
   {
      this(LINE_WIDTH);
   }

   public PercentUnit(int type)
   {
      setType(type);
   }

   public Object clone()
   {
      return new PercentUnit(type);
   }

   public boolean equals(Object object)
   {
      if (this == object) return true;

      if (object == null) return false;

      return object instanceof PercentUnit;
   }


   public float toUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      float bpValue = parser.getListener().getPageDimension(type)*value*0.01f;

      return TeXUnit.BP.toUnit(parser, bpValue, otherUnit);
   }

   public float fromUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      float bpValue = TeXUnit.BP.fromUnit(parser, value, otherUnit);

      return 100*bpValue/parser.getListener().getPageDimension(type)*value;
   }

   public int getType()
   {
      return type;
   }

   public void setType(int newType)
   {
      switch (newType)
      {
         case LINE_WIDTH:
         case COLUMN_WIDTH:
         case TEXT_WIDTH:
         case COLUMN_HEIGHT:
         case TEXT_HEIGHT:
         case HSIZE:
         case VSIZE:
         case PAPER_WIDTH:
         case PAPER_HEIGHT:
         case MARGIN_WIDTH:
           type = newType;
         break;
         default:
           throw new IllegalArgumentException(
             "Invalid percent unit type "+newType);
      }
   }

   public String format()
   {
      switch (type)
      {
         case LINE_WIDTH: return "\\linewidth ";
         case COLUMN_WIDTH: return "\\columnwidth ";
         case TEXT_WIDTH: return "\\textwidth ";
         case COLUMN_HEIGHT: return "\\columnheight ";
         case TEXT_HEIGHT: return "\\textheight ";
         case HSIZE: return "\\hsize ";
         case VSIZE: return "\\vsize ";
         case PAPER_WIDTH: return "\\paperwidth ";
         case PAPER_HEIGHT: return "\\paperwidth ";
         case MARGIN_WIDTH: return "\\marginparwidth ";
      }

      return "";
   }

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return parser.getListener().createString(format());
   }

   public String toString(TeXParser parser)
   {
      return format();
   }

   public void process(TeXParser parser) throws IOException
   {
      float bpValue = parser.getListener().getPageDimension(type);

      parser.getListener().getWriteable().write(
        String.format("%fbp", bpValue));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   private int type=LINE_WIDTH;
   public static final int LINE_WIDTH=0;
   public static final int COLUMN_WIDTH=1;
   public static final int TEXT_WIDTH=2;
   public static final int COLUMN_HEIGHT=3;
   public static final int TEXT_HEIGHT=4;
   public static final int HSIZE=5;
   public static final int VSIZE=6;
   public static final int PAPER_WIDTH=7;
   public static final int PAPER_HEIGHT=8;
   public static final int MARGIN_WIDTH=9;
}
