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
      }

      return charCode;
   }

}
