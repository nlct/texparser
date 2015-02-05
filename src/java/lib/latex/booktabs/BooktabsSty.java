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
package com.dickimawbooks.texparserlib.latex.booktabs;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class BooktabsSty extends LaTeXSty
{
   public BooktabsSty(String name, LaTeXParserListener listener)
   {
      super(name, listener);
   }

   public void addDefinitions()
   {
      registerControlSequence(new BKTrule("toprule", BKTrule.TOP));
      registerControlSequence(new BKTrule("midrule", BKTrule.MIDDLE));
      registerControlSequence(new BKTrule("bottomrule", BKTrule.BOTTOM));

      DimenRegister reg;

      //TODO implement em values

      reg = registerNewLength("heavyrulewidth");
      reg.setValue(0.80002f, FixedUnit.PT); // should be 0.08em

      reg = registerNewLength("lightrulewidth");
      reg.setValue(0.50003f, FixedUnit.PT); // should be 0.05em

      reg = registerNewLength("cmidrulewidth");
      reg.setValue(0.29999f, FixedUnit.PT); // should be 0.03em
   }

   public void processOption(String option)
    throws IOException
   {
   }

   protected void preOptions()
     throws IOException
   {
   }
}
