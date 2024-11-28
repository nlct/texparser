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

/**
 * The principle data types are: string, integer, decimal,
 * currency, datetime, date, and time.
 * The unknown type is typically used as an initial value
 * or for empty strings. The temporal types are new to
 * datatool v3.0 and are considered numeric types.
 */
public enum DatumType
{
   UNKNOWN(-1, "c_datatool_unknown_int"),
   STRING(0, "c_datatool_string_int"),
   INTEGER(1, "c_datatool_integer_int"),
   DECIMAL(2, "c_datatool_decimal_int"),
   CURRENCY(3, "c_datatool_currency_int"),
   DATETIME(4, "c_datatool_datetime_int"),
   DATE(5, "c_datatool_date_int"),
   TIME(6, "c_datatool_time_int");

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

   public static DatumType toDatumType(int typeId)
   {
      for (DatumType type : values())
      {
         if (type.id == typeId)
         {
            return type;
         }
      }

      throw new IllegalArgumentException("Invalid DatumType id "+typeId);
   }

   /**
    * Returns true if this type overrides the other type.
    */
   @Deprecated
   public boolean overrides(DatumType other)
   {
      if (this == other) return false;

      // Everything takes precedence over unknown

      if (this == UNKNOWN) return false;
      if (other == UNKNOWN) return true;

      // Otherwise string takes precedence over numeric 
      if (this == STRING) return true;
      if (other == STRING) return false;

      // Otherwise currency takes precedence over non-currency numeric 
      if (this == CURRENCY) return true;
      if (other == CURRENCY) return false;

      // Otherwise date/time takes precedence over decimal and
      // integer, date and time 
      if (this == DATETIME) return true;
      if (other == DATETIME) return false;

      // Otherwise decimal takes precedence over integer 
      if (this == DECIMAL) return true;
      return false;
   }

   /**
    * Gets the dominant data type. This should be preferred instead
    * of overrides(DatumType) to deal with date + time = datetime.
    */
   public static DatumType getDominant(DatumType type1, DatumType type2)
   {
      if (type1 == type2) return type1;

      if (type1 == UNKNOWN) return type2;
      if (type2 == UNKNOWN) return type1;

      if (type1 == STRING || type2 == STRING) return STRING;

      if (type1 == CURRENCY || type2 == CURRENCY) return CURRENCY;

      if (type1 == DATETIME || type2 == DATETIME) return DATETIME;

      if (
            (type1 == DATE && type2 == INTEGER)
         || (type2 == DATE && type1 == INTEGER)
         )
      {
         return DATE;
      }

      if ( type1 == DATE || type1 == TIME
        || type2 == DATE || type2 == TIME )
      {
         return DATETIME;
      }

      return type1.id > type2.id ? type1 : type2;
   }

   private final int id;
   private final String csname;
}
