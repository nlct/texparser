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

public class DataRealElement extends AbstractTeXObject
  implements DataNumericElement,Expandable
{
   public DataRealElement()
   {
      this(0.0);
   }

   public DataRealElement(Number num)
   {
      this(num.doubleValue());
   }

   public DataRealElement(TeXNumber num)
   {
      this(num.doubleValue());
   }

   public DataRealElement(double value)
   {
      this.value = value;
   }

   @Override
   public Object clone()
   {
      return new DataRealElement(value);
   }

   @Override
   public double doubleValue()
   {
      return value;
   }

   @Override
   public float floatValue()
   {
      return (float)value;
   }

   @Override
   public int intValue()
   {
      return (int)value;
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      value += increment.number(parser);
   }

   @Override
   public void divide(int divisor)
   {
      value /= divisor;
   }

   @Override
   public void multiply(int factor)
   {
      value *= factor;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return intValue();
   }

   @Override
   public byte getDataType()
   {
      return DataToolHeader.TYPE_REAL;
   }

   @Override
   public DatumType getDatumType()
   {
      return DatumType.DECIMAL;
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      expanded.add(new TeXFloatingPoint(doubleValue()));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return parser.getListener().createString(format());
   }

   @Override
   public String format()
   {
      return "" + value;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return format();
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return parser.string(toString(parser));
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      expanded.add(listener.getControlSequence("__texparser_fmt_decimal_value:n"));
      expanded.add(new TeXFloatingPoint(doubleValue()));

      TeXParserUtils.process(expanded, parser, stack);
   }

   public String toString()
   {
      return String.format("%f", value);
   }

   @Override
   public ControlSequence createControlSequence(String name)
   {
      return new FloatingPointContentCommand(name, value);
   }

   private double value;
}
