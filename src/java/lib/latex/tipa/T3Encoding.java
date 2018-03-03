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

import com.dickimawbooks.texparserlib.FontEncoding;

public class T3Encoding extends FontEncoding
{
   public T3Encoding()
   {
      super("T3");
   }

   public int getCharCode(int charCode)
   {
      switch (charCode)
      {
         case '0': return 0x0289;// \textbaru
         case '1': return 0x0268;// \textbari
         case '2': return 0x028C;// \textturnv
         case '3': return 0x025C;// \textrevepsilon
         case '4': return 0x0265;// \textturnh
         case '5': return 0x0250;// \textturna
         case '6': return 0x0252;// \textturnscripta
         case '7': return 0x0264;// \textramshorns
         case '8': return 0x0275;// \textbaro
         case '9': return 0x0258;// \textreve
         case '@': return 0x0259;// \textschwa
         case 'A' : return 0x0251;// \textscripta
         case 'B' : return 0x03B2;// \textbeta
         case 'C' : return 0x0255;// \textctc
         case 'D' : return 0x00F0;// \dh
         case 'E' : return 0x025B;// \textepsilon
         case 'F' : return 0x0278;// \textphi
         case 'G' : return 0x0263;// \textgamma
         case 'H' : return 0x0266;// \texthth
         case 'I' : return 0x026A;// \textsci
         case 'J' : return 0x029D;// \textctj
         case 'K' : return 0x0281;// \textinvscr
         case 'L' : return 0x028E;// \textturny
         case 'M' : return 0x0271;// \texttailm
         case 'N' : return 0x014B;// \ng
         case 'O' : return 0x0254;// \textopeno
         case 'P' : return 0x0294;// \textglotstop
         case 'Q' : return 0x0295;// \textrevglotstop
         case 'R' : return 0x027E;// \textfishhookr
         case 'S' : return 0x0283;// \textesh
         case 'T' : return 0x03B8;// \texttheta
         case 'U' : return 0x028A;// \textupsilon
         case 'V' : return 0x028B;// \textscriptv
         case 'W' : return 0x026F;// \textturnm
         case 'X' : return 0x03C7;// \textchi
         case 'Y' : return 0x028F;// \textscy
         case 'Z' : return 0x021D;// \textyogh
         case 'g' : return 0x0262;// \textscg
         case '"' : return 0x02C8;// \textprimstress
         case ':' : return 0x02D0;// \textlengthmark
         case ';' : return 0x02D1;// \texthalflength
         case 224 : return 0x0299;// \textscb
         case 225 : return 0x0253;// \texthtb
         case 226 : return 0x0257;// \texthtd
         case 227 : return 0x0256;// \textrtaild
         case 228 : return 0x0260;// \texthtg
         case 229 : return 0x0262;// \textscg
         case 230 : return 0x00E6;// \ae (equiv)
         case 231 : return 0x00E7;// \c{c} (equiv)
         case 232 : return 0x0127;// \textcrh
         case 233 : return 0x025F;// \textbardotlessj
         case 234 : return 0x0284;// \texthtbardotlessj
         case 235 : return 0x026B;// \textltilde
         case 236 : return 0x026C;// \textbeltl
         case 237 : return 0x026D;// \textrtaill
         case 238 : return 0x0270;// \textturnmrleg
         case 239 : return 0x0273;// \textrtailn
         case 240 : return 0x0274;// \textscn
         case 241 : return 0x0272;// \textltailn
         case 242 : return 0x0298;// \textbullseye
         case 243 : return 0x027D;// \textrtailr
         case 244 : return 0x0279;// \textturnr
         case 245 : return 0x027B;// \textturnrrtail
         case 246 : return 0x0280;// \textscr
         case 247 : return 247;//\oe (equiv)
         case 248 : return 248;//\o (equiv)
         case 249 : return 0x0282;// \textrtails
         case 250 : return 0x0288;// \textrtailt
         case 251 : return 0x028D;// \textturnw
         case 252 : return 0x0290;// \textrtailz
         case 253 : return 0x0291;// \textctz
         case 254 : return 0x00FE;//\th (equiv)
         case 255 : return 0x0195;// \texthvlig
         case 192 : return 0x1D00;// \textsca
         case 193 : return 0x0188;// \texthtc
         case 194 : return 0x0297;// \textstretchc
         case 195 : return 0x02A4;// \textdyoghlig
         case 196 : return 0x025A;// \textrhookschwa
         case 197 : return 0x029A;// \textcloseepsilon
         case 198 : return 0x025E;// \textcloserevepsilon
         case 199 : return 0x025D;// \textrhookrevepsilon
         case 200 : return 0x0264;// \textbabygamma
         case 201 : return 0x029B;// \texthtscg
         case 202 : return 0x0267;// \texththeng
         case 203 : return 0x029C;// \textsch
         case 204 : return 0x0269;// \textiota
         case 205 : return 0x025F;// \textObardotlessj
         case 206 : return 0x0199;// \texthtk
         case 207 : return 0x029F;// \textscl
         case 208 : return 0x026E;// \textlyoghlig
         case 209 : return 0x0277;// \textcloseomega
         case 210 : return 0x01A5;// \texthtp
         case 211 : return 0x02A0;// \texthtq
         case 212 : return 0x027C;// \textlonglegr
         case 213 : return 0x027A;// \textturnlonglegr
         case 214 : return 0x01AD;// \texthtt
         case 215 : return 0x0276;// \textscoelig
         case 216 : return 0x0287;// \textturnt
         case 217 : return 0x02A7;// \textteshlig
         case 218 : return 0x1D1C;// \textscu
         case 219 : return 0x0296;// \textinvglotstop
         case 220 : return 0x02A1;// \textbarglotstop
         case 221 : return 0x02A2;// \textbarrevglotstop
         case 222 : return 0x0225;// \textcommatailz
         case 223 : return 0x01BF;// \textwynn
         case 160 : return 0x0180;// \textcrb
         case 161 : return 0x0111;// \textcrd
         case 162 : return 0x0221;// \textctd
         case 163 : return 0x1D91;// \texthtrtaild
         case 164 : return 0x1D07;// \textsce
         case 165 : return 0x0067;// \textg
         case 166 : return 0x1EC9;//?? left-hooktop long I \textlhtlongi
         case 167 : return 0x0285;// \textvibyi
         case 168 : return 0x1D0A;// \textscj
         case 169 : return 0x029E;// \textturnk
         case 170 : return 0x019A;// \textbarl
         case 171 : return 0x03BB;// \textlambda
         case 172 : return 0x019B;// \textcrlambda
         case 173 : return CHAR_MAP_COMPOUND;// L-Yogh ligature \textOlyoghlig
         case 174 : return 0x0235;// \textctn
         case 175 : return CHAR_MAP_COMPOUND;// turned C-E ligature \textturncelig
         case 176 : return 0x03C9;// \textomega
         case 177 : return 0xAB65;// \textscomega
         case 178 : return 0x0286;// \textctesh
         case 179 : return 0x01AB;// \textlhookt
         case 180 : return 0x0236;// \textctt
         case 181 : return 0x02A6;// \texttslig
         case 182 : return 0x02AE;// \textlhtlongy
         case 183 : return 0x02AF;// \textvibyy
         case 184 : return 0x0293;//? curly-tail yogh \textctyogh
         case 185 : return 0x01B9;//? reversed yogh \textrevyogh
         case 186 : return 0x042C;// \textsoftsign
         case 187 : return 0x042A;// \texthardsign
         case 188 : return 0x02C0;// \textraiseglotstop
         case 189 : return 0x02C2;// \textlptr
         case 190 : return 0x02C3;// \textrptr
         case 191 : return '|';// ? tone letter stem - closest match is a pipe \texttoneletterstem
         case 32: return 0x02CA;// ? Celtic Palatalization Mark \textceltpal
         //case 35: ??// ? Hooktop \texthooktop
         //case 36: ??// ? Right Hook \textrthook
         //case 37: ??// ? Palatalization Hook \textpalhook
         case 92: return 0x02BD;// \textrevapostrophe
         case 94: return 0x02FA;// \textcorner
         case 95: return 0x02F9;// \textopencorner
         case 123: return 0x01C1;// ?? \textdoublepipe
         case 125: return 0x01C2;// \textdoublebarpipe
         case 126: return 0x02DE;// \textrhoticity
         case 127: return 0x02CC;// \textsecstress
         case 146: return 0x01C0;// \textvertline
         case 147: return 0x01C1;// ?? \textdoublevertline
         case 148: return 0x2193;// ?? \textdownstep
         case 149: return 0x2191;// ?? \textupstep
         case 150: return 0x2197;// ?? \textglobrise
         case 151: return 0x2198;// ?? \textglobfall
      }

      return CHAR_MAP_NONE;
   }

   public String getCharString(int charCode)
   {
      switch (charCode)
      {
         case 173 : return "l\u021D";// L-Yogh ligature
         case 175 : return "\u0254e";// turned C-E ligature
      }

      return super.getCharString(charCode);
   }

}
