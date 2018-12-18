/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class MissingValue implements TeXObject,Expandable
{
   public MissingValue()
   {
      super();
   }

   public Object clone()
   {
      return new MissingValue();
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return new TeXObjectList();
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return new TeXObjectList();
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return new TeXObjectList();
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return new TeXObjectList();
   }

   public void process(TeXParser parser) throws IOException
   {
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
   }

   public String toString(TeXParser parser)
   {
      return "";
   }

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return new TeXObjectList();
   }

   public String format()
   {
      return "";
   }

   public boolean isPar()
   {
      return false;
   }
}
