/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public abstract class WhiteSpace implements TeXObject
{
   @Override
   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      process(parser);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      parser.getListener().getWriteable().write(' ');
   }

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(parser.getListener().getOther((int)' '));
      return list;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   @Override
   public String toString()
   {
      return getClass().getName();
   }

   @Override
   public String format()
   {
      return " ";
   }

   public abstract Object clone();
}

