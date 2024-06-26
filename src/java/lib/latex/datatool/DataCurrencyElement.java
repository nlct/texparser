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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataCurrencyElement extends DataRealElement
{
   public DataCurrencyElement(TeXObject symbol)
   {
      this(symbol, 0.0);
   }

   public DataCurrencyElement(TeXObject symbol, TeXNumber num)
   {
      this(symbol, num.doubleValue());
   }

   public DataCurrencyElement(TeXObject symbol, Number num)
   {
      this(symbol, num.doubleValue());
   }

   public DataCurrencyElement(TeXObject symbol, double value)
   {
      super(value);
      this.symbol = symbol;
   }

   public DataCurrencyElement(TeXObject symbol, double value, TeXObject original)
   {
      super(value, original);
      this.symbol = symbol;
   }

   @Override
   public Object clone()
   {
      return new DataCurrencyElement((TeXObject)symbol.clone(), 
         doubleValue(), original == null ? null : (TeXObject)original.clone());
   }

   @Override
   public byte getDataType()
   {
      return DataToolHeader.TYPE_CURRENCY;
   }

   @Override
   public DatumType getDatumType()
   {
      return DatumType.CURRENCY;
   }

   @Override
   public ControlSequence createControlSequence(String name)
   {
      return new CurrencyContentCommand(name, (TeXObject)symbol.clone(), doubleValue());
   }

   @Override
   public TeXObject getContent(TeXParser parser)
   {
      if (original == null)
      {
         TeXObjectList list = parser.getListener().createString(
           String.format("%.2f", doubleValue()));
         list.push((TeXObject)symbol.clone());

         return list;
      }
      else
      {
         return original;
      }
   }

   @Override
   public TeXObject getCurrencySymbol()
   {
      return getSymbol();
   }

   public TeXObject getSymbol()
   {
      return symbol;
   }

   @Override
   public String toString(TeXParser parser)
   {
      if (original == null)
      {
         return String.format("%s%.2f", symbol.toString(parser), doubleValue());
      }
      else
      {
         return original.toString(parser);
      }
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (original != null)
      {
         return super.expandonce(parser, stack);
      }

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      expanded.add(listener.getControlSequence("DTLfmtcurrency"));

      Group grp = listener.createGroup();
      grp.add(symbol, true);
      expanded.add(grp);

      grp = listener.createGroup();
      expanded.add(grp);

      grp.add(listener.getControlSequence(
        DataToolBaseSty.FMT_CURRENCY_VALUE));
      grp.add(new TeXFloatingPoint(doubleValue()));

      return expanded;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      expandonce(parser).process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      expandonce(parser, stack).process(parser, stack);
   }

   @Override
   public String toString()
   {
      return String.format("%s[symbol=%s,value=%f,original=%s]",
        getClass().getSimpleName(), symbol, value, original);
   }

   private TeXObject symbol;
}
