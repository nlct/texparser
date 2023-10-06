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

public class DataIntElement extends UserNumber implements DataNumericElement
{
   public DataIntElement()
   {
      this(0);
   }

   public DataIntElement(Number value)
   {
      super(value.intValue());
   }

   public DataIntElement(int value)
   {
      super(value);
   }

   public DataIntElement(TeXNumber num)
   {
      super(num.getValue());
   }

   @Override
   public Object clone()
   {
      return new DataIntElement(getValue());
   }

   @Override
   public byte getDataType()
   {
      return DataToolHeader.TYPE_INT;
   }

   @Override
   public DatumType getDatumType()
   {
      return DatumType.INTEGER;
   }

   @Override
   public double doubleValue()
   {
      return (double)intValue();
   }

   @Override
   public float floatValue()
   {
      return (float)intValue();
   }

   @Override
   public int intValue()
   {
      return getValue();
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
   
      expanded.add(listener.getControlSequence("__texparser_fmt_integer_value:n")); 
      expanded.add(new UserNumber(getValue()));
      
      TeXParserUtils.process(expanded, parser, stack);
   }

   @Override
   public String format()
   {
      return "" + getValue();
   }

   @Override
   public ControlSequence createControlSequence(String name)
   {
      return new IntegerContentCommand(name, intValue());
   }
}
