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
      new Object[]{"textquotedbl", new Integer(0x000022)},
      new Object[]{"textquotesingle", new Integer(0x000027)},
      new Object[]{"textasciigrave", new Integer(0x000060)},
      new Object[]{"textcent", new Integer(0x0000A2)},
      new Object[]{"textcurrency", new Integer(0x0000A4)},
      new Object[]{"textyen", new Integer(0x0000A5)},
      new Object[]{"textbrokenbar", new Integer(0x0000A6)},
      new Object[]{"textasciidieresis", new Integer(0x0000A8)},
      new Object[]{"textordfeminine", new Integer(0x0000AA)},
      new Object[]{"textlnot", new Integer(0x0000AC)},
      new Object[]{"textasciimacron", new Integer(0x0000AF)},
      new Object[]{"textdegree", new Integer(0x0000B0)},
      new Object[]{"textpm", new Integer(0x0000B1)},
      new Object[]{"texttwosuperior", new Integer(0x0000B2)},
      new Object[]{"textthreesuperior", new Integer(0x0000B3)},
      new Object[]{"textasciiacute", new Integer(0x0000B4)},
      new Object[]{"textmu", new Integer(0x0000B5)},
      new Object[]{"micro", new Integer(0x0000B5)},
      new Object[]{"textperiodcentered", new Integer(0x0000B7)},
      new Object[]{"textonesuperior", new Integer(0x0000B9)},
      new Object[]{"textordmasculine", new Integer(0x0000BA)},
      new Object[]{"textonequarter", new Integer(0x0000BC)},
      new Object[]{"textonehalf", new Integer(0x0000BD)},
      new Object[]{"textthreequarters", new Integer(0x0000BE)},
      new Object[]{"texttimes", new Integer(0x0000D7)},
      new Object[]{"textdiv", new Integer(0x0000F7)},
      new Object[]{"textthreequartersemdash", new Integer(0x002012)},
      new Object[]{"textbullet", new Integer(0x002022)},
      new Object[]{"textperiodcentered", new Integer(0x002027)},
      new Object[]{"textperthousand", new Integer(0x002030)},
      new Object[]{"textpertenthousand", new Integer(0x002031)},
      new Object[]{"textreferencemark", new Integer(0x00203B)},
      new Object[]{"textinterrobang", new Integer(0x00203D)},
      new Object[]{"textdiscount", new Integer(0x002052)},
      new Object[]{"textlira", new Integer(0x0020A4)},
      new Object[]{"textnaira", new Integer(0x0020A6)},
      new Object[]{"textwon", new Integer(0x0020A9)},
      new Object[]{"textdong", new Integer(0x0020AB)},
      new Object[]{"texteuro", new Integer(0x0020AC)},
      new Object[]{"textpeso", new Integer(0x0020B1)},
      new Object[]{"textguarani", new Integer(0x0020B2)},
      new Object[]{"textcolonmonetary", new Integer(0x0020B5)},
      new Object[]{"textcelsius", new Integer(0x002103)},
      new Object[]{"textdegree", new Integer(0x002109)},
      new Object[]{"textnumero", new Integer(0x002116)},
      new Object[]{"textcircledP", new Integer(0x002117)},
      new Object[]{"textrecipe", new Integer(0x00211E)},
      new Object[]{"textservicemark", new Integer(0x002120)},
      new Object[]{"texttrademark", new Integer(0x002122)},
      new Object[]{"textestimated", new Integer(0x00212E)},
      new Object[]{"textleftarrow", new Integer(0x002190)},
      new Object[]{"textuparrow", new Integer(0x002191)},
      new Object[]{"textrightarrow", new Integer(0x002192)},
      new Object[]{"textdownarrow", new Integer(0x002193)},
      new Object[]{"textsurd", new Integer(0x00221A)},
      new Object[]{"textborn", new Integer(0x002605)},
      new Object[]{"textmarried", new Integer(0x0026AD)},
      new Object[]{"textdivorced", new Integer(0x0026AE)},
      new Object[]{"textlbrackdbl", new Integer(0x0027E6)},
      new Object[]{"textrbrackdbl", new Integer(0x0027E7)},
      new Object[]{"textlangle", new Integer(0x0027E8)},
      new Object[]{"textrangle", new Integer(0x0027E9)},
   };

}
