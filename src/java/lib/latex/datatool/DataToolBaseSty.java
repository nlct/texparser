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
import java.util.Iterator;
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
import com.dickimawbooks.texparserlib.latex.latex3.SequenceCommand;
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
      registerControlSequence(new LaTeX3Boolean(REFORMAT_NUMERIC_BOOL, false));

      SequenceCommand seq = new SequenceCommand(AUTO_REFORMAT_TYPES_SEQ);
      seq.append(getListener().createString("integer"));
      seq.append(getListener().createString("decimal"));
      seq.append(getListener().createString("si"));
      seq.append(getListener().createString("currency"));
      seq.append(getListener().createString("datetime"));
      seq.append(getListener().createString("date"));
      seq.append(getListener().createString("time"));
      registerControlSequence(seq);

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

      registerControlSequence(new DTLsetnumberchars(this));

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
        new TeXCsRef("DTLcurrCodeOrSymOrChar"),
         TeXParserUtils.createGroup(listener, listener.getActiveChar('~')),
         listener.createGroup(), listener.createGroup())
      ));

      registerControlSequence(new TextualContentCommand("DTLCurrencyCode", "XXX"));
      registerControlSequence(new AtNumberOfNumber("DTLcurrCodeOrSymOrChar", 2, 3));

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

      addSymbolVars("cent", 0xA2);
      addSymbolVars("pound", 0xA3);
      addSymbolVars("currency", 0xA4);
      addSymbolVars("yen", 0xA5);
      addSymbolVars("middot", 0xB7);
      addSymbolVars("florin", 0x0192);
      addSymbolVars("baht", 0x0E3F);
      addSymbolVars("ecu", 0x20A0);
      addSymbolVars("colonsign", 0x20A1);
      addSymbolVars("cruzerio", 0x20A2);
      addSymbolVars("frenchfranc", 0x20A3);
      addSymbolVars("lira", 0x20A4);
      addSymbolVars("mill", 0x20A5);
      addSymbolVars("naira", 0x20A6);
      addSymbolVars("peseta", 0x20A7);
      addSymbolVars("rupee", 0x20A8);
      addSymbolVars("won", 0x20A9);
      addSymbolVars("shekel", 0x20AA);
      addSymbolVars("dong", 0x20AB);
      addSymbolVars("euro", 0x20AC);
      addSymbolVars("kip", 0x20AD);
      addSymbolVars("tugrik", 0x20AE);
      addSymbolVars("drachma", 0x20AF);
      addSymbolVars("germanpenny", 0x20B0);
      addSymbolVars("peso", 0x20B1);
      addSymbolVars("guarani", 0x20B2);
      addSymbolVars("austral", 0x20B3);
      addSymbolVars("hryvnia", 0x20B4);
      addSymbolVars("cedi", 0x20B5);
      addSymbolVars("livretournois", 0x20B6);
      addSymbolVars("spesmilo", 0x20B7);
      addSymbolVars("tenge", 0x20B8);
      addSymbolVars("indianrupee", 0x20B9);
      addSymbolVars("turkishlira", 0x20BA);
      addSymbolVars("nordicmark", 0x20BB);
      addSymbolVars("manat", 0x20BC);
      addSymbolVars("ruble", 0x20BD);
      addSymbolVars("lari", 0x20BE);
      addSymbolVars("bitcoin", 0x20BF);
      addSymbolVars("som", 0x20C0);
   }

   protected void addSymbolVars(String name, int cp)
   {
      registerControlSequence(
       listener.createSymbol("l_datatool_"+name+"_str", 0x20C0));
      registerControlSequence(
       listener.createSymbol("l_datatool_"+name+"_tl", 0x20C0));
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
      else if (key.equals("auto-reformat-types"))
      {
         SequenceCommand seq = new SequenceCommand(AUTO_REFORMAT_TYPES_SEQ);

         if (value != null)
         {
            String[] split = value.toString(getParser()).split(",");

            for (String spVal : split)
            {
               spVal = spVal.trim();

               if (spVal.isEmpty())
               {// ignore
               }
               else if ( spVal.equals("integer")
                      || spVal.equals("decimal")
                      || spVal.equals("si")
                      || spVal.equals("datetime")
                      || spVal.equals("date")
                      || spVal.equals("time")
                       )
               {
                  seq.append(getListener().createString(spVal));
               }
               else
               {
                  throw new LaTeXSyntaxException(getParser(),
                    LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE,
                    key, spVal);
               }
            }
         }

         getParser().putControlSequence(true, seq);
      }
      else if (key.equals("numeric"))
      {
         if (value == null)
         {
            throw new LaTeXSyntaxException(getParser(),
              LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE,
              key);
         }

         setupNumeric(TeXParserUtils.toKeyValList(value, getParser()), stack);
      }
      else if (key.equals("datetime"))
      {
         if (value == null)
         {
            throw new LaTeXSyntaxException(getParser(),
              LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE,
              key);
         }

         setupDateTime(TeXParserUtils.toKeyValList(value, getParser()), stack);
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

   public void setupNumeric(KeyValList options, TeXObjectList stack)
   throws IOException
   {
      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();

         TeXObject value = options.get(key);

         if (key.equals("auto-reformat"))
         {
            boolean on = true;

            if (value != null && !value.isEmpty()
                 && getParser().expandToString(value, stack).trim().equals("false"))
            {
               on = false;
            }

            getParser().putControlSequence(true,
              new LaTeX3Boolean(REFORMAT_NUMERIC_BOOL, on));
         }
         else if (key.equals("set-number-chars"))
         {
            TeXObjectList substack = TeXParserUtils.toList(value, getParser());

            TeXObject numGrp = substack.popArg(getParser());
            TeXObject decChar = substack.popArg(getParser());

            setNumberChars(numGrp, decChar);
         }
         else if (key.equals("region-number-chars"))
         {// TODO
         }
         else if (key.equals("set-currency"))
         {
            if (value == null)
            {
               throw new LaTeXSyntaxException(getParser(),
                 LaTeXSyntaxException.ERROR_MISSING_KEY_VALUE,
                 key);
            }

            setDefaultCurrency(value);
         }
         else if (key.equals("region-currency"))
         {// TODO
         }
         else if (key.equals("region-currency-prefix"))
         {// TODO
         }
         else if (key.equals("currency-symbol-style"))
         {// TODO
         }
         else
         {
            throw new LaTeXSyntaxException(getParser(),
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
             key, "numeric");
         }
      }
   }

   public void setupDateTime(KeyValList options, TeXObjectList stack)
   throws IOException
   {
      for (Iterator<String> it = options.keySet().iterator();
           it.hasNext(); )
      {
         String key = it.next();

         TeXObject value = options.get(key);

         if (key.equals("auto-reformat"))
         {
            String strVal = "true";

            if (value != null && !value.isEmpty())
            {
               strVal = getParser().expandToString(value, stack).trim();

               if (strVal.isEmpty())
               {
                  strVal = "true";
               }
            }

            getParser().putControlSequence(true,
              new LaTeX3Boolean(PARSE_DATETIME_BOOL, !strVal.equals("false")));

            // TODO : options "region", "iso", "datetime2"
         }
         else if (key.equals("parse"))
         {
            String strVal = "true";

            if (value != null && !value.isEmpty())
            {
               strVal = getParser().expandToString(value, stack).trim();

               if (strVal.isEmpty())
               {
                  strVal = "true";
               }
            }

            if (strVal.equals("false"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, false));
            }
            else if (strVal.equals("true"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
            }
            else if (strVal.equals("parse-only"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(REFORMAT_DATETIME_BOOL, false));
            }
            else if (strVal.equals("auto-reformat"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(REFORMAT_DATETIME_BOOL, true));
            }
            else if (strVal.equals("iso-only"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
// TODO
            }
            else if (strVal.equals("region-only"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
// TODO
            }
            else if (strVal.equals("iso+region"))
            {
               getParser().putControlSequence(true,
                 new LaTeX3Boolean(PARSE_DATETIME_BOOL, true));
// TODO
            }
            else
            {
               throw new LaTeXSyntaxException(getParser(),
                 LaTeXSyntaxException.ERROR_INVALID_OPTION_VALUE,
                 key, strVal);
            }
         }
         else
         {
            throw new LaTeXSyntaxException(getParser(),
             LaTeXSyntaxException.ERROR_UNKNOWN_OPTION,
             key, "numeric");
         }
      }
   }

   public void setNumberChars(TeXObject numGrp, TeXObject decChar)
   {
      setNumberChars(numGrp.toString(getParser()), decChar.toString(getParser()));
   }

   // TODO: allow regex
   public void setNumberChars(String numGrpChar, String decimalChar)
   {
      TeXParser parser = getParser();

      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator(decimalChar.charAt(0));
      symbols.setGroupingSeparator(numGrpChar.charAt(0));

      parser.putControlSequence(true,
        new TextualContentCommand("@dtl@numbergroupchar", numGrpChar));

      parser.putControlSequence(true,
        new TextualContentCommand("@dtl@decimal", decimalChar));

      DecimalFormat fmt = new DecimalFormat("#,##0", symbols);
      fmt.setParseIntegerOnly(true);
   
      parser.putControlSequence(true,
        new NumericFormatter(
          FMT_INTEGER_VALUE, fmt));

      parser.putControlSequence(true,
        new NumericFormatter(
          FMT_DECIMAL_VALUE,
          new DecimalFormat("#,##0.0######", symbols)));

      parser.putControlSequence(
        new NumericFormatter(DataToolBaseSty.FMT_CURRENCY_VALUE,
           new DecimalFormat("#,##0.00", symbols)));
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
              new TeXCsRef("DTLcurrCodeOrSymOrChar"),
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

   protected boolean isAutoReformatOn(String setting)
   {
      ControlSequence cs = getParser().getControlSequence(AUTO_REFORMAT_TYPES_SEQ);
      boolean found = false;

      if (cs instanceof SequenceCommand)
      {
         return ((SequenceCommand)cs).contains(setting);
      }

      return false;
   }

   public boolean isSIAutoReformatOn(boolean isReformatNumOn)
   {
      return isReformatNumOn && isAutoReformatOn("si");
   }

   public boolean isAutoReformatOn(DatumType type,
      boolean isReformatNumOn, boolean isReformatDateTimeOn)
   {
      switch (type)
      {
         case INTEGER:
           return isReformatNumOn && isAutoReformatOn("integer");
         case DECIMAL:
           return isReformatNumOn && isAutoReformatOn("decimal");
         case CURRENCY:
           return isReformatNumOn && isAutoReformatOn("currency");
         case DATETIME:
           return isReformatDateTimeOn && isAutoReformatOn("datetime");
         case DATE:
           return isReformatDateTimeOn && isAutoReformatOn("date");
         case TIME:
           return isReformatDateTimeOn && isAutoReformatOn("time");
      }

      return false;
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

   public DataElement getPlainElement(DatumType type, TeXObject entry)
     throws IOException
   {
      boolean useDatum
        = TeXParserUtils.isTrue(DataToolSty.DB_STORE_DATUM_BOOL, getParser());

      return getPlainElement(type, entry, useDatum);
   }

   public DataElement getPlainElement(DatumType type, TeXObject entry, boolean useDatum)
     throws IOException
   {
       boolean reformatNum = false;
       boolean reformatDateTime = false;

       if (type.isTemporal())
       {
          reformatDateTime =
            TeXParserUtils.isTrue(REFORMAT_DATETIME_BOOL, getParser());
       }
       else if (type.isNumeric())
       {
          reformatNum =
            TeXParserUtils.isTrue(REFORMAT_NUMERIC_BOOL, getParser());
       }

       return getPlainElement(type, entry, useDatum, reformatNum, reformatDateTime);
   }

   public DataElement getPlainElement(DatumType type, TeXObject entry, boolean useDatum,
      boolean autoReformatNumeric, boolean autoReformatDateTime)
     throws IOException
   {
      DataElement element;
      TeXObject content = entry;
      Number num = null;
      TeXNumber texNum = null;
      TeXObject currency = null;
      Julian julian = null;

      if (type.isNumeric())
      {
         try
         {
            String val = entry.toString(getParser());

            try
            {
               num = Integer.valueOf(val);
            }
            catch (NumberFormatException e)
            {
               num = Double.valueOf(val);
            }
         }
         catch (NumberFormatException e)
         {
            getListener().getTeXApp().error(e);
            type = DatumType.STRING;
            num = null;
         }
      }

      if (type == DatumType.INTEGER || type == DatumType.DECIMAL)
      {
         if (isAutoReformatOn(type, autoReformatNumeric, autoReformatDateTime))
         {
            content = formatNumber(type, num);
         }
      }
      else if (type == DatumType.CURRENCY)
      {
         TeXObjectList currencyList = getListener().createStack();
         currencyList.add(new TeXCsRef("DTLcurr"));
         currencyList.add(TeXParserUtils.createGroup(getListener(),
           new TeXCsRef("DTLCurrencyCode")));
         currency = currencyList;

         if (isAutoReformatOn(type, autoReformatNumeric, autoReformatDateTime))
         {
            TeXObjectList contentList = getListener().createStack();
            contentList.add(new TeXCsRef("DTLcurrency"));
            contentList.add(TeXParserUtils.createGroup(getListener(), entry));
            content = contentList;
         }
      }
      else if (type.isTemporal())
      {
         if (type == DatumType.TIME)
         {
            julian = Julian.createTime(num.doubleValue());
         }
         else if (type == DatumType.DATETIME)
         {
            julian = Julian.createDate(num.doubleValue());
         }
         else if (type == DatumType.DATE)
         {
            julian = Julian.createDay(num.intValue());
         }

         if (isAutoReformatOn(type, autoReformatNumeric, autoReformatDateTime))
         {
            content = julian.createTeXFormat(getListener());
         }

         if (useDatum)
         {
            TeXObjectList objVal = getListener().createStack();

            objVal.add(new TeXCsRef("DTLtemporalvalue"));
            objVal.add(TeXParserUtils.createGroup(getListener(), texNum));
            objVal.add(getListener().createGroup(julian.getTimeStamp()));

            return new DatumElement(content, texNum, objVal, null, julian, type);
         }
      }

      if (useDatum)
      {
         switch (type)
         {
            case INTEGER:
              texNum = new UserNumber(num.intValue());
            break;
            case CURRENCY:
            case DECIMAL:
              texNum = new TeXFloatingPoint(num.doubleValue());
            break;
         }

         element = new DatumElement(content, texNum, currency, type);
      }
      else
      {
         switch (type)
         {
            case INTEGER:
              element = new DataIntElement(num.intValue(), content);
            break;
            case DECIMAL:
              element = new DataRealElement(num.doubleValue(), content);
            break;
            case CURRENCY:
              element = new DataCurrencyElement(currency,
                num.doubleValue(), content);
            break;
            case DATE:
              element = new DataDateElement(julian, content);
            break;
            case TIME:
              element = new DataTimeElement(julian, content);
            break;
            case DATETIME:
              element = new DataDateTimeElement(julian, content);
            break;
            default:
              element = new DataStringElement(TeXParserUtils.toList(content, getParser()));
         }
      }

      return element;
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
       boolean reformatNum = TeXParserUtils.isTrue(REFORMAT_NUMERIC_BOOL, getParser());
       boolean reformatDateTime = TeXParserUtils.isTrue(REFORMAT_DATETIME_BOOL, getParser());

       return getElement(entry, useDatum, reformatNum, reformatDateTime);
   }

   public DataElement getElement(TeXObject entry, boolean useDatum,
     boolean autoReformatNumeric, boolean autoReformatDateTime)
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

         // does it end with a currency marker?
         TeXObject lastObj = list.lastElement();
         int idx = list.size()-1;

         while (lastObj instanceof Ignoreable && idx > 1)
         {
            idx--;
            lastObj = list.get(idx);
         }

         if (isCurrencySymbol(lastObj))
         {
            for (int i = list.size()-1; i >= idx; i--)
            {
               list.remove(i);
            }

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
                     new TeXFloatingPoint(value), lastObj, DatumType.CURRENCY);
               }
               else
               {
                  return new DataCurrencyElement(lastObj, value, original);
               }
            }
            catch (NumberFormatException | ParseException e)
            {// not numeric

               list.add(lastObj);
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

   // TODO add support for regex?
   // \datatool_set_thinspace_group_decimal_char:n etc

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

   public TeXObject formatNumber(DatumType type, Number num)
   {
      ControlSequence cs;

      if (type == DatumType.INTEGER)
      {
         cs = getParser().getControlSequence(FMT_INTEGER_VALUE);
      }
      else
      {
         cs = getParser().getControlSequence(FMT_DECIMAL_VALUE);
      }

      if (cs instanceof NumericFormatter)
      {
         return ((NumericFormatter)cs).format(num, getListener());
      }
      else
      {
         return getListener().createString(num.toString());
      }
   }

   public TeXObject formatCurrency(DatumType type, Number num, String currencyCode)
   {
      ControlSequence cs = getParser().getControlSequence(FMT_CURRENCY_VALUE);

      if (cs instanceof NumericFormatter)
      {
         return ((NumericFormatter)cs).format(num, getListener());
      }
      else
      {
         return getListener().createString(num.toString());
      }
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

   public static final String REFORMAT_NUMERIC_BOOL = "l__datatool_reformat_numeric_bool";
   public static final String AUTO_REFORMAT_TYPES_SEQ = "l__datatool_auto_reformat_types_seq";

   public static final String TMPA_VAR = "l__datatool_tmpa_tl";
   public static final String TMPB_VAR = "l__datatool_tmpb_tl";

   public static final Pattern SCIENTIFIC_PATTERN =
     Pattern.compile("[+\\-]?\\d+(\\.\\d+)?[Ee][+\\-]?\\d+");
}
