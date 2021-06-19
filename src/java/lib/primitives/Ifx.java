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

   @Override
   public Object clone()
   {
      return new Ifx(getName());
   }

   @Override
   public boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      PopStyle popStyle = PopStyle.IGNORE_LEADING_SPACE;

      TeXObject firstArg = parser.popNextToken(stack, popStyle);

      TeXObject secondArg = parser.popNextToken(stack, popStyle);

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

      if (firstArg instanceof AssignedMacro)
      {
         firstArg = ((AssignedMacro)firstArg).getBaseUnderlying();
      }

      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getListener().getControlSequence(
            ((TeXCsRef)secondArg).getName());
      }

      if (secondArg instanceof AssignedMacro)
      {
         secondArg = ((AssignedMacro)secondArg).getBaseUnderlying();
      }

      firstArg = parser.expandOnce(firstArg, stack);
      secondArg = parser.expandOnce(secondArg, stack);

      return firstArg.equals(secondArg);
   }
}
