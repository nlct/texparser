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

   public DataElement getElement(TeXObject entry)
     throws IOException
   {
      TeXParser parser = getListener().getParser();

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
            DataElement elem = DatumMarker.popDataElement(parser, list);

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
               return new DataCurrencyElement(first, 
                Double.parseDouble(list.toString(parser).trim()));
            }
            catch (NumberFormatException e)
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
         return new DataIntElement(Integer.parseInt(str));
      }
      catch (NumberFormatException e)
      {
      }

      // is it a real number?

      try
      {
         return new DataRealElement(Double.parseDouble(str));
      }
      catch (NumberFormatException e)
      {
      }

      if (entry instanceof TeXObjectList)
      {
         return new DataStringElement((TeXObjectList)entry);
      }

      DataStringElement elem = new DataStringElement();
      elem.add(entry);
      return elem;
   }

   private IfThenSty ifThenSty;

   private Vector<TeXObject> currencySymbolList;
   private TeXObject defaultCurrency;

   private CountRegister sortCountReg;

   private DataToolSty datatoolSty;

   public static final String INDEX_OUT_OF_RANGE="datatool.index.outofrange";
}
