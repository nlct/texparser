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

public class TeXUnit implements TeXObject
{
   public TeXUnit()
   {
      this(UNIT_PT);
   }

   public TeXUnit(int unitId)
   {
      if (unitId < 0 || unitId >= UNIT_NAMES.length)
      {
         throw new IllegalArgumentException("Invalid ID '"
           +unitId+"' in TeXUnit(int)");
      }

      id = unitId;
   }

   public TeXUnit(String unitName)
   {
      id = -1;

      for (int i = 0; i < UNIT_NAMES.length; i++)
      {
         if (UNIT_NAMES[i].equals(unitName))
         {
            id = i;
            break;
         }
      }

      if (id == -1)
      {
         throw new IllegalArgumentException("Invalid unit name '"
           +unitName+"' in TeXUnit(String)");
      }
   }

   public Object clone()
   {
      return new TeXUnit(id);
   }

   public int getId()
   {
      return id;
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

   public String toString()
   {
      return UNIT_NAMES[id];
   }

   public float getSpScaleFactor()
   {
      return id == UNIT_SP ? 1f : PT_FACTORS[id]*PT_FACTORS[UNIT_SP];
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(UNIT_NAMES[id]);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public boolean isPar()
   {
      return false;
   }

   private int id;

   public static final int UNIT_PT=0, UNIT_PC=1, UNIT_IN=2,
     UNIT_BP=3, UNIT_CM=4, UNIT_MM=5, UNIT_DD=6, UNIT_CC=7,
     UNIT_SP=8;

   public static final String[] UNIT_NAMES =
    {
       "pt", "pc", "in", "bp", "cm", "mm", "dd", "cc", "sp"
    };

   public static final float[] PT_FACTORS =
    {
       1.0f, 12.0f, 72.27f, (float)(72.27/72), (float)(72.27/2.54),
       (float)(72.27/25.4), (float)(1238.0/1157.0), 
       (float)(12.0*1238.0/1157.0), 
       (float)(1.0/65536)
    };
}
