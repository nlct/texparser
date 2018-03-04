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

   public boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXObject firstArg = parser.popToken(popStyle);

      TeXObject secondArg = parser.popToken(popStyle);

      if (firstArg instanceof ControlSequence 
          && secondArg instanceof ControlSequence
          && ((ControlSequence)firstArg).getName().equals(
             ((ControlSequence)secondArg).getName()))
      {
         return true;
      }

      if (firstArg instanceof TeXCsRef)
      {
         firstArg = parser.getListener().getControlSequence(
            ((TeXCsRef)firstArg).getName());
      }

      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getListener().getControlSequence(
            ((TeXCsRef)secondArg).getName());
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
            if (!(expanded instanceof Group) && expanded.size() == 1)
            {
               firstArg = expanded.firstElement();
            }
            else
            {
               firstArg = expanded;
            }
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
            if (!(expanded instanceof Group) && expanded.size() == 1)
            {
               secondArg = expanded.firstElement();
            }
            else
            {
               secondArg = expanded;
            }
         }
      }

      return firstArg.equals(secondArg);
   }
}
