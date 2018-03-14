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
package com.dickimawbooks.texparserlib.latex.tipa;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.fontenc.FontEncSty;

public class TipaSty extends LaTeXSty
{
   public TipaSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "tipa", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      TipaEncoding declaration = new TipaEncoding();
      registerControlSequence(declaration);
      registerControlSequence(new TextBlockCommand("textipa", declaration));

      LaTeXParserListener listener = getListener();

      for (int i = 0; i < SYMBOLS.length; i++)
      {
         registerControlSequence(listener.createSymbol(
           (String)SYMBOLS[i][0], 
           ((Integer)SYMBOLS[i][1]).intValue(),
           t3Encoding));
      }

   }

   protected void preOptions() throws IOException
   {
      LaTeXParserListener listener = getListener();

      fontEncSty = listener.getFontEncSty();

      if (fontEncSty == null)
      {
         fontEncSty = (FontEncSty)listener.requirepackage("fontenc");
      }

      t3Encoding = new T3Encoding();
      fontEncSty.registerEncoding(t3Encoding);
   }

   private static final Object[][] SYMBOLS = new Object[][]
   {
      // 0-9
      new Object[]{"textbaru", Integer.valueOf('0')},
      new Object[]{"textbari", Integer.valueOf('1')},
      new Object[]{"textturnv", Integer.valueOf('2')},
      new Object[]{"textrevepsilon", Integer.valueOf('3')},
      new Object[]{"textturnh", Integer.valueOf('4')},
      new Object[]{"textturna", Integer.valueOf('5')},
      new Object[]{"textturnscripta", Integer.valueOf('6')},
      new Object[]{"textramshorns", Integer.valueOf('7')},
      new Object[]{"textbaro", Integer.valueOf('8')},
      new Object[]{"textreve", Integer.valueOf('9')},
      // @
      new Object[]{"textschwa", Integer.valueOf('@')},
      // A-Z (doesn't include D: \dh and N: \ng)
      new Object[]{"textscripta", Integer.valueOf('A')},
      new Object[]{"textbeta", Integer.valueOf('B')},
      new Object[]{"textctc", Integer.valueOf('C')},
      new Object[]{"textepsilon", Integer.valueOf('E')},
      new Object[]{"textphi", Integer.valueOf('F')},
      new Object[]{"textgamma", Integer.valueOf('G')},
      new Object[]{"texthth", Integer.valueOf('H')},
      new Object[]{"textsci", Integer.valueOf('I')},
      new Object[]{"textctj", Integer.valueOf('J')},
      new Object[]{"textinvscr", Integer.valueOf('K')},
      new Object[]{"textturny", Integer.valueOf('L')},
      new Object[]{"texttailm", Integer.valueOf('M')},
      new Object[]{"textopeno", Integer.valueOf('O')},
      new Object[]{"textglotstop", Integer.valueOf('P')},
      new Object[]{"textrevglotstop", Integer.valueOf('Q')},
      new Object[]{"textfishhookr", Integer.valueOf('R')},
      new Object[]{"textesh", Integer.valueOf('S')},
      new Object[]{"texttheta", Integer.valueOf('T')},
      new Object[]{"textupsilon", Integer.valueOf('U')},
      new Object[]{"textscriptv", Integer.valueOf('V')},
      new Object[]{"textturnm", Integer.valueOf('W')},
      new Object[]{"textchi", Integer.valueOf('X')},
      new Object[]{"textscy", Integer.valueOf('Y')},
      new Object[]{"textyogh", Integer.valueOf('Z')},

      new Object[]{"textscg", Integer.valueOf('g')},

      new Object[]{"textprimstress", Integer.valueOf('"')},
      new Object[]{"textlengthmark", Integer.valueOf(':')},
      new Object[]{"texthalflength", Integer.valueOf(';')},
      new Object[]{"textpipe", Integer.valueOf('|')},

      // 224--255
      new Object[]{"textscb", Integer.valueOf(224)},
      new Object[]{"texthtb", Integer.valueOf(225)},
      new Object[]{"texthtd", Integer.valueOf(226)},
      new Object[]{"textrtaild", Integer.valueOf(227)},
      new Object[]{"texthtg", Integer.valueOf(228)},
      new Object[]{"textscg", Integer.valueOf(229)},
      // 230 -> \ae and 231 -> \c{c}
      new Object[]{"textcrh", Integer.valueOf(232)},
      new Object[]{"textbardotlessj", Integer.valueOf(233)},
      new Object[]{"texthtbardotlessj", Integer.valueOf(234)},
      new Object[]{"textltilde", Integer.valueOf(235)},
      new Object[]{"textbeltl", Integer.valueOf(236)},
      new Object[]{"textrtaill", Integer.valueOf(237)},
      new Object[]{"textturnmrleg", Integer.valueOf(238)},
      new Object[]{"textrtailn", Integer.valueOf(239)},
      new Object[]{"textscn", Integer.valueOf(240)},
      new Object[]{"textltailn", Integer.valueOf(241)},
      new Object[]{"textbullseye", Integer.valueOf(242)},
      new Object[]{"textrtailr", Integer.valueOf(243)},
      new Object[]{"textturnr", Integer.valueOf(244)},
      new Object[]{"textturnrrtail", Integer.valueOf(245)},
      new Object[]{"textscr", Integer.valueOf(246)},
      // 247 -> \oe and 248 -> \o
      new Object[]{"textrtails", Integer.valueOf(249)},
      new Object[]{"textrtailt", Integer.valueOf(250)},
      new Object[]{"textturnw", Integer.valueOf(251)},
      new Object[]{"textrtailz", Integer.valueOf(252)},
      new Object[]{"textctz", Integer.valueOf(253)},
      new Object[]{"textthorn", Integer.valueOf(254)},
      new Object[]{"texthvlig", Integer.valueOf(255)},
      // 192--223
      new Object[]{"textsca", Integer.valueOf(192)},
      new Object[]{"texthtc", Integer.valueOf(193)},
      new Object[]{"textstretchc", Integer.valueOf(194)},
      new Object[]{"textdyoghlig", Integer.valueOf(195)},
      new Object[]{"textrhookschwa", Integer.valueOf(196)},
      new Object[]{"textcloseepsilon", Integer.valueOf(197)},
      new Object[]{"textcloserevepsilon", Integer.valueOf(198)},
      new Object[]{"textrhookrevepsilon", Integer.valueOf(199)},
      new Object[]{"textbabygamma", Integer.valueOf(200)},
      new Object[]{"texthtscg", Integer.valueOf(201)},
      new Object[]{"texththeng", Integer.valueOf(202)},
      new Object[]{"textsch", Integer.valueOf(203)},
      new Object[]{"textiota", Integer.valueOf(204)},
      new Object[]{"textObardotlessj", Integer.valueOf(205)},
      new Object[]{"texthtk", Integer.valueOf(206)},
      new Object[]{"textscl", Integer.valueOf(207)},
      new Object[]{"textlyoghlig", Integer.valueOf(208)},
      new Object[]{"textcloseomega", Integer.valueOf(209)},
      new Object[]{"texthtp", Integer.valueOf(210)},
      new Object[]{"texthtq", Integer.valueOf(211)},
      new Object[]{"textlonglegr", Integer.valueOf(212)},
      new Object[]{"textturnlonglegr", Integer.valueOf(213)},
      new Object[]{"texthtt", Integer.valueOf(214)},
      new Object[]{"textscoelig", Integer.valueOf(215)},
      new Object[]{"textturnt", Integer.valueOf(216)},
      new Object[]{"textteshlig", Integer.valueOf(217)},
      new Object[]{"textscu", Integer.valueOf(218)},
      new Object[]{"textinvglotstop", Integer.valueOf(219)},
      new Object[]{"textbarglotstop", Integer.valueOf(220)},
      new Object[]{"textbarrevglotstop", Integer.valueOf(221)},
      new Object[]{"textcommatailz", Integer.valueOf(222)},
      new Object[]{"textwynn", Integer.valueOf(223)},
      // 160 - 191
      new Object[]{"textcrb", Integer.valueOf(160)},
      new Object[]{"textcrd", Integer.valueOf(161)},
      new Object[]{"textctd", Integer.valueOf(162)},
      new Object[]{"texthtrtaild", Integer.valueOf(163)},
      new Object[]{"textsce", Integer.valueOf(164)},
      new Object[]{"textg", Integer.valueOf(165)},
      new Object[]{"textlhtlongi", Integer.valueOf(166)},
      new Object[]{"textvibyi", Integer.valueOf(167)},
      new Object[]{"textscj", Integer.valueOf(168)},
      new Object[]{"textturnk", Integer.valueOf(169)},
      new Object[]{"textbarl", Integer.valueOf(170)},
      new Object[]{"textlambda", Integer.valueOf(171)},
      new Object[]{"textcrlambda", Integer.valueOf(172)},
      new Object[]{"textOlyoghlig", Integer.valueOf(173)},
      new Object[]{"textctn", Integer.valueOf(174)},
      new Object[]{"textturncelig", Integer.valueOf(175)},
      new Object[]{"textomega", Integer.valueOf(176)},
      new Object[]{"textscomega", Integer.valueOf(177)},
      new Object[]{"textctesh", Integer.valueOf(178)},
      new Object[]{"textlhookt", Integer.valueOf(179)},
      new Object[]{"textctt", Integer.valueOf(180)},
      new Object[]{"texttslig", Integer.valueOf(181)},
      new Object[]{"textlhtlongy", Integer.valueOf(182)},
      new Object[]{"textvibyy", Integer.valueOf(183)},
      new Object[]{"textctyogh", Integer.valueOf(184)},
      new Object[]{"textrevyogh", Integer.valueOf(185)},
      new Object[]{"textsoftsign", Integer.valueOf(186)},
      new Object[]{"texthardsign", Integer.valueOf(187)},
      new Object[]{"textraiseglotstop", Integer.valueOf(188)},
      new Object[]{"textlptr", Integer.valueOf(189)},
      new Object[]{"textrptr", Integer.valueOf(190)},
      new Object[]{"texttoneletterstem", Integer.valueOf(191)},
      // 32, 35, 36, 37
      new Object[]{"textceltpal", Integer.valueOf(32)},
      // can't determine the closest Unicode match for these
      //new Object[]{"texthooktop", Integer.valueOf(35)},
      //new Object[]{"textrthook", Integer.valueOf(36)},
      //new Object[]{"textpalhook", Integer.valueOf(37)},
      // 92, 94, 95
      new Object[]{"textrevapostrophe", Integer.valueOf(92)},
      new Object[]{"textcorner", Integer.valueOf(94)},
      new Object[]{"textopencorner", Integer.valueOf(95)},
      // 123, 125, 126, 127
      new Object[]{"textdoublepipe", Integer.valueOf(123)},
      new Object[]{"textdoublebarpipe", Integer.valueOf(125)},
      new Object[]{"textrhoticity", Integer.valueOf(126)},
      new Object[]{"textsecstress", Integer.valueOf(127)},
      // 146 - 151
      new Object[]{"textvertline", Integer.valueOf(146)},
      new Object[]{"textdoublevertline", Integer.valueOf(147)},
      new Object[]{"textdownstep", Integer.valueOf(148)},
      new Object[]{"textupstep", Integer.valueOf(149)},
      new Object[]{"textglobrise", Integer.valueOf(150)},
      new Object[]{"textglobfall", Integer.valueOf(151)},
   };

   private FontEncSty fontEncSty;
   private T3Encoding t3Encoding;
}
