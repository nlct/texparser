/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

/**
 * Token that's usually embedded within a MultiToken object.
 */

public class SpecialToken extends AbstractTeXObject implements SingleToken
{
   public SpecialToken(MultiToken object, int charCode, int catCode)
   {
      this.object = object;
      this.charCode = charCode;
      this.catCode = catCode;
   }

   @Override
   public Object clone()
   {
      return new SpecialToken((MultiToken)object.clone(), charCode, catCode);
   }

   public MultiToken getObject()
   {
      return object;
   }

   @Override
   public int getCharCode()
   {
      return charCode;
   }

   @Override
   public int getCatCode()
   {
      return catCode;
   }

   @Override
   public boolean isSingleToken()
   {
      return true;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      object.reconstitute(parser, stack).process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      object.reconstitute(parser, parser).process(parser);
   }

   @Override
   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   @Override
   public String toString(TeXParser parser)
   {
      return format();
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return TeXParserUtils.createStack(parser, 
         parser.getListener().getOther(charCode));  
   }

   @Override
   public String toString()
   {
      return String.format("%s[charcode=%d,catcode=%d,object=%s]",
        getClass().getSimpleName(),
        charCode, catCode, object.toString());
   }

   protected MultiToken object;
   protected int charCode, catCode;
}

