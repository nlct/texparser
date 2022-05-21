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
package com.dickimawbooks.texparserlib.latex.mnsymbol;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.*;

public class MnSymbolSty extends LaTeXSty
{
   public MnSymbolSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "MnSymbol", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      for (int i = 0; i < SYMBOLS.length; i++)
      {
         registerControlSequence(listener.createMathSymbol(
           (String)SYMBOLS[i][0], 
           ((Integer)SYMBOLS[i][1]).intValue()));
      }

      for (int i = 0; i < BIG_OPERATORS.length; i++)
      {
         registerControlSequence(listener.createBigOperator(
           (String)BIG_OPERATORS[i][0], 
           ((Integer)BIG_OPERATORS[i][1]).intValue()));
      }

   }

   private static final Object[][] SYMBOLS = new Object[][]
   {
      new Object[]{"lefttherefore", Integer.valueOf(0x002056)},
      new Object[]{"diamonddots", Integer.valueOf(0x002058)},
      new Object[]{"aleph", Integer.valueOf(0x002135)},
      new Object[]{"beth", Integer.valueOf(0x002136)},
      new Object[]{"gimel", Integer.valueOf(0x002137)},
      new Object[]{"daleth", Integer.valueOf(0x002138)},
      new Object[]{"leftarrow", Integer.valueOf(0x002190)},
      new Object[]{"uparrow", Integer.valueOf(0x002191)},
      new Object[]{"rightarrow", Integer.valueOf(0x002192)},
      new Object[]{"downarrow", Integer.valueOf(0x002193)},
      new Object[]{"leftrightarrow", Integer.valueOf(0x002194)},
      new Object[]{"updownarrow", Integer.valueOf(0x002195)},
      new Object[]{"nwarrow", Integer.valueOf(0x002196)},
      new Object[]{"nearrow", Integer.valueOf(0x002197)},
      new Object[]{"searrow", Integer.valueOf(0x002198)},
      new Object[]{"swarrow", Integer.valueOf(0x002199)},
      new Object[]{"nleftarrow", Integer.valueOf(0x00219A)},
      new Object[]{"nrightarrow", Integer.valueOf(0x00219B)},
      new Object[]{"leftrsquigarrow", Integer.valueOf(0x00219C)},
      new Object[]{"rightlsquigarrow", Integer.valueOf(0x00219D)},
      new Object[]{"twoheadleftarrow", Integer.valueOf(0x00219E)},
      new Object[]{"twoheaduparrow", Integer.valueOf(0x00219F)},
      new Object[]{"twoheadrightarrow", Integer.valueOf(0x0021A0)},
      new Object[]{"twoheaddownarrow", Integer.valueOf(0x0021A1)},
      new Object[]{"leftarrowtail", Integer.valueOf(0x0021A2)},
      new Object[]{"rightarrowtail", Integer.valueOf(0x0021A3)},
      new Object[]{"leftmapsto", Integer.valueOf(0x0021A4)},
      new Object[]{"upmapsto", Integer.valueOf(0x0021A5)},
      new Object[]{"rightmapsto", Integer.valueOf(0x0021A6)},
      new Object[]{"downmapsto", Integer.valueOf(0x0021A7)},
      new Object[]{"hookleftarrow", Integer.valueOf(0x0021A9)},
      new Object[]{"hookrightarrow", Integer.valueOf(0x0021AA)},
      new Object[]{"looparrowleft", Integer.valueOf(0x0021AB)},
      new Object[]{"looparrowright", Integer.valueOf(0x0021AC)},
      new Object[]{"nleftrightarrow", Integer.valueOf(0x0021AE)},
      new Object[]{"lightning", Integer.valueOf(0x0021AF)},
      new Object[]{"Lsh", Integer.valueOf(0x0021B0)},
      new Object[]{"Rsh", Integer.valueOf(0x0021B1)},
      new Object[]{"rcurvearrowleft", Integer.valueOf(0x0021B6)},
      new Object[]{"lcurvearrowright", Integer.valueOf(0x0021B7)},
      new Object[]{"rcirclearrowup", Integer.valueOf(0x0021BA)},
      new Object[]{"lcirclearrowup", Integer.valueOf(0x0021BB)},
      new Object[]{"leftharpooncw", Integer.valueOf(0x0021BC)},
      new Object[]{"leftharpoonccw", Integer.valueOf(0x0021BD)},
      new Object[]{"upharpooncw", Integer.valueOf(0x0021BE)},
      new Object[]{"upharpoonccw", Integer.valueOf(0x0021BF)},
      new Object[]{"rightharpoonccw", Integer.valueOf(0x0021C0)},
      new Object[]{"rightharpooncw", Integer.valueOf(0x0021C1)},
      new Object[]{"downharpoonccw", Integer.valueOf(0x0021C2)},
      new Object[]{"downharpooncw", Integer.valueOf(0x0021C3)},
      new Object[]{"rightleftarrows", Integer.valueOf(0x0021C4)},
      new Object[]{"updownarrows", Integer.valueOf(0x0021C5)},
      new Object[]{"leftrightarrows", Integer.valueOf(0x0021C6)},
      new Object[]{"leftleftarrows", Integer.valueOf(0x0021C7)},
      new Object[]{"upuparrows", Integer.valueOf(0x0021C8)},
      new Object[]{"rightrightarrows", Integer.valueOf(0x0021C9)},
      new Object[]{"downdownarrows", Integer.valueOf(0x0021CA)},
      new Object[]{"leftrightharpoons", Integer.valueOf(0x0021CB)},
      new Object[]{"rightleftharpoons", Integer.valueOf(0x0021CC)},
      new Object[]{"nLeftarrow", Integer.valueOf(0x0021CD)},
      new Object[]{"nLeftrightarrow", Integer.valueOf(0x0021CE)},
      new Object[]{"nRightarrow", Integer.valueOf(0x0021CF)},
      new Object[]{"Leftarrow", Integer.valueOf(0x0021D0)},
      new Object[]{"Uparrow", Integer.valueOf(0x0021D1)},
      new Object[]{"Rightarrow", Integer.valueOf(0x0021D2)},
      new Object[]{"Downarrow", Integer.valueOf(0x0021D3)},
      new Object[]{"Leftrightarrow", Integer.valueOf(0x0021D4)},
      new Object[]{"Updownarrow", Integer.valueOf(0x0021D5)},
      new Object[]{"Nwarrow", Integer.valueOf(0x0021D6)},
      new Object[]{"Nearrow", Integer.valueOf(0x0021D7)},
      new Object[]{"Searrow", Integer.valueOf(0x0021D8)},
      new Object[]{"Swarrow", Integer.valueOf(0x0021D9)},
      new Object[]{"Lleftarrow", Integer.valueOf(0x0021DA)},
      new Object[]{"Rrightarrow", Integer.valueOf(0x0021DB)},
      new Object[]{"dashedleftarrow", Integer.valueOf(0x0021E0)},
      new Object[]{"dasheduparrow", Integer.valueOf(0x0021E1)},
      new Object[]{"dashedrightarrow", Integer.valueOf(0x0021E2)},
      new Object[]{"dasheddownarrow", Integer.valueOf(0x0021E3)},
      new Object[]{"downuparrows", Integer.valueOf(0x0021F5)},
      new Object[]{"parallel", Integer.valueOf(0x002225)},
      new Object[]{"nparallel", Integer.valueOf(0x002226)},
      new Object[]{"squaredots", Integer.valueOf(0x002237)},
      new Object[]{"dotminus", Integer.valueOf(0x002238)},
      new Object[]{"doteq", Integer.valueOf(0x002250)},
      new Object[]{"Doteq", Integer.valueOf(0x002251)},
      new Object[]{"fallingdotseq", Integer.valueOf(0x002252)},
      new Object[]{"risingdotseq", Integer.valueOf(0x002253)},
      new Object[]{"maltese", Integer.valueOf(0x002720)},
      new Object[]{"perp", Integer.valueOf(0x0027C2)},
      new Object[]{"veedot", Integer.valueOf(0x0027C7)},
      new Object[]{"diagup", Integer.valueOf(0x0027CB)},
      new Object[]{"diagdown", Integer.valueOf(0x0027CD)},
      new Object[]{"diamonddot", Integer.valueOf(0x0027D0)},
      new Object[]{"wedgedot", Integer.valueOf(0x0027D1)},
      new Object[]{"leftspoon", Integer.valueOf(0x0027DC)},
      new Object[]{"rcirclearrowleft", Integer.valueOf(0x0027F2)},
      new Object[]{"lcirclearrowright", Integer.valueOf(0x0027F3)},
      new Object[]{"longleftarrow", Integer.valueOf(0x0027F5)},
      new Object[]{"longrightarrow", Integer.valueOf(0x0027F6)},
      new Object[]{"longleftrightarrow", Integer.valueOf(0x0027F7)},
      new Object[]{"Longleftarrow", Integer.valueOf(0x0027F8)},
      new Object[]{"Longrightarrow", Integer.valueOf(0x0027F9)},
      new Object[]{"Longleftrightarrow", Integer.valueOf(0x0027FA)},
      new Object[]{"longmapsto", Integer.valueOf(0x0027FC)},
      new Object[]{"dashedleftarrow", Integer.valueOf(0x00290C)},
      new Object[]{"dashedrightarrow", Integer.valueOf(0x00290D)},
      new Object[]{"nwsearrow", Integer.valueOf(0x002921)},
      new Object[]{"neswarrow", Integer.valueOf(0x002922)},
      new Object[]{"lhooknwarrow", Integer.valueOf(0x002923)},
      new Object[]{"rhooknearrow", Integer.valueOf(0x002924)},
      new Object[]{"lhooksearrow", Integer.valueOf(0x002925)},
      new Object[]{"rhookswarrow", Integer.valueOf(0x002926)},
      new Object[]{"rcurvearrowne", Integer.valueOf(0x002934)},
      new Object[]{"lcurvearrowse", Integer.valueOf(0x002935)},
      new Object[]{"lcurvearrowsw", Integer.valueOf(0x002936)},
      new Object[]{"rcurvearrowse", Integer.valueOf(0x002937)},
      new Object[]{"lcurvearrowdown", Integer.valueOf(0x002938)},
      new Object[]{"rcurvearrowdown", Integer.valueOf(0x002939)},
      new Object[]{"rcurvearrowleft", Integer.valueOf(0x00293A)},
      new Object[]{"rcurvearrowright", Integer.valueOf(0x00293B)},
      new Object[]{"leftrightharpoondownup", Integer.valueOf(0x00294A)},
      new Object[]{"leftrightharpoonupdown", Integer.valueOf(0x00294B)},
      new Object[]{"updownharpoonrightleft", Integer.valueOf(0x00294C)},
      new Object[]{"updownharpoonleftright", Integer.valueOf(0x00294D)},
      new Object[]{"updownharpoons", Integer.valueOf(0x00296E)},
      new Object[]{"downupharpoons", Integer.valueOf(0x00296F)},
      new Object[]{"bullet", Integer.valueOf(0x002981)},
      new Object[]{"angle", Integer.valueOf(0x00299F)},
      new Object[]{"vec", Integer.valueOf(0x0029B3)},
      new Object[]{"obackslash", Integer.valueOf(0x0029B8)},
      new Object[]{"ocirc", Integer.valueOf(0x0029BE)},
      new Object[]{"boxslash", Integer.valueOf(0x0029C4)},
      new Object[]{"boxbackslash", Integer.valueOf(0x0029C5)},
      new Object[]{"boxbox", Integer.valueOf(0x0029C8)},
      new Object[]{"vertbowtie", Integer.valueOf(0x0029D6)},
      new Object[]{"vertbowtie", Integer.valueOf(0x0029D6)},
      new Object[]{"filledmedlozenge", Integer.valueOf(0x0029EB)},
      new Object[]{"setminus", Integer.valueOf(0x0029F5)},
   };

   private static final Object[][] BIG_OPERATORS = new Object[][]
   {
      new Object[]{"complement", Integer.valueOf(0x002201)},
      new Object[]{"lcirclerightint", Integer.valueOf(0x002232)},
      new Object[]{"rcirclerightint", Integer.valueOf(0x002233)},
      new Object[]{"lsem", Integer.valueOf(0x0027E6)},
      new Object[]{"rsem", Integer.valueOf(0x0027E7)},
      new Object[]{"langle", Integer.valueOf(0x0027E8)},
      new Object[]{"rangle", Integer.valueOf(0x0027E9)},
      new Object[]{"llangle", Integer.valueOf(0x0027EA)},
      new Object[]{"rrangle", Integer.valueOf(0x0027EB)},
      new Object[]{"lgroup", Integer.valueOf(0x0027EE)},
      new Object[]{"rgroup", Integer.valueOf(0x0027EF)},
   };

}
