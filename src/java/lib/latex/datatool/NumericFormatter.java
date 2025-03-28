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

import java.io.IOException;
import java.util.Vector;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NumericFormatter extends Command
{
   public NumericFormatter(String name, DecimalFormat numFormat)
   {
      this(name, numFormat, numFormat.getDecimalFormatSymbols(),
        numFormat.isParseIntegerOnly());
   }

   public NumericFormatter(String name, NumberFormat numFormat, 
      DecimalFormatSymbols symbols,
     boolean prohibitDecimal)
   {
      this(name, numFormat, 
       prohibitDecimal || symbols == null ? null : ""+symbols.getDecimalSeparator(),
       prohibitDecimal);
   }

   public NumericFormatter(String name, NumberFormat numFormat, String decimalChar,
     boolean prohibitDecimal)
   {
      super(name);
      this.numFormat = numFormat;
      this.decimalChar = decimalChar;
      this.prohibitDecimal = prohibitDecimal;
   }

   @Override
   public Object clone()
   {
      return this;
   }

   public Number parse(String source) throws ParseException
   {
      if (decimalChar != null && prohibitDecimal)
      {
         int idx = source.indexOf(decimalChar);

         if (idx > -1)
         {
            throw new ParseException(
              "Invalid integer '"+source+"' (decimal character '"+decimalChar+"' found)", idx);
         }
      }

      ParsePosition pos = new ParsePosition(0);

      Number num = numFormat.parse(source, pos);

      if (num == null || pos.getIndex() < source.length())
      {
         throw new ParseException("Not a number: "+source, pos.getIndex());
      }

      return num;
   }

   public TeXObject format(Number num, TeXParserListener listener)
   {
      if (prohibitDecimal)
      {
         return listener.createString(numFormat.format(num.intValue()));
      }
      else
      {
         return listener.createString(numFormat.format(num.doubleValue()));
      }
   }

   protected String fmtArg(TeXObject arg, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String fmt;

      if (arg instanceof TeXFloatingPoint)
      {
         fmt = numFormat.format(((TeXFloatingPoint)arg).doubleValue());
      }
      else if (arg instanceof Numerical)
      {
         fmt = numFormat.format(((Numerical)arg).number(parser));
      }
      else
      {
         String str = parser.expandToString(arg, stack);

         try
         {
            fmt = numFormat.format(Integer.parseInt(str));
         }
         catch (NumberFormatException e)
         {
            try
            {
               fmt = numFormat.format(Double.parseDouble(str));
            }
            catch (NumberFormatException e2)
            {
               throw new TeXSyntaxException(e2, parser,
                TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
            }
         }
      }

      return fmt;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = TeXParserUtils.popArg(parser, stack);

      return parser.getListener().createString(fmtArg(arg, parser, stack));
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = TeXParserUtils.popArg(parser, stack);

      parser.getListener().getWriteable().write(fmtArg(arg, parser, stack));
   }

   private NumberFormat numFormat;
   private String decimalChar = null;
   private boolean prohibitDecimal = false;
}
