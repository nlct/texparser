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

public class Ifx extends If
{
   public Ifx()
   {
      this("ifx");
   }

   public Ifx(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Ifx(getName());
   }

   protected boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject firstArg;

      if (parser == stack || stack == null)
      {
         firstArg = parser.popStack();
      }
      else
      {
         firstArg = stack.popStack();

         if (firstArg == null)
         {
            firstArg = parser.popStack();
         }
      }

      TeXObject secondArg;

      if (parser == stack || stack == null)
      {
         secondArg = parser.popStack();
      }
      else
      {
         secondArg = stack.popStack();

         if (secondArg == null)
         {
            secondArg = parser.popStack();
         }
      }

      if (firstArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)firstArg).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)firstArg).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            firstArg = expanded;
         }
      }

      if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)secondArg).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)secondArg).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            secondArg = expanded;
         }
      }

      return firstArg.equals(secondArg);
   }
}
