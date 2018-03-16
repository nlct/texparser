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

public class Let extends Primitive
{
   public Let()
   {
      this("let");
   }

   public Let(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new Let(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject firstArg = stack.popToken();

      if (firstArg == null)
      {
         process(parser);
         return;
      }

      TeXObject secondArg = stack.popToken();

      if (secondArg == null)
      {
         secondArg = parser.popToken();
      }

      doAssignment(parser, firstArg, secondArg);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject firstArg = parser.popToken();

      TeXObject secondArg = parser.popToken();

      doAssignment(parser, firstArg, secondArg);
   }

   protected void doAssignment(TeXParser parser,
     TeXObject firstArg, TeXObject secondArg)
   {
      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getListener().getControlSequence(
           ((TeXCsRef)secondArg).getName());
      }

      TeXObject underlying = (TeXObject)secondArg.clone();

      if (firstArg instanceof ControlSequence)
      {
         ControlSequence cs = (ControlSequence)firstArg;

         AssignedControlSequence newObject = 
            new AssignedControlSequence(cs.getName(), underlying);

         parser.putControlSequence(getPrefix() != PREFIX_GLOBAL, newObject);
      }
      else if (firstArg instanceof ActiveChar)
      {
         ActiveChar ac = (ActiveChar)firstArg;

         AssignedActiveChar newObject = new AssignedActiveChar(ac.getCharCode(),
            underlying);

         parser.putActiveChar(getPrefix() != PREFIX_GLOBAL, newObject);
      }
      else if (firstArg instanceof CharObject)
      {
         CharObject chObj = (CharObject)firstArg;

         AssignedActiveChar newObject = new AssignedActiveChar(
            chObj.getCharCode(), underlying);

         parser.putActiveChar(getPrefix() != PREFIX_GLOBAL, newObject);
      }
      else
      {
         String str = firstArg.toString(parser);

         AssignedActiveChar newObject = new AssignedActiveChar(
            str.codePointAt(0), underlying);

         parser.putActiveChar(getPrefix() != PREFIX_GLOBAL, newObject);
      }

      clearPrefix();
   }

}
