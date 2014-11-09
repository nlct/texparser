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

public class SbChar extends Macro
{
   public SbChar()
   {
   }

   public Object clone()
   {
      return new SbChar();
   }

   public String toString(TeXParser parser)
   {
      return ""+parser.getSbChar();
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(new Other((int)parser.getSbChar()));

      return list;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.popArg();

      if (object == null)
      {
         throw new TeXSyntaxException(
            parser.getListenerFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_MISSING_PARAM, ""+parser.getSbChar());
      }

      parser.getListener().subscript(object);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject object = parser.popNextArg();

      parser.getListener().subscript(object);
   }


   public String show(TeXParser parser)
    throws IOException
   {
      return "subscript character "+parser.getSbChar();
   }
}

