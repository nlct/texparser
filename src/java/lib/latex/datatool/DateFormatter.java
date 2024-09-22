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
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DateFormatter extends Command
{
   public DateFormatter(String name, SimpleDateFormat dateFormat)
   {
      super(name);
      this.dateFormat = dateFormat;
   }

   @Override
   public Object clone()
   {
      return this;
   }

   public Date parse(String source) throws ParseException
   {
      return dateFormat.parse(source);
   }

   protected String fmtArg(TeXObject arg, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String fmt;
      double jdn;

      if (arg instanceof TeXNumber)
      {
         jdn = ((TeXFloatingPoint)arg).doubleValue();
      }
      else
      {
         String str = parser.expandToString(arg, stack);

         try
         {
            jdn = Double.parseDouble(str);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser,
             TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
         }
      }

      fmt = dateFormat.format(new Date(DataToolBaseSty.unixEpochFromJulianDate(jdn)));

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

   private SimpleDateFormat dateFormat;
}
