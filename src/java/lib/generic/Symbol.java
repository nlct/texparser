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
      new Object[]{"{", new Integer((int)'{')},
      new Object[]{"}", new Integer((int)'}')},
      new Object[]{"%", new Integer((int)'%')},
      new Object[]{"_", new Integer((int)'_')},
      new Object[]{"&", new Integer((int)'&')},
      new Object[]{"$", new Integer((int)'$')},
      new Object[]{"#", new Integer((int)'#')},
      new Object[]{"lbrack", new Integer((int)'[')},
      new Object[]{"rbrack", new Integer((int)']')},
   };

   public static final Object[][] TEXT_SYMBOLS =
   {
      new Object[]{"P", new Integer(0x00B6)},
      new Object[]{"S", new Integer(0x00A7)},
      new Object[]{"O", new Integer(0x00D8)},
      new Object[]{"o", new Integer(0x00F8)},
      new Object[]{"copyright", new Integer(0x00A9)},
      new Object[]{"ldots", new Integer(0x2026)},
      new Object[]{"pounds", new Integer(0x00A3)},
      new Object[]{"dag", new Integer(0x2020)},
      new Object[]{"ddag", new Integer(0x2021)},
      new Object[]{"slash", new Integer((int)'/')},
      new Object[]{"textendash", new Integer(0x2013)},
      new Object[]{"textemdash", new Integer(0x2014)},
      new Object[]{"textasciicircum", new Integer((int)'^')},
      new Object[]{"textasciitilde", new Integer((int)'~')},
      new Object[]{"textbackslash", new Integer((int)'\\')},
      new Object[]{"textbar", new Integer((int)'|')},
      new Object[]{"textbardbl", new Integer(0x2016)},
      new Object[]{"textbraceleft", new Integer((int)'{')},
      new Object[]{"textbraceright", new Integer((int)'}')},
      new Object[]{"textdollar", new Integer((int)'$')},
      new Object[]{"textbullet", new Integer(0x2022)},
      new Object[]{"textexclamdown", new Integer(0x00A1)},
      new Object[]{"textgreater", new Integer((int)'>')},
      new Object[]{"textless", new Integer((int)'<')},
      new Object[]{"textperiodcentered", new Integer(0x00B7)},
      new Object[]{"textasteriskcentered", new Integer(0xFF0A)},
      new Object[]{"textquestiondown", new Integer(0x00BF)},
      new Object[]{"textquotedbl", new Integer((int)'"')},
      new Object[]{"textquotedblleft", new Integer(0x201C)},
      new Object[]{"textquotedblright", new Integer(0x201D)},
      new Object[]{"textquoteleft", new Integer(0x2018)},
      new Object[]{"textquoteright", new Integer(0x2019)},
      new Object[]{"textsection", new Integer(0x00A7)},
      new Object[]{"textparagraph", new Integer(0x00B6)},
      new Object[]{"textsterling", new Integer(0x00A3)},
      new Object[]{"textregistered", new Integer(0x00AE)},
      new Object[]{"texttrademark", new Integer(0x2122)},
      new Object[]{"textunderscore", new Integer((int)'_')},
      new Object[]{"textvisiblespace", new Integer(0x2423)},
      new Object[]{"textperthousand", new Integer(0x2030)},
      new Object[]{"textpertenthousand", new Integer(0x2031)},
      new Object[]{"textasteriskcentered", new Integer(0x2217)},
      new Object[]{"textdagger", new Integer(0x2020)},
      new Object[]{"textdaggerdbl", new Integer(0x2021)},
      new Object[]{"guilsinglleft", new Integer(0x2039)},
      new Object[]{"guilsinglright", new Integer(0x203A)},
      new Object[]{"guillemotleft", new Integer(0x00AB)},
      new Object[]{"guillemotright", new Integer(0x00BB)},
      new Object[]{"yen", new Integer(0x00A5)},
      new Object[]{"ss", new Integer(0x00DF)},
      new Object[]{"ae", new Integer(0x00E6)},
      new Object[]{"AE", new Integer(0x00C6)},
      new Object[]{"eth", new Integer(0x00F0)},
      new Object[]{"Thorn", new Integer(0x00DE)},
      new Object[]{"thorn", new Integer(0x00FE)},
      new Object[]{"micro", new Integer(0x00B5)},
      new Object[]{"degree", new Integer(0x00B0)},
      new Object[]{"textdegree", new Integer(0x00B0)},
      new Object[]{"L", new Integer(0x0141)},
      new Object[]{"l", new Integer(0x0142)},
      new Object[]{"OE", new Integer(0x0152)},
      new Object[]{"oe", new Integer(0x0153)},
      new Object[]{"AA", new Integer(0x00C5)},
      new Object[]{"aa", new Integer(0x00E5)},
      new Object[]{"i", new Integer(0x0131)},
      new Object[]{"j", new Integer(0x0237)},
      new Object[]{"IJ", new Integer(0x0132)},
      new Object[]{"ij", new Integer(0x0133)},
      new Object[]{"ng", new Integer(0x014B)},
      new Object[]{"NG", new Integer(0x014A)},
      new Object[]{"th", new Integer(0x00FE)},
      new Object[]{"TH", new Integer(0x00DE)},
      new Object[]{"dh", new Integer(0x00F0)},
      new Object[]{"DH", new Integer(0x00D0)},
      new Object[]{"dj", new Integer(0x0111)},
      new Object[]{"DJ", new Integer(0x0110)},
      new Object[]{"quotedblbase", new Integer(0x201E)},
      new Object[]{"quotesinglbase", new Integer(0x201A)},
   };

   public static final Object[][] BIG_MATH_SYMBOLS = 
   {
      new Object[]{"bigsqcap", new Integer(0x2293), new Integer(0x2A05)},
      new Object[]{"bigsqcup", new Integer(0x2294), new Integer(0x2A06)},
      new Object[]{"sum", new Integer(0x2211), new Integer(0x2211)},
      new Object[]{"bigcap", new Integer(0x2229), new Integer(0x22C2)},
      new Object[]{"bigcup", new Integer(0x222A), new Integer(0x22C3)},
      new Object[]{"bigodot", new Integer(0x2299), new Integer(0x2A00)},
      new Object[]{"bigoplus", new Integer(0x2295), new Integer(0x2A01)},
      new Object[]{"bigotimes", new Integer(0x2297), new Integer(0x2A02)},
      new Object[]{"biguplus", new Integer(0x228E), new Integer(0x2A04)},
      new Object[]{"bigvee", new Integer(0x2228), new Integer(0x22C1)},
      new Object[]{"bigwedge", new Integer(0x2227), new Integer(0x22C0)},
      new Object[]{"int", new Integer(0x222B), new Integer(0x222B)},
      new Object[]{"intop", new Integer(0x222B), new Integer(0x222B)},
      new Object[]{"smallint", new Integer(0x222B), new Integer(0x222B)},
      new Object[]{"varint", new Integer(0x222B), new Integer(0x222B)},
      new Object[]{"oint", new Integer(0x222E), new Integer(0x222E)},
      new Object[]{"ointop", new Integer(0x222E), new Integer(0x222E)},
      new Object[]{"varoint", new Integer(0x222E), new Integer(0x222E)},
      new Object[]{"coprod", new Integer(0x2A3F), new Integer(0x2210)},
      new Object[]{"bigcurlyvee", new Integer(0x22CE), new Integer(0x22CE)},
      new Object[]{"bigcurlywedge", new Integer(0x22CF), new Integer(0x22CF)},
      new Object[]{"iint", new Integer(0x222C), new Integer(0x222C)},
      new Object[]{"iiint", new Integer(0x222D), new Integer(0x222D)},
      new Object[]{"oiint", new Integer(0x222F), new Integer(0x222F)},
      new Object[]{"oiiint", new Integer(0x2230), new Integer(0x2230)},
      new Object[]{"ointclockwise", new Integer(0x2232), new Integer(0x2232)},
      new Object[]{"ointctrclockwise", new Integer(0x2233), new Integer(0x2233)},
      new Object[]{"sumint", new Integer(0x2A0B), new Integer(0x2A0B)},
      new Object[]{"sqint", new Integer(0x2A16), new Integer(0x2A16)},
      new Object[]{"fint", new Integer(0x2A0F), new Integer(0x2A0F)},
      new Object[]{"landdownint", new Integer(0x2A1A), new Integer(0x2A1A)},
      new Object[]{"landupint", new Integer(0x2A19), new Integer(0x2A19)},
      new Object[]{"bigtimes", new Integer(0x2A09), new Integer(0x2A09)},
      new Object[]{"prod", new Integer(0x220F), new Integer(0x220F)},
      new Object[]{"bigtriangleup", new Integer(0x25B3), new Integer(0x25B3)},
      new Object[]{"bigtriangledown", new Integer(0x25BD), new Integer(0x25BD)},
      new Object[]{"varbigtriangleup", new Integer(0x25B3), new Integer(0x25B3)},
      new Object[]{"varbigtriangledown", new Integer(0x25BD), new Integer(0x25BD)},
   };

   public static final Object[][] DELIMITER_SYMBOLS = 
   {// approximate
      new Object[]{"vert", new Integer(0x2223)},// divides
      new Object[]{"Vert", new Integer(0x2225)},// parallel
      new Object[]{"langle", new Integer(0x27E8)},
      new Object[]{"rangle", new Integer(0x27E9)},
      // not taking spacing into account
      new Object[]{"lvert", new Integer(0x2223)},
      new Object[]{"rvert", new Integer(0x2223)},
      new Object[]{"lVert", new Integer(0x2225)},
      new Object[]{"rVert", new Integer(0x2225)},
   };

   // Some of the upper case Greek symbols aren't
   // actually defined in the LaTeX kernel, but
   // they're added here for completeness.
   public static final Object[][] GREEK_SYMBOLS = 
   {
      new Object[]{"Alpha", new Integer(0x1D6E2)},
      new Object[]{"Beta", new Integer(0x1D6E3)},
      new Object[]{"Gamma", new Integer(0x1D6E4)},
      new Object[]{"Delta", new Integer(0x1D6E5)},
      new Object[]{"Epsilon", new Integer(0x1D6E6)},
      new Object[]{"Zeta", new Integer(0x1D6E7)},
      new Object[]{"Eta", new Integer(0x1D6E8)},
      new Object[]{"Theta", new Integer(0x1D6E9)},
      new Object[]{"Iota", new Integer(0x1D6EA)},
      new Object[]{"Kappa", new Integer(0x1D6EB)},
      new Object[]{"Lambda", new Integer(0x1D6EC)},
      new Object[]{"Mu", new Integer(0x1D6ED)},
      new Object[]{"Nu", new Integer(0x1D6EE)},
      new Object[]{"Xi", new Integer(0x1D6EF)},
      new Object[]{"Omicron", new Integer(0x1D6F0)},
      new Object[]{"Pi", new Integer(0x1D6F1)},
      new Object[]{"Rho", new Integer(0x1D6F2)},
      new Object[]{"Theta", new Integer(0x1D6F3)},
      new Object[]{"Sigma", new Integer(0x1D6F4)},
      new Object[]{"Tau", new Integer(0x1D6F5)},
      new Object[]{"Upsilon", new Integer(0x1D6F6)},
      new Object[]{"Phi", new Integer(0x1D6F7)},
      new Object[]{"Chi", new Integer(0x1D6F8)},
      new Object[]{"Psi", new Integer(0x1D6F9)},
      new Object[]{"Omega", new Integer(0x1D6FA)},
      new Object[]{"nabla", new Integer(0x1D6FB)},
      new Object[]{"alpha", new Integer(0x1D6FC)},
      new Object[]{"beta", new Integer(0x1D6FD)},
      new Object[]{"gamma", new Integer(0x1D6FE)},
      new Object[]{"delta", new Integer(0x1D6FF)},
      new Object[]{"varepsilon", new Integer(0x1D700)},
      new Object[]{"zeta", new Integer(0x1D701)},
      new Object[]{"eta", new Integer(0x1D702)},
      new Object[]{"theta", new Integer(0x1D703)},
      new Object[]{"iota", new Integer(0x1D704)},
      new Object[]{"kappa", new Integer(0x1D705)},
      new Object[]{"lambda", new Integer(0x1D706)},
      new Object[]{"mu", new Integer(0x1D707)},
      new Object[]{"nu", new Integer(0x1D708)},
      new Object[]{"xi", new Integer(0x1D709)},
      new Object[]{"omicron", new Integer(0x1D70A)},
      new Object[]{"pi", new Integer(0x1D70B)},
      new Object[]{"rho", new Integer(0x1D70C)},
      new Object[]{"varsigma", new Integer(0x1D70D)},
      new Object[]{"sigma", new Integer(0x1D70E)},
      new Object[]{"tau", new Integer(0x1D70F)},
      new Object[]{"upsilon", new Integer(0x1D710)},
      new Object[]{"varphi", new Integer(0x1D711)},
      new Object[]{"chi", new Integer(0x1D712)},
      new Object[]{"psi", new Integer(0x1D713)},
      new Object[]{"omega", new Integer(0x1D714)},
      new Object[]{"epsilon", new Integer(0x1D716)},
      new Object[]{"vartheta", new Integer(0x1D717)},
      new Object[]{"varkappa", new Integer(0x1D718)},
      new Object[]{"phi", new Integer(0x1D719)},
      new Object[]{"varrho", new Integer(0x1D71A)},
      new Object[]{"varpi", new Integer(0x1D71B)},
   };

   public static final Object[][] MATH_SYMBOLS = 
   {
      new Object[]{"digamma", new Integer(0x03DD)},
      new Object[]{"Digamma", new Integer(0x03DC)},
      new Object[]{"forall", new Integer(0x2200)},
      new Object[]{"complement", new Integer(0x2201)},
      new Object[]{"partial", new Integer(0x2202)},
      new Object[]{"varpartialdiff", new Integer(0x1D715)},
      new Object[]{"exists", new Integer(0x2203)},
      new Object[]{"nexists", new Integer(0x2204)},
      new Object[]{"varnothing", new Integer(0x2205)},
      new Object[]{"in", new Integer(0x2208)},
      new Object[]{"notin", new Integer(0x2209)},
      new Object[]{"ni", new Integer(0x220B)},
      new Object[]{"owns", new Integer(0x220B)},
      new Object[]{"notni", new Integer(0x220C)},
      new Object[]{"vdots", new Integer(0x22EE)},
      new Object[]{"cdots", new Integer(0x22EF)},
      new Object[]{"ddots", new Integer(0x22F1)},
      new Object[]{"surd", new Integer(0x221A)},
      new Object[]{"infty", new Integer(0x221E)},
      new Object[]{"rightangle", new Integer(0x221F)},
      new Object[]{"angle", new Integer(0x2220)},
      new Object[]{"measuredangle", new Integer(0x2221)},
      new Object[]{"sphericalangle", new Integer(0x2222)},
      new Object[]{"mathdollar", new Integer(0x0024)},
      new Object[]{"mathellipsis", new Integer(0x2026)},
      new Object[]{"mathparagraph", new Integer(0x00B6)},
      new Object[]{"mathsection", new Integer(0x00A7)},
      new Object[]{"mathsterling", new Integer(0x00A3)},
      new Object[]{"mathunderscore", new Integer(0x005F)},
      new Object[]{"bot", new Integer(0x22A5)},
      new Object[]{"top", new Integer(0x22A4)},
      new Object[]{"Im", new Integer(0x2111)},
      new Object[]{"ell", new Integer(0x2113)},
      new Object[]{"Re", new Integer(0x211C)},
      new Object[]{"ohm", new Integer(0x2126)},
      new Object[]{"mho", new Integer(0x2127)},
      new Object[]{"mathring", new Integer(0x212B)},
      new Object[]{"aleph", new Integer(0x2135)},
      new Object[]{"beth", new Integer(0x2136)},
      new Object[]{"gimel", new Integer(0x2137)},
      new Object[]{"daleth", new Integer(0x2138)},
      new Object[]{"hbar", new Integer(0x210F)},
      new Object[]{"wp", new Integer(0x2118)},
      new Object[]{"Bbbk", new Integer(0x1D55C)},
      new Object[]{"game", new Integer(0x2141)},
      new Object[]{"Finv", new Integer(0x2132)},
      new Object[]{"imath", new Integer(0x1D6A4)},
      new Object[]{"jmath", new Integer(0x1D6A5)},
      new Object[]{"emptyset", new Integer(0x2205)},
      new Object[]{"prime", new Integer(0x2032)},
      new Object[]{"hbar", new Integer(0x210F)},
      new Object[]{"triangle", new Integer(0x25B3)},
      new Object[]{"neg", new Integer(0x00AC)},
      new Object[]{"flat", new Integer(0x266D)},
      new Object[]{"natural", new Integer(0x266E)},
      new Object[]{"sharp", new Integer(0x266F)},
      new Object[]{"clubsuit", new Integer(0x2663)},
      new Object[]{"diamondsuit", new Integer(0x2662)},
      new Object[]{"heartsuit", new Integer(0x2661)},
      new Object[]{"spadesuit", new Integer(0x2660)},
      new Object[]{"ldotp", new Integer(0x002E)},
      new Object[]{"colon", new Integer(0x2236)},
      new Object[]{"cdotp", new Integer(0x22C5)},
   };

   public static final Object[][] BINARY_MATH_SYMBOLS = 
   {
      new Object[]{"le", new Integer(0x2264)},
      new Object[]{"leq", new Integer(0x2264)},
      new Object[]{"ge", new Integer(0x2265)},
      new Object[]{"geq", new Integer(0x2265)},
      new Object[]{"ll", new Integer(0x226A)},
      new Object[]{"gg", new Integer(0x226B)},
      new Object[]{"neq", new Integer(0x2260)},
      new Object[]{"ne", new Integer(0x2260)},
      new Object[]{"amalg", new Integer(0x2A3F)},
      new Object[]{"approx", new Integer(0x2248)},
      new Object[]{"approxeq", new Integer(0x224A)},
      new Object[]{"ast", new Integer(0x2217)},
      new Object[]{"asymp", new Integer(0x224D)},
      new Object[]{"backsim", new Integer(0x223D)},
      new Object[]{"because", new Integer(0x2235)},
      new Object[]{"between", new Integer(0x226C)},
      new Object[]{"bigcirc", new Integer(0x25CB)},
      new Object[]{"bigtriangledown", new Integer(0x25BF)},
      new Object[]{"bigtriangleup", new Integer(0x25B5)},
      new Object[]{"bowtie", new Integer(0x22C8)},
      new Object[]{"bullet", new Integer(0x2022)},
      new Object[]{"bumpeq", new Integer(0x224F)},
      new Object[]{"cap", new Integer(0x2229)},
      new Object[]{"cdot", new Integer(0x2219)},
      new Object[]{"cong", new Integer(0x2245)},
      new Object[]{"circ", new Integer(0x2218)},
      new Object[]{"cup", new Integer(0x222A)},
      new Object[]{"dagger", new Integer(0x2020)},
      new Object[]{"dashv", new Integer(0x22A3)},
      new Object[]{"ddagger", new Integer(0x2021)},
      new Object[]{"diamond", new Integer(0x2B26)},
      new Object[]{"div", new Integer(0x00F7)},
      new Object[]{"doteqdot", new Integer(0x2251)},
      new Object[]{"fallingdotseq", new Integer(0x2252)},
      new Object[]{"lhd", new Integer(0x22B2)},
      new Object[]{"mp", new Integer(0x2213)},
      new Object[]{"ncong", new Integer(0x2247)},
      new Object[]{"notcong", new Integer(0x2247)},
      new Object[]{"nmid", new Integer(0x2224)},
      new Object[]{"notmid", new Integer(0x2224)},
      new Object[]{"nprec", new Integer(0x2280)},
      new Object[]{"notprec", new Integer(0x2280)},
      new Object[]{"npreceq", new Integer(0x22E0)},
      new Object[]{"notpreceq", new Integer(0x22E0)},
      new Object[]{"nsucc", new Integer(0x2281)},
      new Object[]{"notsucc", new Integer(0x2281)},
      new Object[]{"nsucceq", new Integer(0x22E1)},
      new Object[]{"notsucceq", new Integer(0x22E1)},
      new Object[]{"odot", new Integer(0x2299)},
      new Object[]{"ominus", new Integer(0x2296)},
      new Object[]{"oplus", new Integer(0x2295)},
      new Object[]{"oslash", new Integer(0x2298)},
      new Object[]{"otimes", new Integer(0x2297)},
      new Object[]{"pm", new Integer(0x00B1)},
      new Object[]{"rhd", new Integer(0x22B3)},
      new Object[]{"setminus", new Integer(0x2216)},
      new Object[]{"sqcap", new Integer(0x2293)},
      new Object[]{"sqcup", new Integer(0x2294)},
      new Object[]{"star", new Integer(0x22C6)},
      new Object[]{"times", new Integer(0x00D7)},
      new Object[]{"triangleleft", new Integer(0x25C3)},
      new Object[]{"triangleright", new Integer(0x25B7)},
      new Object[]{"unlhd", new Integer(0x22B4)},
      new Object[]{"unrhd", new Integer(0x22B5)},
      new Object[]{"uplus", new Integer(0x228E)},
      new Object[]{"vee", new Integer(0x2228)},
      new Object[]{"wedge", new Integer(0x2227)},
      new Object[]{"wr", new Integer(0x2240)},
      new Object[]{"wreath", new Integer(0x2240)},
      new Object[]{"boxdot", new Integer(0x22A1)},
      new Object[]{"boxminus", new Integer(0x229F)},
      new Object[]{"boxplus", new Integer(0x229E)},
      new Object[]{"boxtimes", new Integer(0x22A0)},
      new Object[]{"Cap", new Integer(0x22D2)},
      new Object[]{"centerdot", new Integer(0x22C5)},
      new Object[]{"circledast", new Integer(0x229B)},
      new Object[]{"circledcirc", new Integer(0x229A)},
      new Object[]{"circleddash", new Integer(0x229D)},
      new Object[]{"Cup", new Integer(0x22D3)},
      new Object[]{"curlyvee", new Integer(0x22CE)},
      new Object[]{"curlywedge", new Integer(0x22CF)},
      new Object[]{"divideontimes", new Integer(0x22C7)},
      new Object[]{"dotequal", new Integer(0x2250)},
      new Object[]{"dotplus", new Integer(0x2214)},
      new Object[]{"doublebarwedge", new Integer(0x2A5E)},
      new Object[]{"equiv", new Integer(0x2261)},
      new Object[]{"frown", new Integer(0x2054)},// not sure about this one
      new Object[]{"intercal", new Integer(0x22BA)},
      new Object[]{"Join", new Integer(0x2A1D)},
      new Object[]{"leftthreetimes", new Integer(0x22CB)},
      new Object[]{"ltimes", new Integer(0x22C9)},
      new Object[]{"mid", new Integer(0x2223)},
      new Object[]{"models", new Integer(0x22A7)},
      new Object[]{"nparallel", new Integer(0x2226)},
      new Object[]{"notparallel", new Integer(0x2226)},
      new Object[]{"nvdash", new Integer(0x22AC)},
      new Object[]{"notvdash", new Integer(0x22AC)},
      new Object[]{"nvDash", new Integer(0x22AD)},
      new Object[]{"notvDash", new Integer(0x22AD)},
      new Object[]{"nVDash", new Integer(0x22AF)},
      new Object[]{"notVDash", new Integer(0x22AF)},
      new Object[]{"parallel", new Integer(0x2225)},
      new Object[]{"perp", new Integer(0x22A5)},
      new Object[]{"prec", new Integer(0x227A)},
      new Object[]{"preceq", new Integer(0x227C)},
      new Object[]{"propto", new Integer(0x221D)},
      new Object[]{"sqsubseteq", new Integer(0x2291)},
      new Object[]{"sqsupseteq", new Integer(0x2292)},
      new Object[]{"rightthreetimes", new Integer(0x22CC)},
      new Object[]{"rtimes", new Integer(0x22CA)},
      new Object[]{"sim", new Integer(0x223C)},
      new Object[]{"simeq", new Integer(0x2243)},
      new Object[]{"smallsetminus", new Integer(0x29F5)},
      new Object[]{"smile", new Integer(0x203F)},// not sure about this one
      new Object[]{"succ", new Integer(0x227B)},
      new Object[]{"succeq", new Integer(0x227D)},
      new Object[]{"therefore", new Integer(0x2234)},
      new Object[]{"veebar", new Integer(0x2A61)},
      new Object[]{"plus", new Integer(0x002B)},
      new Object[]{"minus", new Integer(0x2212)},
      new Object[]{"udtimes", new Integer(0x29D6)},
      new Object[]{"vdash", new Integer(0x22A2)},
      new Object[]{"Vdash", new Integer(0x22A9)},
      new Object[]{"vDash", new Integer(0x22A8)},
      new Object[]{"Vvdash", new Integer(0x22AA)},
      new Object[]{"vcentcolon", new Integer(0x2236)},
      new Object[]{"squaredots", new Integer(0x2237)},
      new Object[]{"dotminus", new Integer(0x2238)},
      new Object[]{"eqcolon", new Integer(0x2239)},
      new Object[]{"risingdotseq", new Integer(0x2253)},
      new Object[]{"eqqcolon", new Integer(0x2255)},
      new Object[]{"nearrow", new Integer(0x2197)},
      new Object[]{"searrow", new Integer(0x2198)},
      new Object[]{"nwarrow", new Integer(0x2196)},
      new Object[]{"swarrow", new Integer(0x2199)},
      new Object[]{"Leftrightarrow", new Integer(0x21D4)},
      new Object[]{"Leftarrow", new Integer(0x21D0)},
      new Object[]{"Rightarrow", new Integer(0x21D2)},
      new Object[]{"supset", new Integer(0x2283)},
      new Object[]{"subset", new Integer(0x2282)},
      new Object[]{"supseteq", new Integer(0x2287)},
      new Object[]{"subseteq", new Integer(0x2286)},
      new Object[]{"not", new Integer(0x2215)},// not sure about this one
      new Object[]{"leftrightarrow", new Integer(0x2194)},
      new Object[]{"leftarrow", new Integer(0x2190)},
      new Object[]{"gets", new Integer(0x2190)},
      new Object[]{"rightarrow", new Integer(0x2192)},
      new Object[]{"to", new Integer(0x2192)},
      new Object[]{"mapsto", new Integer(0x21A6)},
      new Object[]{"leftharpoonup", new Integer(0x21BC)},
      new Object[]{"leftharpoondown", new Integer(0x21BD)},
      new Object[]{"rightharpoonup", new Integer(0x21C0)},
      new Object[]{"rightharpoondown", new Integer(0x21C1)},
      new Object[]{"rightleftharpoons", new Integer(0x21CC)},
      new Object[]{"doteq", new Integer(0x2250)},
      new Object[]{"hookrightarrow", new Integer(0x21AA)},
      new Object[]{"hookleftarrow", new Integer(0x21A9)},
      new Object[]{"Longrightarrow", new Integer(0x27F9)},
      new Object[]{"longrightarrow", new Integer(0x27F6)},
      new Object[]{"longleftarrow", new Integer(0x27F5)},
      new Object[]{"Longleftarrow", new Integer(0x27F8)},
      new Object[]{"longmapsto", new Integer(0x27FC)},
      new Object[]{"longleftrightarrow", new Integer(0x27F7)},
      new Object[]{"Longleftrightarrow", new Integer(0x27FA)},
      new Object[]{"iff", new Integer(0x27FA)},// this should actually have \; spacing on either side
   };

   protected int codePoint;
}
