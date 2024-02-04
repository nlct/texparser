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

import java.util.Vector;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.ifthen.IfThenSty;
import com.dickimawbooks.texparserlib.primitives.EndGraf;
import com.dickimawbooks.texparserlib.primitives.NewIf;

public class DataToolBaseSty extends LaTeXSty
{
   public DataToolBaseSty(KeyValList options, 
     LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, "datatool-base", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      TeXParser parser = getListener().getParser();

      sortCountReg = parser.getSettings().newcount(false, "dtl@sortresult");
      NewIf.createConditional(true, parser, "ifDTLlistskipempty", true);

      CountRegister reg;

      registerControlSequence(new DTLnewcurrencysymbol(this));
      registerControlSequence(new DTLsetdefaultcurrency(this));
      registerControlSequence(new DTLifintopenbetween());
      registerControlSequence(new DTLifintclosedbetween());

      registerControlSequence(new DTLifinlist());
      registerControlSequence(new DTLnumitemsinlist());
      registerControlSequence(new DTLlistelement());
      registerControlSequence(new DTLfetchlistelement());
      registerControlSequence(new DTLformatlist());

      registerControlSequence(new AtFirstOfOne("DTLlistformatitem"));
      registerControlSequence(new GenericCommand("DTLlistformatoxford"));
      registerControlSequence(new GenericCommand("DTLlistformatsep",
        null, getListener().createString(", ")));
      registerControlSequence(new GenericCommand(true, "DTLlistformatlastsep",
        null, new TeXObject[] {new TeXCsRef("space"), 
          new TeXCsRef("DTLandname"), new TeXCsRef("space")}));
      registerControlSequence(new DTLandname());

      registerControlSequence(new DTLinsertinto(this));
      registerControlSequence(new DTLinsertinto("edtlinsertinto", true, this));
      registerControlSequence(new DTLcompare());
      registerControlSequence(new DTLcompare("dtlicompare", false));

      registerControlSequence(new DTLsetnumberchars());
      registerControlSequence(new TextualContentCommand("@dtl@numbergroupchar", ","));
      registerControlSequence(new TextualContentCommand("@dtl@decimal", "."));

      registerControlSequence(
        new NumericFormatter("__texparser_fmt_integer_value:n", new DecimalFormat("#,##0"), "."));

      registerControlSequence(
        new NumericFormatter("__texparser_fmt_decimal_value:n", new DecimalFormat("#,##0.0#####")));

      registerControlSequence(
        new NumericFormatter("__texparser_fmt_currency_value:n", new DecimalFormat("#,##0.00")));

      addCurrencySymbol("$");
      addCurrencySymbol("pounds");
      addCurrencySymbol("textsterling");
      addCurrencySymbol("textdollar");
      addCurrencySymbol("textyen");
      addCurrencySymbol("texteuro");
      addCurrencySymbol("textwon");
      addCurrencySymbol("textcurrency");
      addCurrencySymbol("euro");
      addCurrencySymbol("yen");

      // datatool-base v3.0:

      registerControlSequence(new DTLsetup(this));
      registerControlSequence(new DatumMarker());

      registerControlSequence(new IntegerContentCommand("c_datatool_string_int",
        DatumType.STRING.getValue(), true));
      registerControlSequence(new IntegerContentCommand("c_datatool_integer_int",
        DatumType.INTEGER.getValue(), true));
      registerControlSequence(new IntegerContentCommand("c_datatool_decimal_int",
        DatumType.DECIMAL.getValue(), true));
      registerControlSequence(new IntegerContentCommand("c_datatool_currency_int",
        DatumType.CURRENCY.getValue(), true));
      registerControlSequence(new IntegerContentCommand("c_datatool_unknown_int",
        DatumType.UNKNOWN.getValue(), true));
   }

   @Override
   protected void preOptions(TeXObjectList stack) throws IOException
   {
      getListener().requirepackage("etoolbox", stack);

      ifThenSty = (IfThenSty)getListener().requirepackage("ifthen", stack);
   }

