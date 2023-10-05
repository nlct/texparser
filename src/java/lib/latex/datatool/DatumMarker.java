/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

public class DatumMarker extends Command
{
   public DatumMarker()
   {
      this("__datatool_datum:nnnn");
   }

   public DatumMarker(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DatumMarker(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);

      return TeXParserUtils.toList(arg, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);

      TeXParserUtils.process(arg, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public static boolean isDatumMarker(TeXObject object)
   {
      if (object instanceof DatumMarker
         || (object instanceof ControlSequence 
              && ((ControlSequence)object).getName().equals("__datatool_datum:nnnn")))
      {
         return true;
      }

      return false;
   }

   public static DataElement popDataElement(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject arg = TeXParserUtils.popArg(parser, stack);
      TeXObject content;

      if (isDatumMarker(arg))
      {
         content = TeXParserUtils.popArg(parser, stack);
      }
      else
      {// marker already popped
         content = arg;
      }

      TeXObject numArg = TeXParserUtils.popArg(parser, stack);
      TeXObject symArg = TeXParserUtils.popArg(parser, stack);
      int type = TeXParserUtils.popInt(parser, stack);

      switch (type)
      {
         case DataToolHeader.TYPE_INT:

           return new DataIntElement(TeXParserUtils.toInt(numArg, parser, stack));

         case DataToolHeader.TYPE_REAL:

           return new DataRealElement(TeXParserUtils.toDouble(numArg, parser, stack));

         case DataToolHeader.TYPE_CURRENCY:

           return new DataCurrencyElement(
              symArg, TeXParserUtils.toDouble(numArg, parser, stack));

         default:

            return new DataStringElement(TeXParserUtils.toList(content, parser));
      }
   }
}
