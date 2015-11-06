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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class L2HLetter extends Letter
{
   public L2HLetter(int charCode)
   {
      super(charCode);
   }

   public L2HLetter(char c)
   {
      super(c);
   }

   public Object clone()
   {
      return new L2HLetter(getCharCode());
   }

   public String toString(TeXParser parser)
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      int code = parser.getSettings().getCharCode(getCharCode());

      if (code == -1)
      {
         code = getCharCode();
      }

      if (code >= 32 && code <= 126)
      {
         return format();
      }

      return "&#x"+Integer.toHexString(code)+";";
   }

   public void process(TeXParser parser)
      throws IOException
   {
      if (((L2HConverter)parser.getListener()).isInDocEnv())
      {
         super.process(parser);
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      if (((L2HConverter)parser.getListener()).isInDocEnv())
      {
         super.process(parser);
      }
   }
}
