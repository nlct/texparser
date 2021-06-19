/*
    Copyright (C) 2013-2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;
import java.util.Hashtable;

import com.dickimawbooks.texparserlib.*;

public class Symbol extends ControlSequence implements Expandable,CaseChangeable
{
   public Symbol(String name, int codePoint)
   {
      super(name);
      this.codePoint = codePoint;
   }

   public Object clone()
   {
      return new Symbol(getName(), codePoint);
   }

   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (other == null || !(other instanceof Symbol)) return false;

      return codePoint == ((Symbol)other).codePoint;
   }

   public TeXObject toLowerCase(TeXParser parser)
   {
      if (!(Character.isUpperCase(codePoint)
         || Character.isTitleCase(codePoint)))
      {
         return this;
      }

      return parser.getListener().getOther(Character.toLowerCase(codePoint));
   }

   public TeXObject toUpperCase(TeXParser parser)
   {
      if (!Character.isLowerCase(codePoint))
      {
         return this;
      }

      return parser.getListener().getOther(Character.toUpperCase(codePoint));
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {

      TeXObjectList list = new TeXObjectList();

      list.add(parser.getListener().getOther(codePoint));

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public void write(TeXParser parser)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXSettings settings = parser.getSettings();

      int c = settings.getCharCode(codePoint);

      listener.getWriteable().writeCodePoint(c == -1 ? codePoint : c);
   }

   public void process(TeXParser parser) throws IOException
   {
      write(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      process(parser);
   }

   public int getCharCode()
   {
      return codePoint;
   }

   public void setCharCode(int charCode)
   {
      codePoint = charCode;
   }

   public static void addCommands(TeXParser parser,
     TeXParserListener listener)
   {
      for (int i = 0; i < GENERIC_SYMBOLS.length; i++)
      {
         String name = GENERIC_SYMBOLS[i][0].toString();
         int code = ((Integer)GENERIC_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createSymbol(name, code));
      }

      for (int i = 0; i < TEXT_SYMBOLS.length; i++)
      {
         String name = TEXT_SYMBOLS[i][0].toString();
         int code = ((Integer)TEXT_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createSymbol(name, code));
      }

      for (int i = 0; i < GREEK_SYMBOLS.length; i++)
      {
         String name = GREEK_SYMBOLS[i][0].toString();
         int code = ((Integer)GREEK_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createGreekSymbol(name, code));
      }

      for (int i = 0; i < MATH_SYMBOLS.length; i++)
      {
         String name = MATH_SYMBOLS[i][0].toString();
         int code = ((Integer)MATH_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createMathSymbol(name, code));
      }

      for (int i = 0; i < BINARY_MATH_SYMBOLS.length; i++)
      {
         String name = BINARY_MATH_SYMBOLS[i][0].toString();
         int code = ((Integer)BINARY_MATH_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createBinarySymbol(name, code));
      }

      for (int i = 0; i < DELIMITER_SYMBOLS.length; i++)
      {
         String name = DELIMITER_SYMBOLS[i][0].toString();
         int code = ((Integer)DELIMITER_SYMBOLS[i][1]).intValue();

         parser.putControlSequence(listener.createDelimiterSymbol(name, code));
      }

      for (int i = 0; i < BIG_MATH_SYMBOLS.length; i++)
      {
         String name = BIG_MATH_SYMBOLS[i][0].toString();
         int code1 = ((Integer)BIG_MATH_SYMBOLS[i][1]).intValue();
         int code2 = ((Integer)BIG_MATH_SYMBOLS[i][2]).intValue();

         parser.putControlSequence(listener.createBigOperator(name, code1, code2));
      }
   }

   public static final Object[][] GENERIC_SYMBOLS =
   {
      new Object[]{"{", Integer.valueOf((int)'{')},
      new Object[]{"}", Integer.valueOf((int)'}')},
      new Object[]{"%", Integer.valueOf((int)'%')},
      new Object[]{"_", Integer.valueOf((int)'_')},
      new Object[]{"&", Integer.valueOf((int)'&')},
      new Object[]{"$", Integer.valueOf((int)'$')},
      new Object[]{"#", Integer.valueOf((int)'#')},
      new Object[]{"lbrack", Integer.valueOf((int)'[')},
      new Object[]{"rbrack", Integer.valueOf((int)']')},
   };

   public static final Object[][] TEXT_SYMBOLS =
   {
      new Object[]{"P", Integer.valueOf(0x00B6)},
      new Object[]{"S", Integer.valueOf(0x00A7)},
      new Object[]{"O", Integer.valueOf(0x00D8)},
      new Object[]{"o", Integer.valueOf(0x00F8)},
      new Object[]{"copyright", Integer.valueOf(0x00A9)},
      new Object[]{"ldots", Integer.valueOf(0x2026)},
      new Object[]{"pounds", Integer.valueOf(0x00A3)},
      new Object[]{"dag", Integer.valueOf(0x2020)},
      new Object[]{"ddag", Integer.valueOf(0x2021)},
      new Object[]{"slash", Integer.valueOf((int)'/')},
      new Object[]{"textendash", Integer.valueOf(0x2013)},
      new Object[]{"textemdash", Integer.valueOf(0x2014)},
      new Object[]{"textasciicircum", Integer.valueOf((int)'^')},
      new Object[]{"textasciitilde", Integer.valueOf((int)'~')},
      new Object[]{"textbackslash", Integer.valueOf((int)'\\')},
      new Object[]{"textbar", Integer.valueOf((int)'|')},
      new Object[]{"textbardbl", Integer.valueOf(0x2016)},
      new Object[]{"textbraceleft", Integer.valueOf((int)'{')},
      new Object[]{"textbraceright", Integer.valueOf((int)'}')},
      new Object[]{"textdollar", Integer.valueOf((int)'$')},
      new Object[]{"textbullet", Integer.valueOf(0x2022)},
      new Object[]{"textexclamdown", Integer.valueOf(0x00A1)},
      new Object[]{"textgreater", Integer.valueOf((int)'>')},
      new Object[]{"textless", Integer.valueOf((int)'<')},
      new Object[]{"textminus", Integer.valueOf(0x2212)},
      new Object[]{"textperiodcentered", Integer.valueOf(0x00B7)},
      new Object[]{"textasteriskcentered", Integer.valueOf(0xFF0A)},
      new Object[]{"textquestiondown", Integer.valueOf(0x00BF)},
      new Object[]{"textquotedbl", Integer.valueOf((int)'"')},
      new Object[]{"textquotedblleft", Integer.valueOf(0x201C)},
      new Object[]{"textquotedblright", Integer.valueOf(0x201D)},
      new Object[]{"textquoteleft", Integer.valueOf(0x2018)},
      new Object[]{"textquoteright", Integer.valueOf(0x2019)},
      new Object[]{"textsection", Integer.valueOf(0x00A7)},
      new Object[]{"textparagraph", Integer.valueOf(0x00B6)},
      new Object[]{"textsterling", Integer.valueOf(0x00A3)},
      new Object[]{"textregistered", Integer.valueOf(0x00AE)},
      new Object[]{"texttrademark", Integer.valueOf(0x2122)},
      new Object[]{"textunderscore", Integer.valueOf((int)'_')},
      new Object[]{"textvisiblespace", Integer.valueOf(0x2423)},
      new Object[]{"textperthousand", Integer.valueOf(0x2030)},
      new Object[]{"textpertenthousand", Integer.valueOf(0x2031)},
      new Object[]{"textasteriskcentered", Integer.valueOf(0x2217)},
      new Object[]{"textdagger", Integer.valueOf(0x2020)},
      new Object[]{"textdaggerdbl", Integer.valueOf(0x2021)},
      new Object[]{"guilsinglleft", Integer.valueOf(0x2039)},
      new Object[]{"guilsinglright", Integer.valueOf(0x203A)},
      new Object[]{"guillemotleft", Integer.valueOf(0x00AB)},
      new Object[]{"guillemotright", Integer.valueOf(0x00BB)},
      new Object[]{"yen", Integer.valueOf(0x00A5)},
      new Object[]{"ss", Integer.valueOf(0x00DF)},
      new Object[]{"ae", Integer.valueOf(0x00E6)},
      new Object[]{"AE", Integer.valueOf(0x00C6)},
      new Object[]{"eth", Integer.valueOf(0x00F0)},
      new Object[]{"Thorn", Integer.valueOf(0x00DE)},
      new Object[]{"thorn", Integer.valueOf(0x00FE)},
      new Object[]{"micro", Integer.valueOf(0x00B5)},
      new Object[]{"degree", Integer.valueOf(0x00B0)},
      new Object[]{"textdegree", Integer.valueOf(0x00B0)},
      new Object[]{"L", Integer.valueOf(0x0141)},
      new Object[]{"l", Integer.valueOf(0x0142)},
      new Object[]{"OE", Integer.valueOf(0x0152)},
      new Object[]{"oe", Integer.valueOf(0x0153)},
      new Object[]{"AA", Integer.valueOf(0x00C5)},
      new Object[]{"aa", Integer.valueOf(0x00E5)},
      new Object[]{"i", Integer.valueOf(0x0131)},
      new Object[]{"j", Integer.valueOf(0x0237)},
      new Object[]{"IJ", Integer.valueOf(0x0132)},
      new Object[]{"ij", Integer.valueOf(0x0133)},
      new Object[]{"ng", Integer.valueOf(0x014B)},
      new Object[]{"NG", Integer.valueOf(0x014A)},
      new Object[]{"th", Integer.valueOf(0x00FE)},
      new Object[]{"TH", Integer.valueOf(0x00DE)},
      new Object[]{"dh", Integer.valueOf(0x00F0)},
      new Object[]{"DH", Integer.valueOf(0x00D0)},
      new Object[]{"dj", Integer.valueOf(0x0111)},
      new Object[]{"DJ", Integer.valueOf(0x0110)},
      new Object[]{"quotedblbase", Integer.valueOf(0x201E)},
      new Object[]{"quotesinglbase", Integer.valueOf(0x201A)},
   };

   public static final Object[][] BIG_MATH_SYMBOLS = 
   {
      new Object[]{"bigsqcap", Integer.valueOf(0x2293), Integer.valueOf(0x2A05)},
      new Object[]{"bigsqcup", Integer.valueOf(0x2294), Integer.valueOf(0x2A06)},
      new Object[]{"sum", Integer.valueOf(0x2211), Integer.valueOf(0x2211)},
      new Object[]{"bigcap", Integer.valueOf(0x2229), Integer.valueOf(0x22C2)},
      new Object[]{"bigcup", Integer.valueOf(0x222A), Integer.valueOf(0x22C3)},
      new Object[]{"bigodot", Integer.valueOf(0x2299), Integer.valueOf(0x2A00)},
      new Object[]{"bigoplus", Integer.valueOf(0x2295), Integer.valueOf(0x2A01)},
      new Object[]{"bigotimes", Integer.valueOf(0x2297), Integer.valueOf(0x2A02)},
      new Object[]{"biguplus", Integer.valueOf(0x228E), Integer.valueOf(0x2A04)},
      new Object[]{"bigvee", Integer.valueOf(0x2228), Integer.valueOf(0x22C1)},
      new Object[]{"bigwedge", Integer.valueOf(0x2227), Integer.valueOf(0x22C0)},
      new Object[]{"int", Integer.valueOf(0x222B), Integer.valueOf(0x222B)},
      new Object[]{"intop", Integer.valueOf(0x222B), Integer.valueOf(0x222B)},
      new Object[]{"smallint", Integer.valueOf(0x222B), Integer.valueOf(0x222B)},
      new Object[]{"varint", Integer.valueOf(0x222B), Integer.valueOf(0x222B)},
      new Object[]{"oint", Integer.valueOf(0x222E), Integer.valueOf(0x222E)},
      new Object[]{"ointop", Integer.valueOf(0x222E), Integer.valueOf(0x222E)},
      new Object[]{"varoint", Integer.valueOf(0x222E), Integer.valueOf(0x222E)},
      new Object[]{"coprod", Integer.valueOf(0x2A3F), Integer.valueOf(0x2210)},
      new Object[]{"bigcurlyvee", Integer.valueOf(0x22CE), Integer.valueOf(0x22CE)},
      new Object[]{"bigcurlywedge", Integer.valueOf(0x22CF), Integer.valueOf(0x22CF)},
      new Object[]{"iint", Integer.valueOf(0x222C), Integer.valueOf(0x222C)},
      new Object[]{"iiint", Integer.valueOf(0x222D), Integer.valueOf(0x222D)},
      new Object[]{"oiint", Integer.valueOf(0x222F), Integer.valueOf(0x222F)},
      new Object[]{"oiiint", Integer.valueOf(0x2230), Integer.valueOf(0x2230)},
      new Object[]{"ointclockwise", Integer.valueOf(0x2232), Integer.valueOf(0x2232)},
      new Object[]{"ointctrclockwise", Integer.valueOf(0x2233), Integer.valueOf(0x2233)},
      new Object[]{"sumint", Integer.valueOf(0x2A0B), Integer.valueOf(0x2A0B)},
      new Object[]{"sqint", Integer.valueOf(0x2A16), Integer.valueOf(0x2A16)},
      new Object[]{"fint", Integer.valueOf(0x2A0F), Integer.valueOf(0x2A0F)},
      new Object[]{"landdownint", Integer.valueOf(0x2A1A), Integer.valueOf(0x2A1A)},
      new Object[]{"landupint", Integer.valueOf(0x2A19), Integer.valueOf(0x2A19)},
      new Object[]{"bigtimes", Integer.valueOf(0x2A09), Integer.valueOf(0x2A09)},
      new Object[]{"prod", Integer.valueOf(0x220F), Integer.valueOf(0x220F)},
      new Object[]{"bigtriangleup", Integer.valueOf(0x25B3), Integer.valueOf(0x25B3)},
      new Object[]{"bigtriangledown", Integer.valueOf(0x25BD), Integer.valueOf(0x25BD)},
      new Object[]{"varbigtriangleup", Integer.valueOf(0x25B3), Integer.valueOf(0x25B3)},
      new Object[]{"varbigtriangledown", Integer.valueOf(0x25BD), Integer.valueOf(0x25BD)},
   };

   public static final Object[][] DELIMITER_SYMBOLS = 
   {// approximate
      new Object[]{"vert", Integer.valueOf(0x2223)},// divides
      new Object[]{"Vert", Integer.valueOf(0x2225)},// parallel
      new Object[]{"langle", Integer.valueOf(0x27E8)},
      new Object[]{"rangle", Integer.valueOf(0x27E9)},
      // not taking spacing into account
      new Object[]{"lvert", Integer.valueOf(0x2223)},
      new Object[]{"rvert", Integer.valueOf(0x2223)},
      new Object[]{"lVert", Integer.valueOf(0x2225)},
      new Object[]{"rVert", Integer.valueOf(0x2225)},
   };

   // Some of the upper case Greek symbols aren't
   // actually defined in the LaTeX kernel, but
   // they're added here for completeness.
   public static final Object[][] GREEK_SYMBOLS = 
   {
      new Object[]{"Alpha", Integer.valueOf(0x1D6E2)},
      new Object[]{"Beta", Integer.valueOf(0x1D6E3)},
      new Object[]{"Gamma", Integer.valueOf(0x1D6E4)},
      new Object[]{"Delta", Integer.valueOf(0x1D6E5)},
      new Object[]{"Epsilon", Integer.valueOf(0x1D6E6)},
      new Object[]{"Zeta", Integer.valueOf(0x1D6E7)},
      new Object[]{"Eta", Integer.valueOf(0x1D6E8)},
      new Object[]{"Theta", Integer.valueOf(0x1D6E9)},
      new Object[]{"Iota", Integer.valueOf(0x1D6EA)},
      new Object[]{"Kappa", Integer.valueOf(0x1D6EB)},
      new Object[]{"Lambda", Integer.valueOf(0x1D6EC)},
      new Object[]{"Mu", Integer.valueOf(0x1D6ED)},
      new Object[]{"Nu", Integer.valueOf(0x1D6EE)},
      new Object[]{"Xi", Integer.valueOf(0x1D6EF)},
      new Object[]{"Omicron", Integer.valueOf(0x1D6F0)},
      new Object[]{"Pi", Integer.valueOf(0x1D6F1)},
      new Object[]{"Rho", Integer.valueOf(0x1D6F2)},
      new Object[]{"Theta", Integer.valueOf(0x1D6F3)},
      new Object[]{"Sigma", Integer.valueOf(0x1D6F4)},
      new Object[]{"Tau", Integer.valueOf(0x1D6F5)},
      new Object[]{"Upsilon", Integer.valueOf(0x1D6F6)},
      new Object[]{"Phi", Integer.valueOf(0x1D6F7)},
      new Object[]{"Chi", Integer.valueOf(0x1D6F8)},
      new Object[]{"Psi", Integer.valueOf(0x1D6F9)},
      new Object[]{"Omega", Integer.valueOf(0x1D6FA)},
      new Object[]{"nabla", Integer.valueOf(0x1D6FB)},
      new Object[]{"alpha", Integer.valueOf(0x1D6FC)},
      new Object[]{"beta", Integer.valueOf(0x1D6FD)},
      new Object[]{"gamma", Integer.valueOf(0x1D6FE)},
      new Object[]{"delta", Integer.valueOf(0x1D6FF)},
      new Object[]{"varepsilon", Integer.valueOf(0x1D700)},
      new Object[]{"zeta", Integer.valueOf(0x1D701)},
      new Object[]{"eta", Integer.valueOf(0x1D702)},
      new Object[]{"theta", Integer.valueOf(0x1D703)},
      new Object[]{"iota", Integer.valueOf(0x1D704)},
      new Object[]{"kappa", Integer.valueOf(0x1D705)},
      new Object[]{"lambda", Integer.valueOf(0x1D706)},
      new Object[]{"mu", Integer.valueOf(0x1D707)},
      new Object[]{"nu", Integer.valueOf(0x1D708)},
      new Object[]{"xi", Integer.valueOf(0x1D709)},
      new Object[]{"omicron", Integer.valueOf(0x1D70A)},
      new Object[]{"pi", Integer.valueOf(0x1D70B)},
      new Object[]{"rho", Integer.valueOf(0x1D70C)},
      new Object[]{"varsigma", Integer.valueOf(0x1D70D)},
      new Object[]{"sigma", Integer.valueOf(0x1D70E)},
      new Object[]{"tau", Integer.valueOf(0x1D70F)},
      new Object[]{"upsilon", Integer.valueOf(0x1D710)},
      new Object[]{"varphi", Integer.valueOf(0x1D711)},
      new Object[]{"chi", Integer.valueOf(0x1D712)},
      new Object[]{"psi", Integer.valueOf(0x1D713)},
      new Object[]{"omega", Integer.valueOf(0x1D714)},
      new Object[]{"epsilon", Integer.valueOf(0x1D716)},
      new Object[]{"vartheta", Integer.valueOf(0x1D717)},
      new Object[]{"varkappa", Integer.valueOf(0x1D718)},
      new Object[]{"phi", Integer.valueOf(0x1D719)},
      new Object[]{"varrho", Integer.valueOf(0x1D71A)},
      new Object[]{"varpi", Integer.valueOf(0x1D71B)},
   };

   public static final Object[][] MATH_SYMBOLS = 
   {
      new Object[]{"digamma", Integer.valueOf(0x03DD)},
      new Object[]{"Digamma", Integer.valueOf(0x03DC)},
      new Object[]{"forall", Integer.valueOf(0x2200)},
      new Object[]{"complement", Integer.valueOf(0x2201)},
      new Object[]{"partial", Integer.valueOf(0x2202)},
      new Object[]{"varpartialdiff", Integer.valueOf(0x1D715)},
      new Object[]{"exists", Integer.valueOf(0x2203)},
      new Object[]{"nexists", Integer.valueOf(0x2204)},
      new Object[]{"varnothing", Integer.valueOf(0x2205)},
      new Object[]{"in", Integer.valueOf(0x2208)},
      new Object[]{"notin", Integer.valueOf(0x2209)},
      new Object[]{"ni", Integer.valueOf(0x220B)},
      new Object[]{"owns", Integer.valueOf(0x220B)},
      new Object[]{"notni", Integer.valueOf(0x220C)},
      new Object[]{"vdots", Integer.valueOf(0x22EE)},
      new Object[]{"cdots", Integer.valueOf(0x22EF)},
      new Object[]{"ddots", Integer.valueOf(0x22F1)},
      new Object[]{"surd", Integer.valueOf(0x221A)},
      new Object[]{"infty", Integer.valueOf(0x221E)},
      new Object[]{"rightangle", Integer.valueOf(0x221F)},
      new Object[]{"angle", Integer.valueOf(0x2220)},
      new Object[]{"measuredangle", Integer.valueOf(0x2221)},
      new Object[]{"sphericalangle", Integer.valueOf(0x2222)},
      new Object[]{"mathdollar", Integer.valueOf(0x0024)},
      new Object[]{"mathellipsis", Integer.valueOf(0x2026)},
      new Object[]{"mathparagraph", Integer.valueOf(0x00B6)},
      new Object[]{"mathsection", Integer.valueOf(0x00A7)},
      new Object[]{"mathsterling", Integer.valueOf(0x00A3)},
      new Object[]{"mathunderscore", Integer.valueOf(0x005F)},
      new Object[]{"bot", Integer.valueOf(0x22A5)},
      new Object[]{"top", Integer.valueOf(0x22A4)},
      new Object[]{"Im", Integer.valueOf(0x2111)},
      new Object[]{"ell", Integer.valueOf(0x2113)},
      new Object[]{"Re", Integer.valueOf(0x211C)},
      new Object[]{"ohm", Integer.valueOf(0x2126)},
      new Object[]{"mho", Integer.valueOf(0x2127)},
      new Object[]{"mathring", Integer.valueOf(0x212B)},
      new Object[]{"aleph", Integer.valueOf(0x2135)},
      new Object[]{"beth", Integer.valueOf(0x2136)},
      new Object[]{"gimel", Integer.valueOf(0x2137)},
      new Object[]{"daleth", Integer.valueOf(0x2138)},
      new Object[]{"hbar", Integer.valueOf(0x210F)},
      new Object[]{"wp", Integer.valueOf(0x2118)},
      new Object[]{"Bbbk", Integer.valueOf(0x1D55C)},
      new Object[]{"game", Integer.valueOf(0x2141)},
      new Object[]{"Finv", Integer.valueOf(0x2132)},
      new Object[]{"imath", Integer.valueOf(0x1D6A4)},
      new Object[]{"jmath", Integer.valueOf(0x1D6A5)},
      new Object[]{"emptyset", Integer.valueOf(0x2205)},
      new Object[]{"prime", Integer.valueOf(0x2032)},
      new Object[]{"hbar", Integer.valueOf(0x210F)},
      new Object[]{"triangle", Integer.valueOf(0x25B3)},
      new Object[]{"neg", Integer.valueOf(0x00AC)},
      new Object[]{"flat", Integer.valueOf(0x266D)},
      new Object[]{"natural", Integer.valueOf(0x266E)},
      new Object[]{"sharp", Integer.valueOf(0x266F)},
      new Object[]{"clubsuit", Integer.valueOf(0x2663)},
      new Object[]{"diamondsuit", Integer.valueOf(0x2662)},
      new Object[]{"heartsuit", Integer.valueOf(0x2661)},
      new Object[]{"spadesuit", Integer.valueOf(0x2660)},
      new Object[]{"ldotp", Integer.valueOf(0x002E)},
      new Object[]{"colon", Integer.valueOf(0x2236)},
      new Object[]{"cdotp", Integer.valueOf(0x22C5)},
   };

   public static final Object[][] BINARY_MATH_SYMBOLS = 
   {
      new Object[]{"le", Integer.valueOf(0x2264)},
      new Object[]{"leq", Integer.valueOf(0x2264)},
      new Object[]{"ge", Integer.valueOf(0x2265)},
      new Object[]{"geq", Integer.valueOf(0x2265)},
      new Object[]{"ll", Integer.valueOf(0x226A)},
      new Object[]{"gg", Integer.valueOf(0x226B)},
      new Object[]{"neq", Integer.valueOf(0x2260)},
      new Object[]{"ne", Integer.valueOf(0x2260)},
      new Object[]{"amalg", Integer.valueOf(0x2A3F)},
      new Object[]{"approx", Integer.valueOf(0x2248)},
      new Object[]{"approxeq", Integer.valueOf(0x224A)},
      new Object[]{"ast", Integer.valueOf(0x2217)},
      new Object[]{"asymp", Integer.valueOf(0x224D)},
      new Object[]{"backsim", Integer.valueOf(0x223D)},
      new Object[]{"because", Integer.valueOf(0x2235)},
      new Object[]{"between", Integer.valueOf(0x226C)},
      new Object[]{"bigcirc", Integer.valueOf(0x25CB)},
      new Object[]{"bigtriangledown", Integer.valueOf(0x25BF)},
      new Object[]{"bigtriangleup", Integer.valueOf(0x25B5)},
      new Object[]{"bowtie", Integer.valueOf(0x22C8)},
      new Object[]{"bullet", Integer.valueOf(0x2022)},
      new Object[]{"bumpeq", Integer.valueOf(0x224F)},
      new Object[]{"cap", Integer.valueOf(0x2229)},
      new Object[]{"cdot", Integer.valueOf(0x2219)},
      new Object[]{"cong", Integer.valueOf(0x2245)},
      new Object[]{"circ", Integer.valueOf(0x2218)},
      new Object[]{"cup", Integer.valueOf(0x222A)},
      new Object[]{"dagger", Integer.valueOf(0x2020)},
      new Object[]{"dashv", Integer.valueOf(0x22A3)},
      new Object[]{"ddagger", Integer.valueOf(0x2021)},
      new Object[]{"diamond", Integer.valueOf(0x2B26)},
      new Object[]{"div", Integer.valueOf(0x00F7)},
      new Object[]{"doteqdot", Integer.valueOf(0x2251)},
      new Object[]{"fallingdotseq", Integer.valueOf(0x2252)},
      new Object[]{"lhd", Integer.valueOf(0x22B2)},
      new Object[]{"mp", Integer.valueOf(0x2213)},
      new Object[]{"ncong", Integer.valueOf(0x2247)},
      new Object[]{"notcong", Integer.valueOf(0x2247)},
      new Object[]{"nmid", Integer.valueOf(0x2224)},
      new Object[]{"notmid", Integer.valueOf(0x2224)},
      new Object[]{"nprec", Integer.valueOf(0x2280)},
      new Object[]{"notprec", Integer.valueOf(0x2280)},
      new Object[]{"npreceq", Integer.valueOf(0x22E0)},
      new Object[]{"notpreceq", Integer.valueOf(0x22E0)},
      new Object[]{"nsucc", Integer.valueOf(0x2281)},
      new Object[]{"notsucc", Integer.valueOf(0x2281)},
      new Object[]{"nsucceq", Integer.valueOf(0x22E1)},
      new Object[]{"notsucceq", Integer.valueOf(0x22E1)},
      new Object[]{"odot", Integer.valueOf(0x2299)},
      new Object[]{"ominus", Integer.valueOf(0x2296)},
      new Object[]{"oplus", Integer.valueOf(0x2295)},
      new Object[]{"oslash", Integer.valueOf(0x2298)},
      new Object[]{"otimes", Integer.valueOf(0x2297)},
      new Object[]{"pm", Integer.valueOf(0x00B1)},
      new Object[]{"rhd", Integer.valueOf(0x22B3)},
      new Object[]{"setminus", Integer.valueOf(0x2216)},
      new Object[]{"sqcap", Integer.valueOf(0x2293)},
      new Object[]{"sqcup", Integer.valueOf(0x2294)},
      new Object[]{"star", Integer.valueOf(0x22C6)},
      new Object[]{"times", Integer.valueOf(0x00D7)},
      new Object[]{"triangleleft", Integer.valueOf(0x25C3)},
      new Object[]{"triangleright", Integer.valueOf(0x25B7)},
      new Object[]{"unlhd", Integer.valueOf(0x22B4)},
      new Object[]{"unrhd", Integer.valueOf(0x22B5)},
      new Object[]{"uplus", Integer.valueOf(0x228E)},
      new Object[]{"vee", Integer.valueOf(0x2228)},
      new Object[]{"wedge", Integer.valueOf(0x2227)},
      new Object[]{"wr", Integer.valueOf(0x2240)},
      new Object[]{"wreath", Integer.valueOf(0x2240)},
      new Object[]{"boxdot", Integer.valueOf(0x22A1)},
      new Object[]{"boxminus", Integer.valueOf(0x229F)},
      new Object[]{"boxplus", Integer.valueOf(0x229E)},
      new Object[]{"boxtimes", Integer.valueOf(0x22A0)},
      new Object[]{"Cap", Integer.valueOf(0x22D2)},
      new Object[]{"centerdot", Integer.valueOf(0x22C5)},
      new Object[]{"circledast", Integer.valueOf(0x229B)},
      new Object[]{"circledcirc", Integer.valueOf(0x229A)},
      new Object[]{"circleddash", Integer.valueOf(0x229D)},
      new Object[]{"Cup", Integer.valueOf(0x22D3)},
      new Object[]{"curlyvee", Integer.valueOf(0x22CE)},
      new Object[]{"curlywedge", Integer.valueOf(0x22CF)},
      new Object[]{"divideontimes", Integer.valueOf(0x22C7)},
      new Object[]{"dotequal", Integer.valueOf(0x2250)},
      new Object[]{"dotplus", Integer.valueOf(0x2214)},
      new Object[]{"doublebarwedge", Integer.valueOf(0x2A5E)},
      new Object[]{"equiv", Integer.valueOf(0x2261)},
      new Object[]{"frown", Integer.valueOf(0x2054)},// not sure about this one
      new Object[]{"intercal", Integer.valueOf(0x22BA)},
      new Object[]{"Join", Integer.valueOf(0x2A1D)},
      new Object[]{"leftthreetimes", Integer.valueOf(0x22CB)},
      new Object[]{"ltimes", Integer.valueOf(0x22C9)},
      new Object[]{"mid", Integer.valueOf(0x2223)},
      new Object[]{"models", Integer.valueOf(0x22A7)},
      new Object[]{"nparallel", Integer.valueOf(0x2226)},
      new Object[]{"notparallel", Integer.valueOf(0x2226)},
      new Object[]{"nvdash", Integer.valueOf(0x22AC)},
      new Object[]{"notvdash", Integer.valueOf(0x22AC)},
      new Object[]{"nvDash", Integer.valueOf(0x22AD)},
      new Object[]{"notvDash", Integer.valueOf(0x22AD)},
      new Object[]{"nVDash", Integer.valueOf(0x22AF)},
      new Object[]{"notVDash", Integer.valueOf(0x22AF)},
      new Object[]{"parallel", Integer.valueOf(0x2225)},
      new Object[]{"perp", Integer.valueOf(0x22A5)},
      new Object[]{"prec", Integer.valueOf(0x227A)},
      new Object[]{"preceq", Integer.valueOf(0x227C)},
      new Object[]{"propto", Integer.valueOf(0x221D)},
      new Object[]{"sqsubseteq", Integer.valueOf(0x2291)},
      new Object[]{"sqsupseteq", Integer.valueOf(0x2292)},
      new Object[]{"rightthreetimes", Integer.valueOf(0x22CC)},
      new Object[]{"rtimes", Integer.valueOf(0x22CA)},
      new Object[]{"sim", Integer.valueOf(0x223C)},
      new Object[]{"simeq", Integer.valueOf(0x2243)},
      new Object[]{"smallsetminus", Integer.valueOf(0x29F5)},
      new Object[]{"smile", Integer.valueOf(0x203F)},// not sure about this one
      new Object[]{"succ", Integer.valueOf(0x227B)},
      new Object[]{"succeq", Integer.valueOf(0x227D)},
      new Object[]{"therefore", Integer.valueOf(0x2234)},
      new Object[]{"veebar", Integer.valueOf(0x2A61)},
      new Object[]{"plus", Integer.valueOf(0x002B)},
      new Object[]{"minus", Integer.valueOf(0x2212)},
      new Object[]{"udtimes", Integer.valueOf(0x29D6)},
      new Object[]{"vdash", Integer.valueOf(0x22A2)},
      new Object[]{"Vdash", Integer.valueOf(0x22A9)},
      new Object[]{"vDash", Integer.valueOf(0x22A8)},
      new Object[]{"Vvdash", Integer.valueOf(0x22AA)},
      new Object[]{"vcentcolon", Integer.valueOf(0x2236)},
      new Object[]{"squaredots", Integer.valueOf(0x2237)},
      new Object[]{"dotminus", Integer.valueOf(0x2238)},
      new Object[]{"eqcolon", Integer.valueOf(0x2239)},
      new Object[]{"risingdotseq", Integer.valueOf(0x2253)},
      new Object[]{"eqqcolon", Integer.valueOf(0x2255)},
      new Object[]{"nearrow", Integer.valueOf(0x2197)},
      new Object[]{"searrow", Integer.valueOf(0x2198)},
      new Object[]{"nwarrow", Integer.valueOf(0x2196)},
      new Object[]{"swarrow", Integer.valueOf(0x2199)},
      new Object[]{"Leftrightarrow", Integer.valueOf(0x21D4)},
      new Object[]{"Leftarrow", Integer.valueOf(0x21D0)},
      new Object[]{"Rightarrow", Integer.valueOf(0x21D2)},
      new Object[]{"supset", Integer.valueOf(0x2283)},
      new Object[]{"subset", Integer.valueOf(0x2282)},
      new Object[]{"supseteq", Integer.valueOf(0x2287)},
      new Object[]{"subseteq", Integer.valueOf(0x2286)},
      new Object[]{"not", Integer.valueOf(0x2215)},// not sure about this one
      new Object[]{"leftrightarrow", Integer.valueOf(0x2194)},
      new Object[]{"leftarrow", Integer.valueOf(0x2190)},
      new Object[]{"gets", Integer.valueOf(0x2190)},
      new Object[]{"rightarrow", Integer.valueOf(0x2192)},
      new Object[]{"to", Integer.valueOf(0x2192)},
      new Object[]{"mapsto", Integer.valueOf(0x21A6)},
      new Object[]{"leftharpoonup", Integer.valueOf(0x21BC)},
      new Object[]{"leftharpoondown", Integer.valueOf(0x21BD)},
      new Object[]{"rightharpoonup", Integer.valueOf(0x21C0)},
      new Object[]{"rightharpoondown", Integer.valueOf(0x21C1)},
      new Object[]{"rightleftharpoons", Integer.valueOf(0x21CC)},
      new Object[]{"doteq", Integer.valueOf(0x2250)},
      new Object[]{"hookrightarrow", Integer.valueOf(0x21AA)},
      new Object[]{"hookleftarrow", Integer.valueOf(0x21A9)},
      new Object[]{"Longrightarrow", Integer.valueOf(0x27F9)},
      new Object[]{"longrightarrow", Integer.valueOf(0x27F6)},
      new Object[]{"longleftarrow", Integer.valueOf(0x27F5)},
      new Object[]{"Longleftarrow", Integer.valueOf(0x27F8)},
      new Object[]{"longmapsto", Integer.valueOf(0x27FC)},
      new Object[]{"longleftrightarrow", Integer.valueOf(0x27F7)},
      new Object[]{"Longleftrightarrow", Integer.valueOf(0x27FA)},
      new Object[]{"iff", Integer.valueOf(0x27FA)},// this should actually have \; spacing on either side
   };

   protected int codePoint;
}
