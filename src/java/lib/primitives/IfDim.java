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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class IfDim extends If
{
   public IfDim()
   {
      this("ifdim");
   }

   public IfDim(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new IfDim(getName());
   }

   public boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXDimension firstArg = stack.popDimension(parser);

      TeXObject comparison = stack.popToken(
        TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (!(comparison instanceof CharObject))
      {
         throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_EXPECTED,
           "<, =, >");
      }

      int charCode = ((CharObject)comparison).getCharCode();

      TeXDimension secondArg = stack.popDimension(parser);

      switch (charCode)
      {
         case '<': return firstArg.getValue() < secondArg.getValue();
         case '>': return firstArg.getValue() > secondArg.getValue();
         case '=': return firstArg.getValue() == secondArg.getValue();
      }

      throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_EXPECTED,
           "<, =, >");

   }

}
