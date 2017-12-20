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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class TokenRegister extends Register
{
   public TokenRegister(String name)
   {
      this(name, new TeXObjectList());
   }

   public TokenRegister(String name, TeXObject contents)
   {
      super(name);
      this.contents = contents;
   }

   public Object clone()
   {
      return new TokenRegister(getName(), (TeXObject)contents.clone());
   }

   public void setContents(TeXParser parser, TeXObject object)
    throws TeXSyntaxException
   {
      contents = object;
   }

   public TeXObject getContents(TeXParser parser)
    throws TeXSyntaxException
   {
      return contents;
   }

   protected TeXObject popValue(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object;

      if (parser == stack)
      {
         object = parser.popNextArg(TeXObjectList.POP_IGNORE_LEADING_SPACE);
      }
      else
      {
         object = stack.popArg(parser, TeXObjectList.POP_IGNORE_LEADING_SPACE);
      }

      return object;
   }

   private TeXObject contents;
}
