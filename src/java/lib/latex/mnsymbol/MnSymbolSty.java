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
      new Object[]{"lefttherefore", new Integer(0x002056)},
      new Object[]{"diamonddots", new Integer(0x002058)},
      new Object[]{"aleph", new Integer(0x002135)},
      new Object[]{"beth", new Integer(0x002136)},
      new Object[]{"gimel", new Integer(0x002137)},
      new Object[]{"daleth", new Integer(0x002138)},
      new Object[]{"leftarrow", new Integer(0x002190)},
      new Object[]{"uparrow", new Integer(0x002191)},
      new Object[]{"rightarrow", new Integer(0x002192)},
      new Object[]{"downarrow", new Integer(0x002193)},
      new Object[]{"leftrightarrow", new Integer(0x002194)},
      new Object[]{"updownarrow", new Integer(0x002195)},
      new Object[]{"nwarrow", new Integer(0x002196)},
      new Object[]{"nearrow", new Integer(0x002197)},
      new Object[]{"searrow", new Integer(0x002198)},
      new Object[]{"swarrow", new Integer(0x002199)},
      new Object[]{"nleftarrow", new Integer(0x00219A)},
      new Object[]{"nrightarrow", new Integer(0x00219B)},
      new Object[]{"leftrsquigarrow", new Integer(0x00219C)},
      new Object[]{"rightlsquigarrow", new Integer(0x00219D)},
      new Object[]{"twoheadleftarrow", new Integer(0x00219E)},
      new Object[]{"twoheaduparrow", new Integer(0x00219F)},
      new Object[]{"twoheadrightarrow", new Integer(0x0021A0)},
      new Object[]{"twoheaddownarrow", new Integer(0x0021A1)},
      new Object[]{"leftarrowtail", new Integer(0x0021A2)},
      new Object[]{"rightarrowtail", new Integer(0x0021A3)},
      new Object[]{"leftmapsto", new Integer(0x0021A4)},
      new Object[]{"upmapsto", new Integer(0x0021A5)},
      new Object[]{"rightmapsto", new Integer(0x0021A6)},
      new Object[]{"downmapsto", new Integer(0x0021A7)},
      new Object[]{"hookleftarrow", new Integer(0x0021A9)},
      new Object[]{"hookrightarrow", new Integer(0x0021AA)},
      new Object[]{"looparrowleft", new Integer(0x0021AB)},
      new Object[]{"looparrowright", new Integer(0x0021AC)},
      new Object[]{"nleftrightarrow", new Integer(0x0021AE)},
      new Object[]{"lightning", new Integer(0x0021AF)},
      new Object[]{"Lsh", new Integer(0x0021B0)},
      new Object[]{"Rsh", new Integer(0x0021B1)},
      new Object[]{"rcurvearrowleft", new Integer(0x0021B6)},
      new Object[]{"lcurvearrowright", new Integer(0x0021B7)},
      new Object[]{"rcirclearrowup", new Integer(0x0021BA)},
      new Object[]{"lcirclearrowup", new Integer(0x0021BB)},
      new Object[]{"leftharpooncw", new Integer(0x0021BC)},
      new Object[]{"leftharpoonccw", new Integer(0x0021BD)},
      new Object[]{"upharpooncw", new Integer(0x0021BE)},
      new Object[]{"upharpoonccw", new Integer(0x0021BF)},
      new Object[]{"rightharpoonccw", new Integer(0x0021C0)},
      new Object[]{"rightharpooncw", new Integer(0x0021C1)},
      new Object[]{"downharpoonccw", new Integer(0x0021C2)},
      new Object[]{"downharpooncw", new Integer(0x0021C3)},
      new Object[]{"rightleftarrows", new Integer(0x0021C4)},
      new Object[]{"updownarrows", new Integer(0x0021C5)},
      new Object[]{"leftrightarrows", new Integer(0x0021C6)},
      new Object[]{"leftleftarrows", new Integer(0x0021C7)},
      new Object[]{"upuparrows", new Integer(0x0021C8)},
      new Object[]{"rightrightarrows", new Integer(0x0021C9)},
      new Object[]{"downdownarrows", new Integer(0x0021CA)},
      new Object[]{"leftrightharpoons", new Integer(0x0021CB)},
      new Object[]{"rightleftharpoons", new Integer(0x0021CC)},
      new Object[]{"nLeftarrow", new Integer(0x0021CD)},
      new Object[]{"nLeftrightarrow", new Integer(0x0021CE)},
      new Object[]{"nRightarrow", new Integer(0x0021CF)},
      new Object[]{"Leftarrow", new Integer(0x0021D0)},
      new Object[]{"Uparrow", new Integer(0x0021D1)},
      new Object[]{"Rightarrow", new Integer(0x0021D2)},
      new Object[]{"Downarrow", new Integer(0x0021D3)},
      new Object[]{"Leftrightarrow", new Integer(0x0021D4)},
      new Object[]{"Updownarrow", new Integer(0x0021D5)},
      new Object[]{"Nwarrow", new Integer(0x0021D6)},
      new Object[]{"Nearrow", new Integer(0x0021D7)},
      new Object[]{"Searrow", new Integer(0x0021D8)},
      new Object[]{"Swarrow", new Integer(0x0021D9)},
      new Object[]{"Lleftarrow", new Integer(0x0021DA)},
      new Object[]{"Rrightarrow", new Integer(0x0021DB)},
      new Object[]{"dashedleftarrow", new Integer(0x0021E0)},
      new Object[]{"dasheduparrow", new Integer(0x0021E1)},
      new Object[]{"dashedrightarrow", new Integer(0x0021E2)},
      new Object[]{"dasheddownarrow", new Integer(0x0021E3)},
      new Object[]{"downuparrows", new Integer(0x0021F5)},
      new Object[]{"parallel", new Integer(0x002225)},
      new Object[]{"nparallel", new Integer(0x002226)},
      new Object[]{"squaredots", new Integer(0x002237)},
      new Object[]{"dotminus", new Integer(0x002238)},
      new Object[]{"doteq", new Integer(0x002250)},
      new Object[]{"Doteq", new Integer(0x002251)},
      new Object[]{"fallingdotseq", new Integer(0x002252)},
      new Object[]{"risingdotseq", new Integer(0x002253)},
      new Object[]{"maltese", new Integer(0x002720)},
      new Object[]{"perp", new Integer(0x0027C2)},
      new Object[]{"veedot", new Integer(0x0027C7)},
      new Object[]{"diagup", new Integer(0x0027CB)},
      new Object[]{"diagdown", new Integer(0x0027CD)},
      new Object[]{"diamonddot", new Integer(0x0027D0)},
      new Object[]{"wedgedot", new Integer(0x0027D1)},
      new Object[]{"leftspoon", new Integer(0x0027DC)},
      new Object[]{"rcirclearrowleft", new Integer(0x0027F2)},
      new Object[]{"lcirclearrowright", new Integer(0x0027F3)},
      new Object[]{"longleftarrow", new Integer(0x0027F5)},
      new Object[]{"longrightarrow", new Integer(0x0027F6)},
      new Object[]{"longleftrightarrow", new Integer(0x0027F7)},
      new Object[]{"Longleftarrow", new Integer(0x0027F8)},
      new Object[]{"Longrightarrow", new Integer(0x0027F9)},
      new Object[]{"Longleftrightarrow", new Integer(0x0027FA)},
      new Object[]{"longmapsto", new Integer(0x0027FC)},
      new Object[]{"dashedleftarrow", new Integer(0x00290C)},
      new Object[]{"dashedrightarrow", new Integer(0x00290D)},
      new Object[]{"nwsearrow", new Integer(0x002921)},
      new Object[]{"neswarrow", new Integer(0x002922)},
      new Object[]{"lhooknwarrow", new Integer(0x002923)},
      new Object[]{"rhooknearrow", new Integer(0x002924)},
      new Object[]{"lhooksearrow", new Integer(0x002925)},
      new Object[]{"rhookswarrow", new Integer(0x002926)},
      new Object[]{"rcurvearrowne", new Integer(0x002934)},
      new Object[]{"lcurvearrowse", new Integer(0x002935)},
      new Object[]{"lcurvearrowsw", new Integer(0x002936)},
      new Object[]{"rcurvearrowse", new Integer(0x002937)},
      new Object[]{"lcurvearrowdown", new Integer(0x002938)},
      new Object[]{"rcurvearrowdown", new Integer(0x002939)},
      new Object[]{"rcurvearrowleft", new Integer(0x00293A)},
      new Object[]{"rcurvearrowright", new Integer(0x00293B)},
      new Object[]{"leftrightharpoondownup", new Integer(0x00294A)},
      new Object[]{"leftrightharpoonupdown", new Integer(0x00294B)},
      new Object[]{"updownharpoonrightleft", new Integer(0x00294C)},
      new Object[]{"updownharpoonleftright", new Integer(0x00294D)},
      new Object[]{"updownharpoons", new Integer(0x00296E)},
      new Object[]{"downupharpoons", new Integer(0x00296F)},
      new Object[]{"bullet", new Integer(0x002981)},
      new Object[]{"angle", new Integer(0x00299F)},
      new Object[]{"vec", new Integer(0x0029B3)},
      new Object[]{"obackslash", new Integer(0x0029B8)},
      new Object[]{"ocirc", new Integer(0x0029BE)},
      new Object[]{"boxslash", new Integer(0x0029C4)},
      new Object[]{"boxbackslash", new Integer(0x0029C5)},
      new Object[]{"boxbox", new Integer(0x0029C8)},
      new Object[]{"vertbowtie", new Integer(0x0029D6)},
      new Object[]{"vertbowtie", new Integer(0x0029D6)},
      new Object[]{"filledmedlozenge", new Integer(0x0029EB)},
      new Object[]{"setminus", new Integer(0x0029F5)},
   };

   private static final Object[][] BIG_OPERATORS = new Object[][]
   {
      new Object[]{"complement", new Integer(0x002201)},
      new Object[]{"lcirclerightint", new Integer(0x002232)},
      new Object[]{"rcirclerightint", new Integer(0x002233)},
      new Object[]{"lsem", new Integer(0x0027E6)},
      new Object[]{"rsem", new Integer(0x0027E7)},
      new Object[]{"langle", new Integer(0x0027E8)},
      new Object[]{"rangle", new Integer(0x0027E9)},
      new Object[]{"llangle", new Integer(0x0027EA)},
      new Object[]{"rrangle", new Integer(0x0027EB)},
      new Object[]{"lgroup", new Integer(0x0027EE)},
      new Object[]{"rgroup", new Integer(0x0027EF)},
   };

}
