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
package com.dickimawbooks.texparserlib.latex.textcomp;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.*;

public class TextCompSty extends LaTeXSty
{
   public TextCompSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "textcomp", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      for (int i = 0; i < SYMBOLS.length; i++)
      {
         registerControlSequence(listener.createSymbol(
           (String)SYMBOLS[i][0], 
           ((Integer)SYMBOLS[i][1]).intValue()));
      }

   }

   private static final Object[][] SYMBOLS = new Object[][]
   {
      new Object[]{"textquotedbl", Integer.valueOf(0x000022)},
      new Object[]{"textquotesingle", Integer.valueOf(0x000027)},
      new Object[]{"textasciigrave", Integer.valueOf(0x000060)},
      new Object[]{"textcent", Integer.valueOf(0x0000A2)},
      new Object[]{"textcurrency", Integer.valueOf(0x0000A4)},
      new Object[]{"textyen", Integer.valueOf(0x0000A5)},
      new Object[]{"textbrokenbar", Integer.valueOf(0x0000A6)},
      new Object[]{"textasciidieresis", Integer.valueOf(0x0000A8)},
      new Object[]{"textordfeminine", Integer.valueOf(0x0000AA)},
      new Object[]{"textlnot", Integer.valueOf(0x0000AC)},
      new Object[]{"textasciimacron", Integer.valueOf(0x0000AF)},
      new Object[]{"textdegree", Integer.valueOf(0x0000B0)},
      new Object[]{"textpm", Integer.valueOf(0x0000B1)},
      new Object[]{"texttwosuperior", Integer.valueOf(0x0000B2)},
      new Object[]{"textthreesuperior", Integer.valueOf(0x0000B3)},
      new Object[]{"textasciiacute", Integer.valueOf(0x0000B4)},
      new Object[]{"textmu", Integer.valueOf(0x0000B5)},
      new Object[]{"micro", Integer.valueOf(0x0000B5)},
      new Object[]{"textperiodcentered", Integer.valueOf(0x0000B7)},
      new Object[]{"textonesuperior", Integer.valueOf(0x0000B9)},
      new Object[]{"textordmasculine", Integer.valueOf(0x0000BA)},
      new Object[]{"textonequarter", Integer.valueOf(0x0000BC)},
      new Object[]{"textonehalf", Integer.valueOf(0x0000BD)},
      new Object[]{"textthreequarters", Integer.valueOf(0x0000BE)},
      new Object[]{"texttimes", Integer.valueOf(0x0000D7)},
      new Object[]{"textdiv", Integer.valueOf(0x0000F7)},
      new Object[]{"textthreequartersemdash", Integer.valueOf(0x002012)},
      new Object[]{"textbullet", Integer.valueOf(0x002022)},
      new Object[]{"textperiodcentered", Integer.valueOf(0x002027)},
      new Object[]{"textperthousand", Integer.valueOf(0x002030)},
      new Object[]{"textpertenthousand", Integer.valueOf(0x002031)},
      new Object[]{"textreferencemark", Integer.valueOf(0x00203B)},
      new Object[]{"textinterrobang", Integer.valueOf(0x00203D)},
      new Object[]{"textdiscount", Integer.valueOf(0x002052)},
      new Object[]{"textlira", Integer.valueOf(0x0020A4)},
      new Object[]{"textnaira", Integer.valueOf(0x0020A6)},
      new Object[]{"textwon", Integer.valueOf(0x0020A9)},
      new Object[]{"textdong", Integer.valueOf(0x0020AB)},
      new Object[]{"texteuro", Integer.valueOf(0x0020AC)},
      new Object[]{"textpeso", Integer.valueOf(0x0020B1)},
      new Object[]{"textguarani", Integer.valueOf(0x0020B2)},
      new Object[]{"textcolonmonetary", Integer.valueOf(0x0020B5)},
      new Object[]{"textcelsius", Integer.valueOf(0x002103)},
      new Object[]{"textnumero", Integer.valueOf(0x002116)},
      new Object[]{"textcircledP", Integer.valueOf(0x002117)},
      new Object[]{"textrecipe", Integer.valueOf(0x00211E)},
      new Object[]{"textservicemark", Integer.valueOf(0x002120)},
      new Object[]{"texttrademark", Integer.valueOf(0x002122)},
      new Object[]{"textestimated", Integer.valueOf(0x00212E)},
      new Object[]{"textleftarrow", Integer.valueOf(0x002190)},
      new Object[]{"textuparrow", Integer.valueOf(0x002191)},
      new Object[]{"textrightarrow", Integer.valueOf(0x002192)},
      new Object[]{"textdownarrow", Integer.valueOf(0x002193)},
      new Object[]{"textsurd", Integer.valueOf(0x00221A)},
      new Object[]{"textborn", Integer.valueOf(0x002605)},
      new Object[]{"textmarried", Integer.valueOf(0x0026AD)},
      new Object[]{"textdivorced", Integer.valueOf(0x0026AE)},
      new Object[]{"textlbrackdbl", Integer.valueOf(0x0027E6)},
      new Object[]{"textrbrackdbl", Integer.valueOf(0x0027E7)},
      new Object[]{"textlangle", Integer.valueOf(0x0027E8)},
      new Object[]{"textrangle", Integer.valueOf(0x0027E9)},
   };

}
