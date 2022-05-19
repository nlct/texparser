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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class AtNumberOfNumber extends Command
{
   public AtNumberOfNumber(String name, int number, int total)
   {
      super(name);

      if (number < 1 || number > total || total < 1)
      {
         throw new IllegalArgumentException(
         String.format("Invalid %d of %d", number, total));
      }

      this.number = number;
      this.total = total;
   }

   public Object clone()
   {
      return new AtNumberOfNumber(getName(), number, total);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      for (int i = 1; i <= total; i++)
      {
         TeXObject arg = popArg(parser, stack);

         if (i == number)
         {
            list.add(arg);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = null;

      for (int i = 1; i <= total; i++)
      {
         TeXObject obj = popArg(parser, stack);

         if (i == number)
         {
            arg = obj;
         }
      }

      stack.push(arg);
   }

   private int number;
   private int total;
}
