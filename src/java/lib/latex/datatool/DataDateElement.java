/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

import java.util.Date;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataDateElement extends AbstractTeXObject
  implements DataTemporalElement,Expandable
{
   public DataDateElement()
   {
      this(0);
   }

   public DataDateElement(Number num)
   {
      this(num.intValue(), null);
   }

   public DataDateElement(TeXNumber num)
   {
      this(num.getValue(), null);
   }

   public DataDateElement(int value)
   {
      this(value, null);
   }

   public DataDateElement(int value, TeXObject original)
   {
      this(Julian.createDay(value), original);
   }

   public DataDateElement(Julian julian)
   {
      this(julian, null);
   }

   public DataDateElement(Julian julian, TeXObject original)
   {
      this.julian = julian;
      this.original = original;
   }

   @Override
   public Object clone()
   {
      return new DataDateElement(julian,
        original == null ? null : (TeXObject)original.clone());
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
      return julian.getJulianDay();
   }

   @Override
   public long longValue()
   {
      return (long)intValue();
   }

   @Override
   public Date getDate()
   {
      return new Date(julian.toUnixEpochMillis());
   }

   @Override
   public Julian getJulian()
   {
      return julian;
   }

   @Override
   public TeXObject getTeXValue(TeXParser parser)
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(new TeXCsRef("DTLtemporalvalue"));

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(new UserNumber(intValue()));

      if (original == null)
      {
         list.add(listener.createGroup(julian.getTimeStamp()));
      }
      else
      {
         grp = listener.createGroup();
         grp.add((TeXObject)original.clone(), true);
         list.add(grp);
      }

      return list;
   }

   @Override
   public TeXObject getContent(TeXParser parser)
   {
      if (original == null)
      {
         return parser.getListener().createString(
          DataToolBaseSty.DATE_FORMAT.format(getDate()));
      }
      else
      {
         return original;
      }
   }

   @Override
   public TeXObject getCurrencySymbol()
   {
      return null;
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      int value = intValue();
      value += increment.number(parser);

      julian = Julian.createDay(value);
      original = null;
   }

   @Override
   public void divide(int divisor)
   {
      int value = intValue();
      value /= divisor;

      julian = Julian.createDay(value);
      original = null;
   }

   @Override
   public void multiply(int factor)
   {
      int value = intValue();
      value *= factor;

      julian = Julian.createDay(value);
      original = null;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return intValue();
   }

   @Override
   public byte getDataType()
   {
      return DataToolHeader.TYPE_DATE;
   }

   @Override
   public DatumType getDatumType()
   {
      return DatumType.DATE;
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
      if (parser.isStack(original))
      {
         return (TeXObjectList)original.clone();
      }

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      if (original == null)
      {
         expanded.add(listener.getControlSequence(
           DataToolBaseSty.FMT_DATE_VALUE));
         expanded.add(new UserNumber(intValue()));
      }
      else
      {
         expanded.add((TeXObject)original.clone());
      }

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
      if (original == null)
      {
         return DataToolBaseSty.DATE_FORMAT.format(getDate());
      }
      else
      {
         return original.format();
      }
   }

   @Override
   public String toString(TeXParser parser)
   {
      if (original == null)
      {
         return DataToolBaseSty.DATE_FORMAT.format(getDate());
      }
      else
      {
         return original.toString(parser);
      }
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

      if (original == null)
      {
         TeXObjectList expanded = listener.createStack();

         expanded.add(listener.getControlSequence(
           DataToolBaseSty.FMT_DATE_VALUE));
         expanded.add(new UserNumber(intValue()));

         TeXParserUtils.process(expanded, parser, stack);
      }
      else
      {
         TeXParserUtils.process(original, parser, stack);
      }
   }

   public String toString()
   {
      return String.format("%s[julian=%s,original=%s]",
        getClass().getSimpleName(), julian, original);
   }

   @Override
   public ControlSequence createControlSequence(String name)
   {
      return new IntegerContentCommand(name, intValue());
   }

   protected Julian julian;
   protected TeXObject original;
}
