/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import com.dickimawbooks.texparserlib.TeXParserListener;
import com.dickimawbooks.texparserlib.ControlSequence;

public enum DatumType
{
   UNKNOWN(-1, "c_datatool_unknown_int"),
   STRING(0, "c_datatool_string_int"),
   INTEGER(1, "c_datatool_integer_int"),
   DECIMAL(2, "c_datatool_decimal_int"),
   CURRENCY(3, "c_datatool_currency_int");

   DatumType(int id, String csname)
   {
      this.id = id;
      this.csname = csname;
   }

   public int getValue()
   {
      return id;
   }

   public String getCsName()
   {
      return csname;
   }

   public ControlSequence getCs(TeXParserListener listener)
   {
      return listener.getControlSequence(csname);
   }

   public boolean isNumeric()
   {
      return id > 0;
   }

   public static DatumType toDatumType(int type)
   {
      switch (type)
      {
         case -1 : return UNKNOWN;
         case 0 : return STRING;
         case 1 : return INTEGER;
         case 2 : return DECIMAL;
         case 3 : return CURRENCY;
      }

      throw new IllegalArgumentException("Invalid DatumType id "+type);
   }

   private final int id;
   private final String csname;
}
