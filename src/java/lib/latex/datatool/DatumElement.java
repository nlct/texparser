/*
    Copyright (C) 2023-2024 Nicola L.C. Talbot
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

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DatumElement extends AbstractTeXObject 
  implements DataNumericElement,Expandable
{
   public DatumElement()
   {
      this(new TeXObjectList(), null, null, null, null, DatumType.UNKNOWN);
   }

   public DatumElement(TeXObject content)
   {
      this(content, null, null, null, null, DatumType.STRING);
   }

   public DatumElement(TeXObject content, TeXNumber number,
      TeXObject currencySymbol, DatumType datumType)
   {
      this(content, number, number, currencySymbol, null, datumType);
   }

   public DatumElement(TeXObject content, TeXNumber number,
      TeXObject objectValue,
      TeXObject currencySymbol,
      Julian julian, DatumType datumType)
   {
      this.content = content;
      this.number = number;
      this.objectValue = objectValue;
      this.currencySymbol = currencySymbol;
      this.julian = julian;
      this.datumType = datumType;
   }

   public DatumElement(TeXParserListener listener, TeXObject content, Julian julian)
   {
      this.content = content;
      this.currencySymbol = null;

      TeXObjectList list = listener.createStack();
      objectValue = list;

      list.add(new TeXCsRef("DTLtemporalvalue"));
      Group grp = listener.createGroup();
      list.add(grp);

      if (julian.hasDate() && julian.hasTime())
      {
         this.datumType = DatumType.DATETIME;
         number = new TeXFloatingPoint(julian.getJulianDate());
      }
      else if (julian.hasTime())
      {
         this.datumType = DatumType.TIME;
         number = new TeXFloatingPoint(julian.getJulianTime());
      }
      else
      {
         this.datumType = DatumType.DATE;
         number = new UserNumber(julian.getJulianDay());
      }

      grp.add(number);

      list.add(listener.createGroup(julian.getTimeStamp()));
   }

   @Override
   public Object clone()
   {
      DatumElement element = new DatumElement((TeXObject)content.clone());

      if (number != null)
      {
         element.number = (TeXNumber)number.clone();
      }

      if (objectValue != null)
      {
         element.objectValue = (TeXObject)objectValue.clone();
      }

      if (currencySymbol != null)
      {
         element.currencySymbol = (TeXObject)currencySymbol.clone();
      }

      element.julian = julian;

      element.datumType = datumType;

      return element;
   }

   @Override
   public byte getDataType()
   {
      return (byte)datumType.getValue();
   }

   @Override
   public DatumType getDatumType()
   {
      return datumType;
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public double doubleValue()
   {
      return number == null ? 0.0 : number.doubleValue();
   }

   @Override
   public float floatValue()
   {
      return (float)doubleValue();
   }

   @Override
   public int intValue()
   {
      return number == null ? 0 : number.getValue();
   }

   @Override
   public long longValue()
   {
      return number == null ? 0 : number.longValue();
   }

   @Override
   public TeXObject getTeXValue(TeXParser parser)
   {
      return objectValue;
   }

   public Julian getJulian()
   {
      return julian;
   }

   public Number getNumber()
   {
      if (number == null)
      {
         return null;
      }

      if (datumType == DatumType.INTEGER)
      {
         return Integer.valueOf(number.getValue());
      }

      if (datumType == DatumType.DATE)
      {
         return Long.valueOf(number.longValue());
      }

      return Double.valueOf(number.doubleValue());
   }

   @Override
   public TeXObject getCurrencySymbol()
   {
      return currencySymbol;
   }

   public TeXObject getOriginal()
   {
      return content;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return intValue();
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      if (number == null)
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, content.toString(parser));
      }
      else
      {
         number.advance(parser, increment);
         content = number;
      }
   }

   @Override
   public void divide(int divisor)
   {
      if (number != null)
      {
         number.divide(divisor);
         content = number;
      }
   }

   @Override
   public void multiply(int factor)
   {
      if (number != null)
      {
         number.multiply(factor);
         content = number;
      }
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      expanded.add(content, true);

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (content.canExpand())
      {
         return TeXParserUtils.toList(
           TeXParserUtils.expandFully(content, parser, stack), parser);
      }
      else
      {
         return expandonce(parser, stack);
      }
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public ControlSequence createControlSequence(String name)
   {
      return new DatumCommand(name, content, getNumber(),
         currencySymbol, datumType);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      content.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      content.process(parser);
   }

   public void write(TeXParser parser, PrintWriter writer,
      FileFormatType format, String version)
    throws IOException
   {
      if (format == FileFormatType.DBTEX && version.equals("3.0"))
      {
         writer.print("\\dtldbdatumreconstruct{");
         writer.print(content.toString(parser));
         writer.print("}{");

         if (objectValue != null)
         {
            writer.print(objectValue.toString(parser));
         }

         writer.print("}{");

         if (currencySymbol != null)
         {
            writer.print(currencySymbol.toString(parser));
         }

         writer.print("}{");
         writer.print(datumType.getValue());
         writer.print("}");
      }
      else
      {
         writer.print(content.toString(parser));
      }
   }

   @Override
   public TeXObject getContent(TeXParser parser)
   {
      TeXParserListener listener = parser.getListener();
      TeXObjectList stack = listener.createStack();

      stack.add(listener.getControlSequence(DataToolBaseSty.DATUM_NNNN));
      Group grp = listener.createGroup();
      stack.add(grp);
      grp.add(content, true);

      grp = listener.createGroup();
      stack.add(grp);

      if (objectValue != null)
      {
         grp.add((TeXObject)objectValue.clone());
      }

      grp = listener.createGroup();
      stack.add(grp);

      if (currencySymbol != null)
      {
         grp.add(currencySymbol);
      }

      stack.add(new UserNumber(datumType.getValue()));

      return stack;
   }

   @Override
   public String format()
   {
      return content.format();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return content.toString(parser);
   }

   @Override
   public String toString()
   {
      return String.format("%s[content=%s,number=%s,objectValue=%s,symbol=%s,julian=%s,type=%s]",
        getClass().getSimpleName(), content, number, objectValue, currencySymbol,
         julian, datumType);
   }

   @Override
   public TeXObjectList string(TeXParser parser)
   throws IOException
   {
      return content.string(parser);
   }

   private TeXObject content, currencySymbol;
   private TeXNumber number;
   private TeXObject objectValue;
   private DatumType datumType;
   private Julian julian;
}
