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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXObject;
import com.dickimawbooks.texparserlib.ControlSequence;

public interface DataElement extends TeXObject
{
   /**
    * Gets the data type as a byte. The return value should be one
    * of: DataToolHeader.TYPE_UNDEF, DataToolHeader.TYPE_STRING,
    * DataToolHeader.TYPE_INT, DataToolHeader.TYPE_REAL,
    * DataToolHeader.TYPE_CURRENCY.
    */ 
   public byte getDataType();

   /**
    * Gets the data type.
    */ 
   public DatumType getDatumType();

   /**
    * Creates a control sequence that is defined to this value.
    */ 
   public ControlSequence createControlSequence(String name);

   /**
    * Gets the currency symbol or null if not currency.
    */
   public TeXObject getCurrencySymbol();

   /**
    * Gets this data as an ordinary TeXObject.
    */
   public TeXObject getContent(TeXParser parser);
}
