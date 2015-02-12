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
package com.dickimawbooks.texparserlib.latex.shortvrb;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class VerbChar extends ActiveChar
{
   public VerbChar(int codePoint)
   {
      this.codePoint = codePoint;
   }

   public VerbChar(char c)
   {
      this((int)c);
   }

   public Object clone()
   {
      return new VerbChar(codePoint);
   }

   public int getCharCode()
   {
      return codePoint;
   }

   public String toString(TeXParser parser)
   {
      return toString();
   }

   public String toString()
   {
      return String.format("%c", codePoint);
   }

   public TeXObjectList expandonce(TeXParser parser)
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObjectList list = parser.popRemainingVerb(codePoint);

      parser.getListener().verb("verb", false, (char)codePoint, 
        list.toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      // This method shouldn't be called as verbatim can't be used
      // in the argument of commands.
   }

   private int codePoint;
}
