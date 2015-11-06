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

public class ExUnit extends TeXUnit
{
   public ExUnit()
   {
   }

   public Object clone()
   {
      return new ExUnit();
   }

   public boolean equals(Object object)
   {
      if (this == object) return true;

      if (object == null) return false;

      return object instanceof ExUnit;
   }

   // convert value in other unit to this unit
   public float toUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      if (equals(otherUnit))
      {
         return value;
      }

      if (otherUnit.equals(FixedUnit.PT))
      {
         return value/parser.getListener().exToPt(1f);
      }

      return otherUnit.toPt(parser, value)/parser.getListener().exToPt(1f);
   }

   // convert value in this unit to other unit
   public float fromUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      if (equals(otherUnit))
      {
         return value;
      }

      if (otherUnit.equals(FixedUnit.PT))
      {
         return parser.getListener().exToPt(value);
      }

      return otherUnit.fromPt(parser, parser.getListener().exToPt(value));
   }

   public float toPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return parser.getListener().exToPt(value);
   }

   public float fromPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return value/parser.getListener().exToPt(1f);
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return parser.string(toString());
   }

   public String toString(TeXParser parser)
   {
      return toString();
   }

   public String format()
   {
      return "ex";
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write("ex");
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

}
