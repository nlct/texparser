/*
    Copyright (C) 2013-2025 Nicola L.C. Talbot
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

import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Pattern;

import java.io.IOException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.math.BigDecimal;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.ifthen.IfThenSty;
import com.dickimawbooks.texparserlib.latex.latex3.LaTeX3Boolean;
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

      registerControlSequence(new LaTeX3Boolean(PARSE_DATETIME_BOOL, false));
      registerControlSequence(new LaTeX3Boolean(REFORMAT_DATETIME_BOOL, false));

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

      if (numericLocale == null)
      {
         setNumericLocale(getListener().getTeXApp().getDefaultLocale());
      }

      registerControlSequence(new AtFirstOfTwo("DTLtemporalvalue"));

      registerControlSequence(
        new DateFormatter(FMT_DATETIME_VALUE, DATE_TIME_FORMAT));

      registerControlSequence(
        new DateFormatter(FMT_DATE_VALUE, DATE_FORMAT));

      registerControlSequence(
        new DateFormatter(FMT_TIME_VALUE, TIME_FORMAT));

      registerControlSequence(new DTLpadleadingzeros());
      registerControlSequence(new TextualContentCommand(
        "dtlpadleadingzerosminus", "-"));
      registerControlSequence(new TextualContentCommand(
        "dtlpadleadingzerosplus", ""));

      registerControlSequence(new DataToolDateFmt());
      registerControlSequence(new DataToolTimeFmt());
      registerControlSequence(new DataToolTimeZoneFmt());
      registerControlSequence(new TextualContentCommand(
        "DataToolTimeStampFmtSep", " "));

      registerControlSequence(new DataToolDateTimeFmt());

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
      registerControlSequence(new AtNumberOfNumber("datatool_datum_value:Nnnnn", 3, 5));
      registerControlSequence(new AtNumberOfNumber("datatool_datum_type:Nnnnn", 5, 5));

      registerControlSequence(new DTLdatumtype(this));
      registerControlSequence(new DTLdatumvalue(this));
      registerControlSequence(new DTLsettemporaldatum(this));

      registerControlSequence(new DTLparse(this));
      registerControlSequence(new DTLparse("xDTLparse", true, this));

      for (DatumType type : DatumType.values())
      {
         registerControlSequence(new IntegerContentCommand(
           type.getCsName(), type.getValue(), true));
      }

      registerControlSequence(new TextualContentCommand("datatoolpersoncomma", ", "));
      registerControlSequence(new TextualContentCommand("datatoolplacecomma", ", "));
      registerControlSequence(new TextualContentCommand("datatoolsubjectcomma", ", "));
      registerControlSequence(new TextualContentCommand("datatoolparenstart", " "));

      registerControlSequence(new LaTeXGenericCommand(true,
       "datatoolparen", "m", TeXParserUtils.createStack(listener,
        listener.getSpace(), listener.getOther('('),
        listener.getParam(1), listener.getOther(')'))));

      registerControlSequence(new GenericCommand(true, "datatoolasciistart"));
      registerControlSequence(new GenericCommand(true, "datatoolasciiend"));
      registerControlSequence(new GenericCommand(true, "datatoolctrlboundary"));

      registerControlSequence(new AtFirstOfTwo("dtltexorsort"));

      registerControlSequence(
       listener.createSymbol("datatool_cent_str", 0xA2));
      registerControlSequence(
       listener.createSymbol("datatool_pound_str", 0xA3));
      registerControlSequence(
       listener.createSymbol("datatool_currency_str", 0xA4));
      registerControlSequence(
       listener.createSymbol("datatool_yen_str", 0xA5));
      registerControlSequence(
       listener.createSymbol("datatool_middot_str", 0xB7));
      registerControlSequence(
       listener.createSymbol("datatool_colonsign_str", 0x20A1));
      registerControlSequence(
       listener.createSymbol("datatool_cruzerio_str", 0x20A2));
      registerControlSequence(
       listener.createSymbol("datatool_frenchfranc_str", 0x20A3));
      registerControlSequence(
       listener.createSymbol("datatool_lira_str", 0x20A4));
      registerControlSequence(
       listener.createSymbol("datatool_mill_str", 0x20A5));
      registerControlSequence(
       listener.createSymbol("datatool_naira_str", 0x20A6));
      registerControlSequence(
       listener.createSymbol("datatool_peseta_str", 0x20A7));
      registerControlSequence(
       listener.createSymbol("datatool_rupee_str", 0x20A8));
      registerControlSequence(
       listener.createSymbol("datatool_won_str", 0x20A9));
      registerControlSequence(
       listener.createSymbol("datatool_shekel_str", 0x20AA));
      registerControlSequence(
       listener.createSymbol("datatool_dong_str", 0x20AB));
      registerControlSequence(
       listener.createSymbol("datatool_euro_str", 0x20AC));
      registerControlSequence(
       listener.createSymbol("datatool_kip_str", 0x20AD));
      registerControlSequence(
       listener.createSymbol("datatool_tugrik_str", 0x20AE));
      registerControlSequence(
       listener.createSymbol("datatool_drachma_str", 0x20AF));
      registerControlSequence(
       listener.createSymbol("datatool_germanpenny_str", 0x20B0));
      registerControlSequence(
       listener.createSymbol("datatool_peso_str", 0x20B1));
      registerControlSequence(
       listener.createSymbol("datatool_guarani_str", 0x20B2));
      registerControlSequence(
       listener.createSymbol("datatool_austral_str", 0x20B3));
      registerControlSequence(
       listener.createSymbol("datatool_hryvnia_str", 0x20B4));
      registerControlSequence(
       listener.createSymbol("datatool_cedi_str", 0x20B5));
      registerControlSequence(
       listener.createSymbol("datatool_livretournois_str", 0x20B6));
      registerControlSequence(
       listener.createSymbol("datatool_spesmilo_str", 0x20B7));
      registerControlSequence(
       listener.createSymbol("datatool_tenge_str", 0x20B8));
      registerControlSequence(
       listener.createSymbol("datatool_indianrupee_str", 0x20B9));
      registerControlSequence(
       listener.createSymbol("datatool_turkishlira_str", 0x20BA));
      registerControlSequence(
       listener.createSymbol("datatool_nordicmark_str", 0x20BB));
      registerControlSequence(
       listener.createSymbol("datatool_manat_str", 0x20BC));
      registerControlSequence(
       listener.createSymbol("datatool_ruble_str", 0x20BD));
      registerControlSequence(
       listener.createSymbol("datatool_lari_str", 0x20BE));
      registerControlSequence(
       listener.createSymbol("datatool_bitcoin_str", 0x20BF));
      registerControlSequence(
       listener.createSymbol("datatool_som_str", 0x20C0));
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
      else if (key.equals("datetime"))
      {
         if (value == null)
         {
            throw new LaTeXSyntaxException(getParser(),
              LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE,
              key);
         }

         String strVal = getParser().expandToString(value, stack).trim();

         if (strVal.equals("false"))
         {
            getParser().putControlSequence(true,
              new LaTeX3Boolean(PARSE_DATETIME_BOOL, false));
         }
         else if (strVal.equals("parse-only"))
         {
            getParser().putControlSequence(true,
              new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
            getParser().putControlSequence(true,
              new LaTeX3Boolean(REFORMAT_DATETIME_BOOL, false));
         }
         else if (strVal.equals("reformat"))
         {
            getParser().putControlSequence(true,
              new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
            getParser().putControlSequence(true,
              new LaTeX3Boolean(REFORMAT_DATETIME_BOOL, true));
         }
         else
         {
            throw new LaTeXSyntaxException(getParser(),
              LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE,
              key, strVal);
         }
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
      boolean useDatum
        = TeXParserUtils.isTrue(DataToolSty.DB_STORE_DATUM_BOOL, getParser());

      return getElement(entry, useDatum);
   }

   public DataElement getElement(TeXObject entry, boolean useDatum)
     throws IOException
   {
      TeXParser parser = getListener().getParser();

      if (entry instanceof DatumElement)
      {
         return (DatumElement)entry;
      }

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
               case DATETIME:
               case TIME:
                 return new DatumElement(entry,
                   new TeXFloatingPoint(((DataNumericElement)entry).doubleValue()),
                   ((DataElement)entry).getTeXValue(parser), type);
               case DATE:
                 return new DatumElement(entry,
                   new TeXLongNumber(((DataNumericElement)entry).longValue()),
                   ((DataElement)entry).getTeXValue(parser), type);
               default:
                 return new DatumElement(entry);
            }
         }
         else
         {
            return (DataElement)entry;
         }
      }

      TeXObject original = (TeXObject)entry.clone();

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

      if (isParseTemporalOn())
      {
         // is it date/time?
         DataElement elem = parseTemporal(str, entry, useDatum);

         if (elem != null) return elem;
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

   protected DataElement parseTemporal(String str, TeXObject original,
       boolean useDatum)
     throws IOException
   {
      return parseTemporal(str, original, useDatum, isReformatTemporalOn());
   }

   protected DataElement parseTemporal(String str, TeXObject original,
      boolean useDatum, boolean reformatOriginal)
     throws IOException
   {
      try
      {
         Julian julian = Julian.create(str);

         return julian.toDataElement(getListener(), original,
           useDatum, reformatOriginal);
      }
      catch (IllegalArgumentException e)
      {
         return null;
      }
   }

   public boolean isParseTemporalOn()
   {
      return TeXParserUtils.isTrue(PARSE_DATETIME_BOOL, getParser());
   }

   public boolean isReformatTemporalOn()
   {
      return TeXParserUtils.isTrue(REFORMAT_DATETIME_BOOL, getParser());
   }

   public static double toJulianDate(Date date)
   {
      return unixEpochMillisToJulianDate(date.getTime());
   }

   public static double unixEpochSecondsToJulianDate(long timeInSecs)
   {
      return timeInSecs / 86400.0 + 2440587.5;
   }

   public static double unixEpochMillisToJulianDate(long timeInMillis)
   {
      return unixEpochSecondsToJulianDate(timeInMillis/1000L);
   }

   public static long unixEpochSecondsFromJulianDate(long jd)
   {
      return unixEpochSecondsFromJulianDate((double)jd);
   }

   public static long unixEpochSecondsFromJulianDate(double jdt)
   {
      return (long)Math.round((jdt - 2440587.5) * 86400.0);
   }

   public static long unixEpochMillisFromJulianDate(long jd)
   {
      return unixEpochMillisFromJulianDate((double)jd);
   }

   public static long unixEpochMillisFromJulianDate(double jdt)
   {
      return (long)Math.round((jdt - 2440587.5) * 86400000.0);
   }

   public static String formatDateTime(Number num)
   {
      return DATE_TIME_FORMAT.format(
       new Date(unixEpochMillisFromJulianDate(num.doubleValue())));
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
          || name.equals(OLD_NULL_VALUE_CSNAME)
            )
         {
            return true;
         }
      }
      else if (getParser().isStack(object))
      {
         if (TeXParserUtils.onlyContainsControlSequence(
               (TeXObjectList)object, NULL_VALUE_CSNAME,
                 NUMBER_NULL_CSNAME, STRING_NULL_CSNAME, OLD_NULL_VALUE_CSNAME)
          || ((TeXObjectList)object).equalsMatchCatCode(nullMarker))
         {
            return true;
         }
      }

      return false;
   }

   public Locale getNumericLocale()
   {
      return numericLocale;
   }

   public void setNumericLocale(Locale locale)
   {
      numericLocale = locale;

      DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);

      registerControlSequence(new TextualContentCommand("@dtl@numbergroupchar",
        ""+symbols.getGroupingSeparator()));
      registerControlSequence(new TextualContentCommand("@dtl@decimal",
        ""+symbols.getDecimalSeparator()));

      registerControlSequence(
        new NumericFormatter(FMT_INTEGER_VALUE,
          NumberFormat.getIntegerInstance(locale), symbols, true));

      registerControlSequence(
        new NumericFormatter(FMT_DECIMAL_VALUE, 
          NumberFormat.getNumberInstance(locale), symbols, false));

      registerControlSequence(
        new NumericFormatter(FMT_CURRENCY_VALUE,
           new DecimalFormat("#,##0.00", symbols)));
   }

   private IfThenSty ifThenSty;

   private Locale numericLocale;

   private Vector<TeXObject> currencySymbolList;

   private CountRegister sortCountReg;

   private TeXObjectList nullMarker;
   private GenericCommand noValueCs, numberNullCs, stringNullCs;

   private DataToolSty datatoolSty;

   public static final String INDEX_OUT_OF_RANGE="datatool.index.outofrange";
   public static final String INVALID_DATE_TIME="datatool.invalid.datetime";

   public static final String FMT_INTEGER_VALUE
      = "__texparser_fmt_integer_value:n";

   public static final String FMT_DECIMAL_VALUE
      = "__texparser_fmt_decimal_value:n";

   public static final String FMT_CURRENCY_VALUE
      = "__texparser_fmt_currency_value:n";

   public static final String FMT_DATETIME_VALUE
      = "__texparser_fmt_datetime_value:n";

   public static final String FMT_DATE_VALUE
      = "__texparser_fmt_date_value:n";

   public static final String FMT_TIME_VALUE
      = "__texparser_fmt_time_value:n";

   static final SimpleDateFormat DATE_TIME_FORMAT
      = new SimpleDateFormat("y-MM-dd'T'HH:mm:ssX");
   static final SimpleDateFormat LOCAL_DATE_TIME_FORMAT
      = new SimpleDateFormat("y-MM-dd'T'HH:mm:ss");
   static final SimpleDateFormat DATE_FORMAT
      = new SimpleDateFormat("y-MM-dd");
   static final SimpleDateFormat TIME_FORMAT
      = new SimpleDateFormat("HH:mm:ss");

   static final Pattern TIMESTAMP_PATTERN
     = Pattern.compile("([+\\-]?\\d+)-(\\d{2})-(\\d{2})[ T](\\d{2}):(\\d{2})(?:(\\d{2}))?(Z|[+\\-]\\d{2}:\\d{2})?");

   public static final String DATUM_NNNN = "__datatool_datum:nnnn";

   /**
    * The old control sequence name for the token representing a
    * missing value.
    * Note that datatool.sty v3.0 has replaced the internal command
    * representing null with a constant token list, but datatooltk
    * still uses the old name as representing a missing value. 
    */
   public static final String OLD_NULL_VALUE_CSNAME = "@dtlnovalue";

   /**
    * The new control sequence name for the token representing a
    * missing value.
    */

   public static final String NULL_VALUE_CSNAME = "c_datatool_nullvalue_tl";
   public static final String NUMBER_NULL_CSNAME = "@dtlnumbernull";
   public static final String STRING_NULL_CSNAME = "@dtlstringnull";

   public static final String DTL_CURRENCY_CSNAME = "@dtl@currency";

   public static final String PARSE_DATETIME_BOOL = "l__datatool_parse_datetime_bool";
   public static final String REFORMAT_DATETIME_BOOL = "l__datatool_reformat_datetime_bool";

   public static final String TMPA_VAR = "l__datatool_tmpa_tl";
   public static final String TMPB_VAR = "l__datatool_tmpb_tl";

   public static final Pattern SCIENTIFIC_PATTERN =
     Pattern.compile("[+\\-]?\\d+(\\.\\d+)?[Ee][+\\-]?\\d+");
}
