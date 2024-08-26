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
import java.util.regex.Pattern;

import java.io.IOException;

import java.text.DecimalFormat;
import java.text.ParseException;

import java.math.BigDecimal;

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

      // Numeric

      registerControlSequence(new DTLsetnumberchars());
      registerControlSequence(new TextualContentCommand("@dtl@numbergroupchar", ","));
      registerControlSequence(new TextualContentCommand("@dtl@decimal", "."));

      registerControlSequence(
        new NumericFormatter(FMT_INTEGER_VALUE, new DecimalFormat("#,##0"), "."));

      registerControlSequence(
        new NumericFormatter(FMT_DECIMAL_VALUE, new DecimalFormat("#,##0.0#####")));

      registerControlSequence(
        new NumericFormatter(FMT_CURRENCY_VALUE, new DecimalFormat("#,##0.00")));

      registerControlSequence(new DTLpadleadingzeros());
      registerControlSequence(new TextualContentCommand(
        "dtlpadleadingzerosminus", "-"));
      registerControlSequence(new TextualContentCommand(
        "dtlpadleadingzerosplus", ""));

      // Currency

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

      defineCurrency(null, "XXX", new TeXCsRef("textcurrency"),
        listener.getOther(0x00A4));

      defineCurrency(null, "XBT", new TeXCsRef("faBtc"),
        listener.getOther(0x20BF));

      defineCurrency(null, "EUR", new TeXCsRef("euro"),
        listener.getOther(0x20AC));

      registerControlSequence(new GenericCommand(true,
        DTL_CURRENCY_CSNAME, null, new TeXCsRef("$")));

      registerControlSequence(new LaTeXGenericCommand(true,
        "DTLcurrency", false, "m",
        TeXParserUtils.createStack(parser, 
         new TeXCsRef("DTLfmtcurrency"), new TeXCsRef(DTL_CURRENCY_CSNAME),
         TeXParserUtils.createGroup(parser, listener.getParam(1)))));

      registerControlSequence(new LaTeXGenericCommand(true, "DTLfmtcurrency", 
       "mm", TeXParserUtils.createStack(listener, 
          new TeXCsRef("dtlcurrdefaultfmt"),
          TeXParserUtils.createGroup(listener, listener.getParam(1)),
          TeXParserUtils.createGroup(listener, listener.getParam(2))
          )
      ));

      registerControlSequence(new LaTeXGenericCommand(true, "dtlcurrdefaultfmt", 
       "mm", TeXParserUtils.createStack(listener, 
         new TeXCsRef("dtlcurrprefixfmt"),
          TeXParserUtils.createGroup(listener, listener.getParam(1)),
          TeXParserUtils.createGroup(listener, listener.getParam(2))
          )
      ));

      registerControlSequence(new LaTeXGenericCommand(true,
        "dtlcurrprefixfmt", false, "mm",
        TeXParserUtils.createStack(parser, 
          listener.getParam(1),
          new TeXCsRef("dtlcurrfmtsep"),
          listener.getParam(2))
      ));

      registerControlSequence(new LaTeXGenericCommand(true,
        "dtlcurrsuffixfmt", false, "mm",
        TeXParserUtils.createStack(parser, 
          listener.getParam(2),
          new TeXCsRef("dtlcurrfmtsep"),
          listener.getParam(1))
      ));

      registerControlSequence(new GenericCommand(true,
        "dtlcurrfmtsep", null, TeXParserUtils.createStack(listener,
        new TeXCsRef("DTLcurrCodeOrSymOrStr"),
         TeXParserUtils.createGroup(listener, listener.getActiveChar('~')),
         listener.createGroup(), listener.createGroup())
      ));

      registerControlSequence(new TextualContentCommand("DTLCurrencyCode", "XXX"));
      registerControlSequence(new AtNumberOfNumber("DTLcurrCodeOrSymOrStr", 2, 3));

      registerControlSequence(new GenericCommand(true,
        "DTLdefaultEURcurrencyfmt", null, new TeXCsRef("dtlcurrdefaultfmt")));

      registerControlSequence(new DTLdefcurrency(this));

      // Null

      nullMarker = TeXParserUtils.createStack(listener,
       listener.getOther(' '),
       listener.getLetter('U'),
       listener.getLetter('n'),
       listener.getLetter('d'),
       listener.getLetter('e'),
       listener.getLetter('f'),
       listener.getLetter('i'),
       listener.getLetter('n'),
       listener.getLetter('e'),
       listener.getLetter('d'),
       listener.getOther(' '),
       listener.getLetter('V'),
       listener.getLetter('a'),
       listener.getLetter('l'),
       listener.getLetter('u'),
       listener.getLetter('e'),
       listener.getLetter(' ')
      );

      registerControlSequence(
        new GenericCommand(true, NULL_VALUE_CSNAME, 
         null, nullMarker));

      noValueCs = new GenericCommand(true, "dtlnovalue",
          null, new TeXCsRef(NULL_VALUE_CSNAME));

      registerControlSequence(noValueCs);

      registerControlSequence(
        new IntegerContentCommand(NUMBER_NULL_CSNAME, 0, true));

      numberNullCs = new GenericCommand(true, "DTLnumbernull",
          null, new TeXCsRef(NUMBER_NULL_CSNAME));

      registerControlSequence(numberNullCs);

      registerControlSequence(
        new TextualContentCommand(STRING_NULL_CSNAME, "NULL", true));

      stringNullCs = new GenericCommand(true, "DTLstringnull",
          null, new TeXCsRef(STRING_NULL_CSNAME));

      registerControlSequence(stringNullCs);

      registerControlSequence(new DTLsetup(this));
      registerControlSequence(new DatumMarker());

      registerControlSequence(new IntegerContentCommand(
        DatumType.STRING.getCsName(),
        DatumType.STRING.getValue(), true));
      registerControlSequence(new IntegerContentCommand(
        DatumType.INTEGER.getCsName(),
        DatumType.INTEGER.getValue(), true));
      registerControlSequence(new IntegerContentCommand(
        DatumType.DECIMAL.getCsName(),
        DatumType.DECIMAL.getValue(), true));
      registerControlSequence(new IntegerContentCommand(
        DatumType.CURRENCY.getCsName(),
        DatumType.CURRENCY.getValue(), true));
      registerControlSequence(new IntegerContentCommand(
        DatumType.UNKNOWN.getCsName(),
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
      String symLabel = symbol.toString(getParser());

      ControlSequence cs = getParser().getControlSequence(
        "dtl@curr@" + symLabel + "@fmt");

      if (cs == null)
      {
         getParser().putControlSequence(
            new TextualContentCommand("DTLCurrencyCode", "XXX"));
         getParser().putControlSequence(true,
           new GenericCommand(true, DTL_CURRENCY_CSNAME, null, symbol));
      }
      else
      {
         getParser().putControlSequence(
            new TextualContentCommand("DTLCurrencyCode", symLabel));

         getParser().putControlSequence(true,
           new GenericCommand(true, DTL_CURRENCY_CSNAME, null,
           new TeXCsRef("DTLcurr"+symLabel)));

         getParser().putControlSequence(true,
          new GenericCommand(true, "DTLfmtcurrency", null, cs));
      }
   }

   public void defineCurrency(TeXObject fmt, String label, TeXObject sym,
     TeXObject str)
   {
      if (fmt == null)
      {
         fmt = new TeXCsRef("dtlcurrdefaultfmt");
      }

      getParser().putControlSequence(true,
        new GenericCommand(true, "dtl@curr@"+label+"@str", null, str));

      getParser().putControlSequence(true,
        new GenericCommand(true, "dtl@curr@"+label+"@sym", null, sym));

      getParser().putControlSequence(true,
        new GenericCommand(true, "DTLcurr"+label, null, 
         TeXParserUtils.createStack(listener,
            new TeXCsRef("dtltexorsort"),
            TeXParserUtils.createGroup(listener,
              new TeXCsRef("DTLcurrCodeOrSymOrStr"),
               TeXParserUtils.createGroup(listener, listener.getParam(1)),
               new TeXCsRef("dtl@curr@"+label+"@sym"),
               new TeXCsRef("dtl@curr@"+label+"@str")
            ),
            TeXParserUtils.createGroup(listener,
             new TeXCsRef("dtl@curr@"+label+"@str"))
         )
      ));

      addCurrencySymbol("DTLcurr"+label);
      addCurrencySymbol(sym);
      addCurrencySymbol(str);

      getParser().putControlSequence(true,
         new GenericCommand(true, "dtl@curr@"+label+"@fmt", null, fmt));
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
      ControlSequence cs = parser.getControlSequence(FMT_INTEGER_VALUE);

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
        = parser.getControlSequence(FMT_DECIMAL_VALUE);

      try
      {
         if (cs instanceof NumericFormatter)
         {
            return ((NumericFormatter)cs).parse(str).doubleValue();
         }

         if (SCIENTIFIC_PATTERN.matcher(str).matches())
         {
            try
            {
               return (new BigDecimal(str)).doubleValue();
            }
            catch (NumberFormatException e)
            {// shouldn't happen
            }
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
        = parser.getControlSequence(FMT_CURRENCY_VALUE);
            
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
        = TeXParserUtils.isTrue(DataToolSty.DB_STORE_DATUM_BOOL, parser);

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

      TeXObject original = original = (TeXObject)entry.clone();

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
                 FMT_CURRENCY_VALUE);

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
                  return new DataCurrencyElement(first, value, original);
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

      // is it scientific notation?

      if (SCIENTIFIC_PATTERN.matcher(str).matches())
      {
         try
         {
            double value = (new BigDecimal(str)).doubleValue();

            if (useDatum)
            {
               return new DatumElement(original, new TeXFloatingPoint(value),
                 null, DatumType.DECIMAL);
            }
            else
            {
               return new DataRealElement(value, original);
            }
         }
         catch (NumberFormatException e)
         {// shouldn't happen
         }
      }

      // is it an integer?

      try
      {
         ControlSequence cs = parser.getControlSequence(FMT_INTEGER_VALUE);

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
            return new DataIntElement(value, original);
         }
      }
      catch (NumberFormatException | ParseException e)
      {
      }

      // is it a real number?

      try
      {
         ControlSequence cs = parser.getControlSequence(FMT_DECIMAL_VALUE);

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
            return new DataRealElement(value, original);
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

   public boolean isNull(TeXObject object)
   {
      if (object instanceof AssignedMacro)
      {
         object = ((AssignedMacro)object).getBaseUnderlying();
      }

      if (object.equals(noValueCs)
         || object.equals(numberNullCs)
         || object.equals(stringNullCs))
      {
         return true;
      }
      else if (object instanceof ControlSequence)
      {
         String name = ((ControlSequence)object).getName();

         if (name.equals(NULL_VALUE_CSNAME)
         || name.equals(NUMBER_NULL_CSNAME)
         || name.equals(STRING_NULL_CSNAME)
            )
         {
            return true;
         }
      }
      else if (getParser().isStack(object))
      {
         if (((TeXObjectList)object).size() == 1)
         {
            object = ((TeXObjectList)object).firstElement();

            if (object instanceof ControlSequence)
            {
                String name = ((ControlSequence)object).getName();

                if (name.equals(NULL_VALUE_CSNAME)
                || name.equals(NUMBER_NULL_CSNAME)
                || name.equals(STRING_NULL_CSNAME)
                )
                {
                   return true;
                }
            }
         }
         else if (((TeXObjectList)object).equalsMatchCatCode(nullMarker))
         {
            return true;
         }
      }

      return false;
   }

   private IfThenSty ifThenSty;

   private Vector<TeXObject> currencySymbolList;

   private CountRegister sortCountReg;

   private TeXObjectList nullMarker;
   private GenericCommand noValueCs, numberNullCs, stringNullCs;

   private DataToolSty datatoolSty;

   public static final String INDEX_OUT_OF_RANGE="datatool.index.outofrange";

   public static final String FMT_INTEGER_VALUE
      = "__texparser_fmt_integer_value:n";

   public static final String FMT_DECIMAL_VALUE
      = "__texparser_fmt_decimal_value:n";

   public static final String FMT_CURRENCY_VALUE
      = "__texparser_fmt_currency_value:n";

   public static final String DATUM_NNNN = "__datatool_datum:nnnn";

   public static final String NULL_VALUE_CSNAME = "c_datatool_nullvalue_tl";
   public static final String NUMBER_NULL_CSNAME = "@dtlnumbernull";
   public static final String STRING_NULL_CSNAME = "@dtlstringnull";

   public static final String DTL_CURRENCY_CSNAME = "@dtl@currency";

   public static final String TMPA_VAR = "l__datatool_tmpa_tl";
   public static final String TMPB_VAR = "l__datatool_tmpb_tl";

   public static final Pattern SCIENTIFIC_PATTERN =
     Pattern.compile("[+\\-]?\\d+(\\.\\d+)?[Ee][+\\-]?\\d+");
}
