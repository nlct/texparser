/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public class StringCs extends Primitive implements Expandable
{
   public StringCs()
   {
      this("string");
   }

   public StringCs(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new StringCs(getName());
   }


   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      return parser.popToken().string(parser);
   }

   private TeXObjectList string(TeXParser parser, AbstractTeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = stack.deconstruct(parser);

      TeXObject object = list.popToken();

      parser.addAll(0, list);
      stack.clear();

      return object.string(parser);
   } 

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return string(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return string(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.addAll(0, string(parser, stack));
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      parser.addAll(0, string(parser));
   }
}
