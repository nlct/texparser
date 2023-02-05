/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

   @Override
   public Object clone()
   {
      return new SbChar();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return new String(Character.toChars(parser.getSbChar()));
   }

   @Override
   public String format()
   {
      return "_";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(parser.getSbChar()));

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.popArg(parser);

      TeXObject nextObject = stack.peekStack();

      if (nextObject instanceof SbChar)
      {
         stack.push(parser.getListener().createGroup());

         parser.error(new TeXSyntaxException(
           parser, TeXSyntaxException.ERROR_DOUBLE_SUBSCRIPT,
               object.toString(parser)));
      }

      parser.getListener().subscript(object);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject object = parser.popNextArg();

      TeXObject nextObject = parser.peekStack();

      if (nextObject instanceof SbChar)
      {
         parser.push(parser.getListener().createGroup());

         parser.error(new TeXSyntaxException(
           parser, TeXSyntaxException.ERROR_DOUBLE_SUBSCRIPT,
               object.toString(parser)));
      }

      parser.getListener().subscript(object);
   }

   public String show(TeXParser parser)
    throws IOException
   {
      return String.format("subscript character %s", 
       new String(Character.toChars(parser.getSbChar())));
   }
}

