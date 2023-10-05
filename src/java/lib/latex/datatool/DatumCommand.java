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

public class DatumCommand extends Command
{
   public DatumCommand(String name, TeXObject original, 
     Number numValue, TeXObject currencySym, DatumType type)
   {
      super(name);
      this.original = original;
      this.numValue = numValue;
      this.currencySym = currencySym;
      this.type = type;
   }

   @Override
   public Object clone()
   {
      return new DatumCommand(getName(), (TeXObject)original.clone(),
       numValue, currencySym == null ? null : (TeXObject)currencySym.clone(),
       type);
   }

   public static DatumCommand createString(String name, TeXObject content)
   {
      return new DatumCommand(name, content, null, null, DatumType.STRING);
   }

   public static DatumCommand createInteger(String name, TeXObject content, int value)
   {
      return new DatumCommand(name, content, Integer.valueOf(value), null, 
        DatumType.INTEGER);
   }

   public static DatumCommand createDecimal(String name, TeXObject content, double value)
   {
      return new DatumCommand(name, content, Double.valueOf(value), null, 
        DatumType.DECIMAL);
   }

   public static DatumCommand createCurrency(String name, TeXObject content, double value,
    TeXObject sym)
   {
      return new DatumCommand(name, content, Double.valueOf(value), sym, 
        DatumType.CURRENCY);
   }

   public static DatumCommand create(String name, DataElement element)
   {
      DatumType type = element.getDatumType();

      switch (type)
      {
         case INTEGER:

           return new DatumCommand(name, element, 
             Integer.valueOf(((DataNumericElement)element).intValue()),
             null, type);

         case DECIMAL:

           return new DatumCommand(name, element, 
             Double.valueOf(((DataNumericElement)element).doubleValue()),
             null, type);

         case CURRENCY:

           DataCurrencyElement currElem = (DataCurrencyElement)element;

           return new DatumCommand(name, element, 
             Double.valueOf(currElem.doubleValue()),
             currElem.getSymbol(), type);

         default:
           return new DatumCommand(name, element, null, null, type);
      }
   }

   public static DatumCommand create(DataToolSty sty, String csname, TeXObject contents)
   throws IOException
   {
      if (contents instanceof DataElement)
      {
         return create(csname, (DataElement)contents);
      }

      if (contents.isEmpty())
      {
         return new DatumCommand(csname, contents, null, null, DatumType.UNKNOWN);
      }

      return create(csname, sty.getElement(contents));
   }

   public static DatumCommand create(DataToolBaseSty sty, String csname, TeXObject contents)
   throws IOException
   {
      if (contents instanceof DataElement)
      {
         return create(csname, (DataElement)contents);
      }

      if (contents.isEmpty())
      {
         return new DatumCommand(csname, contents, null, null, DatumType.UNKNOWN);
      }

      return create(csname, sty.getElement(contents));
   }

   public TeXObject getOriginal()
   {
      return original;
   }

   public TeXObject getCurrencySymbol()
   {
      return currencySym;
   }

   public Number getNumericValue()
   {
      return numValue;
   }

   public DatumType getType()
   {
      return type;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();
      expanded.add(listener.getControlSequence("__datatool_datum:nnnn"));

      Group grp = listener.createGroup();
      expanded.add(grp);

      if (parser.isStack(original))
      {
         grp.addAll((TeXObjectList)original.clone());
      }
      else
      {
         grp.add((TeXObject)original.clone());
      }

      grp = listener.createGroup();

      if (numValue != null)
      {
         switch (type)
         {
            case INTEGER:
              grp.add(new UserNumber(numValue.intValue()));
            break;
            default:
              grp.add(new TeXFloatingPoint(numValue.doubleValue()));
         }
      }

      expanded.add(grp);

      grp = listener.createGroup();
      expanded.add(grp);

      if (currencySym != null)
      {
         if (parser.isStack(currencySym))
         {
            grp.addAll((TeXObjectList)currencySym.clone());
         }
         else
         {
            grp.add((TeXObject)currencySym.clone());
         }
      }

      String csName;

      switch (type)
      {
         case STRING:
           csName = "c_datatool_string_int";
         break;
         case INTEGER:
           csName = "c_datatool_integer_int";
         break;
         case DECIMAL:
           csName = "c_datatool_decimal_int";
         break;
         case CURRENCY:
           csName = "c_datatool_currency_int";
         break;
         default:
           csName = "c_datatool_unknown_int";
      }

      expanded.add(listener.getControlSequence(csName));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.toList((TeXObject)original.clone(), parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserUtils.process((TeXObject)original.clone(), parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected TeXObject original, currencySym;
   protected Number numValue;
   protected DatumType type;
}
