/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.Begin;
import com.dickimawbooks.texparserlib.latex.End;

public class HangIndent extends Primitive implements Expandable,InternalQuantity
{
   public HangIndent()
   {
      this("hangindent");
   }

   public HangIndent(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new HangIndent(getName());
   }

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXDimension dim = parser.getSettings().getCurrentHangIndent();

      if (dim == null)
      {
         return new UserDimension();
      }
      else
      {
         return dim;
      }
   }

   @Override
   public void setQuantity(TeXParser parser, TeXObject quantity)
    throws TeXSyntaxException
   {
      if (!(quantity instanceof TeXDimension))
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_DIMEN_EXPECTED);
      }

      parser.getSettings().setHangIndent((TeXDimension)quantity);
   }

   /**
    * This will fully expand because the paragraph break may be
    * inside the definition of a command.
    */ 
   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      popModifier(parser, stack, '=');
      TeXDimension dim = stack.popDimension(parser);
      parser.getSettings().setHangIndent(dim);

      Paragraph par = parser.getListener().createParagraph();
      par.setLeftMargin(dim);

      par.build(parser, stack);

      parser.getSettings().setHangIndent(null);

      TeXObjectList list = parser.getListener().createStack();
      list.add(par);
      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      popModifier(parser, parser, '=');
      TeXDimension dim = parser.popDimension();
      parser.getSettings().setHangIndent(dim);

      Paragraph par = parser.getListener().createParagraph();
      par.setLeftMargin(dim);

      par.build(parser, parser);

      parser.getSettings().setHangIndent(null);

      TeXObjectList list = parser.getListener().createStack();
      list.add(par);
      return list;
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
      return expandonce(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      popModifier(parser, stack, '=');
      TeXDimension dim = stack.popDimension(parser);
      parser.getSettings().setHangIndent(dim);

      Paragraph par = parser.getListener().createParagraph();
      par.setLeftMargin(dim);

      par.build(parser, stack);

      parser.getSettings().setHangIndent(null);
      stack.push(par);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      popModifier(parser, parser, '=');
      TeXDimension dim = parser.popDimension();
      parser.getSettings().setHangIndent(dim);

      Paragraph par = parser.getListener().createParagraph();
      par.setLeftMargin(dim);

      par.build(parser, parser);

      parser.getSettings().setHangIndent(null);
      parser.push(par);
   }
}
