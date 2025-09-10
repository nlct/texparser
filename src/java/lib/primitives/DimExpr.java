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

public class DimExpr extends Primitive implements Expandable
{
   public DimExpr()
   {
      this("dimexpr");
   }

   public DimExpr(String name)
   {
      super(name, true);
   }

   @Override
   public Object clone()
   {
      return new DimExpr(getName());
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
      TeXDimension dim;

      if (stack == null || stack == parser)
      {
         dim = parser.popDimExpr(parser);
      }
      else
      {
         dim = stack.popDimExpr(parser);
      }

      TeXObjectList list = new TeXObjectList();

      list.add(dim);

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
      TeXDimension dim = stack.popDimExpr(parser);
      stack.push(dim);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      TeXDimension dim = parser.popDimExpr(parser);
      parser.push(dim);
   }


}
