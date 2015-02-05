/*
    Copyright (C) 2013 Nicola L.C. Talbot
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

public class RomanNumeral extends Primitive implements Expandable
{
   public RomanNumeral()
   {
      this("romannumeral");
   }

   public RomanNumeral(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new RomanNumeral(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      TeXObjectList list = new TeXObjectList();

      return parser.getListener().createString(
        romannumeral(arg.number(parser)));
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      Numerical arg = parser.popNumerical();

      return parser.getListener().createString(
         romannumeral(arg.number(parser)));
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      parser.getListener().getWriteable().write(
         romannumeral(arg.number(parser)));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      Numerical arg = parser.popNumerical();

      parser.getListener().getWriteable().write(
         romannumeral(arg.number(parser)));
   }

   public static String romannumeral(int num)
   {
      StringBuilder builder = new StringBuilder();

      int idx = 0;

      while (idx < DECIMAL.length)
      {
         int val = DECIMAL[idx];
         String rom = ROMAN[idx];

         if (num < val)
         {
            idx++;
         }
         else
         {
            num -= val;
            builder.append(rom);
         }
      }

      return builder.toString();
   }

   private static final int[] DECIMAL =
     new int[] {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

   private static final String[] ROMAN =
     new String[] {"m", "cm", "d", "cd", "c", "cx", "l", "xl",
       "x", "ix", "v", "iv", "i"};
}
