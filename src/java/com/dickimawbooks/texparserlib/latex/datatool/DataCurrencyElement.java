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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataCurrencyElement extends DataRealElement
{
   public DataCurrencyElement(DataToolSty sty, TeXObject symbol)
   {
      this(sty, symbol, 0.0);
   }

   public DataCurrencyElement(DataToolSty sty, TeXObject symbol, TeXNumber num)
   {
      this(sty, symbol, num.getValue());
   }

   public DataCurrencyElement(DataToolSty sty, TeXObject symbol, double value)
   {
      super(sty, value);
      this.symbol = symbol;
   }

   public Object clone()
   {
      return new DataCurrencyElement(sty, (TeXObject)symbol.clone(), 
         doubleValue());
   }

   public byte getDataType()
   {
      return DataToolHeader.TYPE_CURRENCY;
   }

   public TeXObject getSymbol()
   {
      return symbol;
   }

   public String toString(TeXParser parser)
   {
      return String.format("%s%s", symbol.toString(parser), format());
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      TeXObjectList list = super.expandonce(parser);

      if (symbol instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)symbol).expandonce(parser);

         if (expanded == null)
         {
            list.add(0, symbol);
         }
         else
         {
            list.addAll(0, expanded);
         }
      }
      else
      {
         list.add(0, symbol);
      }

      return list;
   }

   public void process(TeXParser parser) throws IOException
   {
      symbol.process(parser);
      parser.getListener().getWriteable().write(super.toString(parser));
   }

   private TeXObject symbol;
}
