/*
    Copyright (C) 2025 Nicola L.C. Talbot
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

public class NumExpr extends Primitive implements Expandable
{
   public NumExpr()
   {
      this("numexpr");
   }

   public NumExpr(String name)
   {
      super(name, true);
   }

   @Override
   public Object clone()
   {
      return new NumExpr(getName());
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
      Numerical num;

      if (stack == null || stack == parser)
      {
         num = parser.popNumExpr(parser);
      }
      else
      {
         num = stack.popNumExpr(parser);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(num);

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Numerical num = stack.popNumExpr(parser);
      stack.push(num);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      Numerical num = parser.popNumExpr(parser);
      parser.push(num);
   }


}
