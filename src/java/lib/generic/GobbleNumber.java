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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class GobbleNumber extends Command
{
   public GobbleNumber(String name, boolean gobbleAssignment)
   {
      super(name);
      this.gobbleAssignment = gobbleAssignment;
   }

   @Override
   public Object clone()
   {
      return new GobbleNumber(getName(), gobbleAssignment);
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   protected void gobble(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      if (parser == stack || stack == null)
      {
         parser.popNumerical();

         if (gobbleAssignment)
         {
            popModifier(parser, stack, '=');
            parser.popNumerical();
         }
      }
      else
      {
         stack.popNumerical(parser);

         if (gobbleAssignment)
         {
            popModifier(parser, stack, '=');
            stack.popNumerical(parser);
         }
      }
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      gobble(parser, stack);

      return parser.getListener().createStack();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      gobble(parser, parser);

      return parser.getListener().createStack();
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
      gobble(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      gobble(parser, parser);
   }

   protected boolean gobbleAssignment;
}
