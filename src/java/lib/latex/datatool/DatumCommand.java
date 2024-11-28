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

import com.dickimawbooks.texparserlib.*;

public class DatumCommand extends Command
{
   public DatumCommand(String name, TeXObject original, 
     Number numValue, TeXObject currencySym, DatumType type)
   {
      this(name, original, numValue, null, currencySym, type);
   }

   public DatumCommand(String name, TeXObject original, 
     Number numValue, TeXObject objValue, TeXObject currencySym, DatumType type)
   {
      super(name);
      this.original = original;
      this.numValue = numValue;

      if (objValue == null && numValue != null)
      {
         switch (type)
         {
            case INTEGER:
               objValue = new UserNumber(numValue.intValue());
            break;
            case DATE:
               // date no time (integer Julian Day)
               objValue = new TeXLongNumber(numValue.longValue());
            break;
            default:
               objValue = new TeXFloatingPoint(numValue.doubleValue());
            break;
         }
      }
      else
      {
         this.objValue = objValue;
      }

      this.currencySym = currencySym;
      this.type = type;
   }

   @Override
   public Object clone()
   {
      return new DatumCommand(getName(), (TeXObject)original.clone(),
       numValue, objValue == null ? null : (TeXObject)objValue.clone(),
       currencySym == null ? null : (TeXObject)currencySym.clone(),
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

   public static DatumCommand createDateTime(String name, TeXObject content,
    double value, TeXObject objValue)
   {
      return new DatumCommand(name, content, Double.valueOf(value), objValue, 
        DatumType.DATETIME);
   }

   public static DatumCommand createDate(String name, TeXObject content,
    long value, TeXObject objValue)
   {
      return new DatumCommand(name, content, Long.valueOf(value), objValue, 
        DatumType.DATE);
   }

   public static DatumCommand createTime(String name, TeXObject content,
    double value, TeXObject objValue)
   {
      return new DatumCommand(name, content, Double.valueOf(value), objValue, 
        DatumType.TIME);
   }

   public static DatumCommand create(TeXParser parser, String name, DataElement element)
   {
      DatumType type = element.getDatumType();

      if (element instanceof DatumElement)
      {
         DatumElement datum = (DatumElement)element;

         return new DatumCommand(name, element, 
           datum.getNumber(), datum.getTeXValue(parser),
           datum.getCurrencySymbol(), type);
      }

      switch (type)
      {
         case INTEGER:

           return new DatumCommand(name, element, 
             Integer.valueOf(((DataNumericElement)element).intValue()),
             element.getTeXValue(parser),
             null, type);

         case DECIMAL:

           return new DatumCommand(name, element, 
             Double.valueOf(((DataNumericElement)element).doubleValue()),
             element.getTeXValue(parser),
             null, type);

         case CURRENCY:

           DataCurrencyElement currElem = (DataCurrencyElement)element;

           return new DatumCommand(name, element, 
             Double.valueOf(currElem.doubleValue()),
             element.getTeXValue(parser),
             currElem.getSymbol(), type);

         case DATETIME:

           return new DatumCommand(name, element, 
             Double.valueOf(((DataNumericElement)element).doubleValue()),
             element.getTeXValue(parser), type);

         case DATE:

           return new DatumCommand(name, element, 
             Long.valueOf(((DataNumericElement)element).longValue()),
             element.getTeXValue(parser), type);

         case TIME:

           return new DatumCommand(name, element, 
             Double.valueOf(((DataNumericElement)element).doubleValue()),
             element.getTeXValue(parser), type);

         default:
           return new DatumCommand(name, element, null, null, type);
      }
   }

   public static DatumCommand create(DataToolSty sty, String csname, TeXObject contents)
   throws IOException
   {
      if (contents instanceof DataElement)
      {
         return create(sty.getParser(), csname, (DataElement)contents);
      }

      if (contents.isEmpty())
      {
         return new DatumCommand(csname, contents, null, null, DatumType.UNKNOWN);
      }

      return create(sty.getParser(), csname, sty.getElement(contents));
   }

   public static DatumCommand create(DataToolBaseSty sty, String csname, TeXObject contents)
   throws IOException
   {
      if (contents instanceof DataElement)
      {
         return create(sty.getParser(), csname, (DataElement)contents);
      }

      if (contents.isEmpty())
      {
         return new DatumCommand(csname, contents, null, null, DatumType.UNKNOWN);
      }

      return create(sty.getParser(), csname, sty.getElement(contents));
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

   public TeXObject getTeXValue()
   {
      return objValue;
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

      expanded.add(listener.getControlSequence(DataToolBaseSty.DATUM_NNNN));

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

      if (objValue != null)
      {
         grp.add((TeXObject)objValue.clone());
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

      expanded.add(type.getCs(listener));

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

   public String toString()
   {
      return String.format("%s[name=%s,original=%s,type=%s,currencySym=%s,number=%s,value=%s]",
       getClass().getSimpleName(), getName(), original, type,
        currencySym, numValue, objValue);
   }

   protected TeXObject original, currencySym, objValue;
   protected Number numValue;
   protected DatumType type;
}