   public void setDataBaseSty(DataToolSty datatoolSty)
   {
      this.datatoolSty = datatoolSty;
   }

   public void processSetupOption(String key, TeXObject value, TeXObjectList stack)
   throws IOException
   {
      if (key.equals("verbose"))
      {// ignore
      }
      else if (key.equals("initial-purify"))
      {// TODO
      }
      else if (key.equals("compare"))
      {// TODO
      }
      else if (key.equals("lists"))
      {// TODO
      }
      else if (key.equals("utf8") || key.equals("math") 
         || key.equals("locales") || key.equals("nolocale"))
      {// ignore
      }
      else if (datatoolSty != null)
      {
         datatoolSty.processSetupOption(key, value, stack);
      }
      else
      {
         throw new LaTeXSyntaxException(getParser(),
          LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
          key, "datatool-base");
      }
   }

   public void addCurrencySymbol(TeXObject symbol)
   {
      if (currencySymbolList == null)
      {
         currencySymbolList = new Vector<TeXObject>();
         defaultCurrency = symbol;
      }

      if (symbol == null)
      {
         throw new NullPointerException();
      }

      currencySymbolList.add(symbol);
   }

   public void addCurrencySymbol(String csName)
   {
      addCurrencySymbol(new TeXCsRef(csName));
   }

   public boolean isCurrencySymbol(TeXObject obj)
   {
      if (obj == null) return false;

      for (TeXObject symbol: currencySymbolList)
      {
         if (symbol instanceof ControlSequence 
             && obj instanceof ControlSequence
             && ((ControlSequence)symbol).getName().equals(
               ((ControlSequence)obj).getName()))
         {
            return true;
         }

         if (symbol.equals(obj))
         {
            return true;
         }
      }

      return false;
   }

   public void setDefaultCurrency(TeXObject symbol)
   {
      defaultCurrency = symbol;
   }

   public IfThenSty getIfThenSty()
   {
      return ifThenSty;
   }

   public CountRegister getSortCountRegister()
   {
      return sortCountReg;
   }

