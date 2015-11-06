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

public class FillUnit extends TeXUnit
{
   public FillUnit(int strength)
   {
      setStrength(strength);
   }

   public FillUnit(String string)
   {
      if (string.equals("fil"))
      {
         strength = 1;
      }
      else if (string.equals("fill"))
      {
         strength = 2;
      }
      else if (string.equals("filll"))
      {
         strength = 3;
      }
      else
      {
         throw new IllegalArgumentException("Illegal fill unit "+string);
      }
   }

   private void setStrength(int strength)
   {
      if (strength < 1 || strength > 3)
      {
         throw new IllegalArgumentException("Illegal fill strength "+strength);
      }

      this.strength = strength;
   }

   public int getStrength()
   {
      return strength;
   }

   public Object clone()
   {
      return new FillUnit(strength);
   }

   public boolean equals(Object object)
   {
      if (this == object) return true;

      if (object == null) return false;

      if (!(object instanceof FillUnit)) return false;

      return strength == ((FillUnit)object).getStrength();
   }

   // convert value in other unit to this unit
   public float toUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      return value;
   }

   // convert value in this unit to other unit
   public float fromUnit(TeXParser parser, float value, 
     TeXUnit otherUnit)
   throws TeXSyntaxException
   {
      return value;
   }

   public float toPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return value;
   }

   public float fromPt(TeXParser parser, float value)
      throws TeXSyntaxException
   {
      return value;
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
      String str = "fi";

      for (int i = 1; i <= strength; i++)
      {
         str += "l";
      }

      return str;
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(toString());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   private int strength=1;
}
