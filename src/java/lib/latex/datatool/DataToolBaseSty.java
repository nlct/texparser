/*
    Copyright (C) 2013 Nicola L.C. Talbot
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

   public void addDefinitions()
   {
      TeXParser parser = getListener().getParser();

      sortCountReg = parser.getSettings().newcount(false, "dtl@sortresult");
      NewIf.createConditional(true, parser, "ifDTLlistskipempty", true);

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
   }

   protected void preOptions() throws IOException
   {
      getListener().requirepackage("etoolbox");

      ifThenSty = (IfThenSty)getListener().requirepackage("ifthen");
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

   private IfThenSty ifThenSty;

   private Vector<TeXObject> currencySymbolList;
   private TeXObject defaultCurrency;

   private CountRegister sortCountReg;

   public static final String INDEX_OUT_OF_RANGE="datatool.index.outofrange";
}
