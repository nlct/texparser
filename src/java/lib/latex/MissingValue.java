/*
    Copyright (C) 2018-2022 Nicola L.C. Talbot
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

public class MissingValue extends AbstractTeXObject implements Expandable
{
   public MissingValue()
   {
      super();
   }

   @Override
   public Object clone()
   {
      return new MissingValue();
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return new TeXObjectList();
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
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
   public String format()
   {
      return "";
   }

}
