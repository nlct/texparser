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

import com.dickimawbooks.texparserlib.*;

public class BigOperator extends MathSymbol
{
   public BigOperator(String name, int codePoint)
   {
      this(name, codePoint, codePoint);
   }

   public BigOperator(String name, int inLineCodePoint,
     int dispCodePoint)
   {
      super(name, inLineCodePoint);
      this.dispCodePoint = dispCodePoint;
   }

   public Object clone()
   {
      return new BigOperator(getName(), getCharCode(), dispCodePoint);
   }

   public int getDispCharCode()
   {
      return dispCodePoint;
   }

   public void write(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      int cp;

      switch (settings.getMode())
      {
         case TeXSettings.MODE_INLINE_MATH:
            cp = getCharCode();
         break;
         case TeXSettings.MODE_DISPLAY_MATH:
            cp = dispCodePoint;
         break;
         default:
            throw new TeXSyntaxException(
              parser,
              TeXSyntaxException.ERROR_NOT_MATH_MODE,
              toString(parser));
      }

      TeXParserListener listener = parser.getListener();

      int c = settings.getCharCode(cp);

      listener.getWriteable().writeCodePoint(c == -1 ? cp : c);
   }

   protected int dispCodePoint;
}
