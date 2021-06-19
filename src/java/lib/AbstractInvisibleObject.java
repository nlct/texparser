/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public abstract class AbstractInvisibleObject implements TeXObject
{
   @Override
   public String format()
   {
      return "";
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
   }

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return false;
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public String stripToString(TeXParser parser)
     throws IOException
   {
      return "";
   }

   @Override
   public boolean isEmptyObject()
   {
      return false;
   }

   @Override
   public int getTeXCategory()
   {
      return TYPE_OBJECT;
   }

   @Override
   public abstract Object clone();
}

