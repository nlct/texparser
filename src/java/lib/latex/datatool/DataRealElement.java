/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public class DataRealElement implements DataNumericElement,Expandable
{
   public DataRealElement(DataToolSty sty)
   {
      this(sty, 0.0);
   }

   public DataRealElement(DataToolSty sty, TeXNumber num)
   {
      this(sty, num.getValue());
   }

   public DataRealElement(DataToolSty sty, double value)
   {
      this.sty = sty;
      this.value = value;
   }

   @Override
   public Object clone()
   {
      return new DataRealElement(sty, value);
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

   public byte getDataType()
   {
      return DataToolHeader.TYPE_REAL;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return string(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      TeXObjectList list = expandonce(parser);

      if (list == null) return null;

      return list.expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList list = expandonce(parser, stack);

      if (list == null) return null;

      return list.expandfully(parser, stack);
   }

   @Override
   public String format()
   {
      return String.format("%f", value);
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
      parser.getListener().getWriteable().write(toString(parser));
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      process(parser);
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   private double value;
   protected DataToolSty sty;
}
