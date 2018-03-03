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
         case '0': return 0x0289;
         case '1': return 0x0268;
         case '2': return 0x028C;
         case '3': return 0x025C;
         case '4': return 0x0265;
         case '5': return 0x0250;
         case '6': return 0x0252;
         case '7': return 0x0264;
         case '8': return 0x0275;
         case '9': return 0x0258;
         case '@': return 0x0259;
         case 'A' : return 0x0251;
         case 'B' : return 0x03B2;
         case 'C' : return 0x0255;
         case 'D' : return 0x00F0;
         case 'E' : return 0x025B;
         case 'F' : return 0x0278;
         case 'G' : return 0x0263;
         case 'H' : return 0x0266;
         case 'I' : return 0x026A;
         case 'J' : return 0x029D;
         case 'K' : return 0x0281;
         case 'L' : return 0x028E;
         case 'M' : return 0x0271;
         case 'N' : return 0x014B;
         case 'O' : return 0x0254;
         case 'P' : return 0x0294;
         case 'Q' : return 0x0295;
         case 'R' : return 0x027E;
         case 'S' : return 0x0283;
         case 'T' : return 0x03B8;
         case 'U' : return 0x028A;
         case 'V' : return 0x028B;
         case 'W' : return 0x026F;
         case 'X' : return 0x03C7;
         case 'Y' : return 0x028F;
         case 'Z' : return 0x021D;
         case 'g' : return 0x0262;
         case '"' : return 0x02C8;
         case ':' : return 0x02D0;
         case ';' : return 0x02D1;
         case 224 : return 0x0299;
         case 225 : return 0x0253;
         case 226 : return 0x0257;
         case 227 : return 0x0256;
         case 228 : return 0x0260;
         case 229 : return 0x0262;
         //case 230 : return 0x00E6;//\ae
         //case 231 : return 0x00E7;//\c{c}
         case 232 : return 0x0127;
         case 233 : return 0x025F;
         case 234 : return 0x0284;
         case 235 : return 0x026B;
         case 236 : return 0x026C;
         case 237 : return 0x026D;
         case 238 : return 0x0270;
         case 239 : return 0x0273;
         case 240 : return 0x0274;
         case 241 : return 0x0272;
         case 242 : return 0x0298;
         case 243 : return 0x027D;
         case 244 : return 0x0279;
         case 245 : return 0x027B;
         case 246 : return 0x0280;
         //case 247 : return 247;//\oe
         //case 248 : return 248;//\o
         case 249 : return 0x0282;
         case 250 : return 0x0288;
         case 251 : return 0x028D;
         case 252 : return 0x0290;
         case 253 : return 0x0291;
         //case 254 : return 0x00FE;//\th
         case 255 : return 0x0195;
         case 192 : return 0x1D00;
         case 193 : return 0x0188;
         case 194 : return 0x0297;
         case 195 : return 0x02A4;
         case 196 : return 0x025A;
         case 197 : return 0x029A;
         case 198 : return 0x025E;
         case 199 : return 0x025D;
         case 200 : return 0x0264;
         case 201 : return 0x029B;
         case 202 : return 0x0267;
         case 203 : return 0x029C;
         case 204 : return 0x0269;
         case 205 : return 0x025F;
         case 206 : return 0x0199;
         case 207 : return 0x029F;
         case 208 : return 0x026E;
         case 209 : return 0x0277;
         case 210 : return 0x01A5;
         case 211 : return 0x02A0;
         case 212 : return 0x027C;
         case 213 : return 0x027A;
         case 214 : return 0x01AD;
         case 215 : return 0x0276;
         case 216 : return 0x0287;
         case 217 : return 0x02A7;
         case 218 : return 0x1D1C;
         case 219 : return 0x0296;
         case 220 : return 0x02A1;
         case 221 : return 0x02A2;
         case 222 : return 0x0225;
         case 223 : return 0x01BF;
         case 160 : return 0x0180;
         case 161 : return 0x0111;
         case 162 : return 0x0221;
         case 163 : return 0x1D91;
         case 164 : return 0x1D07;
         case 165 : return 0x0067;
         case 166 : return 0x1EC9;//?? left-hooktop long I
         case 167 : return 0x0285;
         case 168 : return 0x1D0A;
         case 169 : return 0x029E;
         case 170 : return 0x019A;
         case 171 : return 0x03BB;
         case 172 : return 0x019B;
         case 173 : return CHAR_MAP_COMPOUND;// L-Yogh ligature
         case 174 : return 0x0235;
         case 175 : return CHAR_MAP_COMPOUND;// turned C-E ligature;
         case 176 : return 0x03C9;
         case 177 : return 0xAB65;
         case 178 : return 0x0286;
         case 179 : return 0x01AB;
         case 180 : return 0x0236;
         case 181 : return 0x02A6;
         case 182 : return 0x02AE;
         case 183 : return 0x02AF;
         case 184 : return 0x0293;//? curly-tail yogh
         case 185 : return 0x01B9;//? reversed yogh
         case 186 : return 0x042C;
         case 187 : return 0x042A;
         case 188 : return 0x02C0;
         case 189 : return 0x02C2;
         case 190 : return 0x02C3;
         case 191 : return '|';// ? tone letter stem - closest match is a bar
         case 32: return 0x02CA;// ? Celtic Palatalization Mark
         //case 35: ??// ? Hooktop
         //case 36: ??// ? Right Hook
         //case 37: ??// ? Palatalization Hook
         case 92: return 0x02BD;
         case 94: return 0x02FA;
         case 95: return 0x02F9;
         case 123: return 0x01C1;// ??
         case 125: return 0x01C2;
         case 126: return 0x02DE;
         case 127: return 0x02CC;
         case 146: return 0x01C0;
         case 147: return 0x01C1;// ??
         case 148: return 0x2193;// ??
         case 149: return 0x2191;// ??
         case 150: return 0x2197;// ??
         case 151: return 0x2198;// ??
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
