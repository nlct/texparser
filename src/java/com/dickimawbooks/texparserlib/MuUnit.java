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

public class MuUnit extends TeXUnit
{
   public MuUnit()
   {
   }

   public Object clone()
   {
      return new MuUnit();
   }

   public boolean equals(Object object)
   {
      if (this == object) return true;

      if (object == null) return false;

      return object instanceof MuUnit;
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
         return emToMu(value/parser.getListener().emToPt(1f));
      }

      return emToMu(otherUnit.toPt(parser, value)
        / parser.getListener().emToPt(1f));
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
         return parser.getListener().emToPt(muToEm(value));
      }

      return otherUnit.fromPt(parser, 
        parser.getListener().emToPt(muToEm(value)));
   }

   public float toPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return parser.getListener().emToPt(muToEm(value));
   }

   public float fromPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return emToMu(value/parser.getListener().emToPt(1f));
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
      return "mu";
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write("mu");
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }


}