   public static int parseInt(String str, TeXParser parser)
    throws TeXSyntaxException
   {
      ControlSequence cs 
        = parser.getControlSequence("__texparser_fmt_integer_value:n");

      try
      {
         if (cs instanceof NumericFormatter)
         {
            return ((NumericFormatter)cs).parse(str).intValue();
         }

         return Integer.parseInt(str);
      }
      catch (NumberFormatException | ParseException e)
      {
         throw new TeXSyntaxException(e, parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public static double parseDecimal(String str, TeXParser parser)
    throws TeXSyntaxException
   {
      ControlSequence cs
        = parser.getControlSequence("__texparser_fmt_decimal_value:n");

      try
      {
         if (cs instanceof NumericFormatter)
         {
            return ((NumericFormatter)cs).parse(str).doubleValue();
         }

         return Double.parseDouble(str);
      }
      catch (NumberFormatException | ParseException e)
      {
         throw new TeXSyntaxException(e, parser, 
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public static double parseCurrencyDecimal(String str, TeXParser parser)
    throws TeXSyntaxException
   {
      ControlSequence cs 
        = parser.getControlSequence("__texparser_fmt_currency_value:n");
            
      try
      {
         if (cs instanceof NumericFormatter)
         {
            return ((NumericFormatter)cs).parse(str).doubleValue();
         }  

         return Double.parseDouble(str);
      }     
      catch (NumberFormatException | ParseException e)
      {
         throw new TeXSyntaxException(e, parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public DataElement getElement(TeXObject entry)
     throws IOException
   {
      if (entry instanceof DatumElement)
      {
         return (DatumElement)entry;
      }

      TeXParser parser = getListener().getParser();

      boolean useDatum
        = TeXParserUtils.isTrue("l__datatool_db_store_datum_bool", parser);

      if (entry instanceof DataElement)
      {
         if (useDatum)
         {
            DatumType type = ((DataElement)entry).getDatumType();

            switch (type)
            {
               case INTEGER:
                 return new DatumElement(entry,
                    new UserNumber(((DataNumericElement)entry).intValue()),
                    null, type);
               case DECIMAL:
                 return new DatumElement(entry,
                    new TeXFloatingPoint(((DataNumericElement)entry).doubleValue()),
                    null, type);
               case CURRENCY:
                 return new DatumElement(entry,
                    new TeXFloatingPoint(((DataNumericElement)entry).doubleValue()),
                    ((DataElement)entry).getCurrencySymbol(), type);
               default:
                 return new DatumElement(entry);
            }
         }
         else
         {
            return (DataElement)entry;
         }
      }

      TeXObject original = null;

      if (useDatum)
      {
         original = (TeXObject)entry.clone();
      }

      if (entry instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)entry;
         TeXObject first = list.peekStack();

         if (first == null)
         {// empty
            return null;
         }

         // does it start with a datum marker?

         if (DatumMarker.isDatumMarker(first))
         {
            DataElement elem = DatumMarker.popDataElement(useDatum, parser, list);

            list.trim();

            if (!list.isEmpty())
            {
               throw new LaTeXSyntaxException(parser, 
                 LaTeXSyntaxException.ERROR_TRAILING_CONTENT, first, 
                   list.toString(parser));
            }

            return elem;
         }

         // does it start with a currency marker?

         if (isCurrencySymbol(first))
         {
            first = list.popStack(parser);

            // is the remainder numerical?

            try
            {
               ControlSequence cs = parser.getControlSequence(
                 "__texparser_fmt_currency_value:n");

               String str = list.toString(parser).trim();
               double value;

               if (cs instanceof NumericFormatter)
               {
                  value = ((NumericFormatter)cs).parse(str).doubleValue();
               }
               else
               {
                  value = Double.parseDouble(str);
               }

               if (useDatum)
               {
                  return new DatumElement(original,
                     new TeXFloatingPoint(value), first, DatumType.CURRENCY);
               }
               else
               {
                  return new DataCurrencyElement(first, value);
               }
            }
            catch (NumberFormatException | ParseException e)
            {// not numeric

               list.add(0, first);
               return new DataStringElement(list);
            }
         }
      }

      String str = entry.toString(parser).trim();

      // is it an integer?

      try
      {
         ControlSequence cs = parser.getControlSequence(
            "__texparser_fmt_integer_value:n");

         int value;

         if (cs instanceof NumericFormatter)
         {
            value = ((NumericFormatter)cs).parse(str).intValue();
         }
         else
         {
            value = Integer.parseInt(str);
         }

         if (useDatum)
         {
            return new DatumElement(original, new UserNumber(value),
             null, DatumType.INTEGER);
         }
         else
         {
            return new DataIntElement(value);
         }
      }
      catch (NumberFormatException | ParseException e)
      {
      }

      // is it a real number?

      try
      {
         ControlSequence cs = parser.getControlSequence(
            "__texparser_fmt_decimal_value:n");

         double value;

         if (cs instanceof NumericFormatter)
         {
            value = ((NumericFormatter)cs).parse(str).doubleValue();
         }
         else
         {
            value = Double.parseDouble(str);
         }

         if (useDatum)
         {
            return new DatumElement(original, new TeXFloatingPoint(value),
              null, DatumType.DECIMAL);
         }
         else
         {
            return new DataRealElement(value);
         }
      }
      catch (NumberFormatException | ParseException e)
      {
      }

      if (useDatum)
      {
         if (entry.isEmpty())
         {
            return new DatumElement();
         }
         else
         {
            return new DatumElement(entry);
         }
      }
      else
      {
         if (entry instanceof TeXObjectList)
         {
            return new DataStringElement((TeXObjectList)entry);
         }

         DataStringElement elem = new DataStringElement();
         elem.add(entry);
         return elem;
      }
   }

   private IfThenSty ifThenSty;

   private Vector<TeXObject> currencySymbolList;
   private TeXObject defaultCurrency;

   private CountRegister sortCountReg;

   private DataToolSty datatoolSty;

   public static final String INDEX_OUT_OF_RANGE="datatool.index.outofrange";
}
