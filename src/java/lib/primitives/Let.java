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

public class Def extends Primitive
{
   public Def()
   {
      this("def");
   }

   public Def(String name)
   {
      this(name, true, true);
   }

   public Def(String name, boolean isShort, boolean isLocal)
   {
      super(name);
      this.isShort = isShort;
      this.isLocal = isLocal;
   }

   public Object clone()
   {
      return new Def(getName(), isShort, isLocal);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject cs = stack.popStack();

      if (cs == null)
      {
         stack = parser;
      }

      TeXObjectList syntax = new TeXObjectList();
      TeXObject nextObject = stack.popStack();

      while (!(nextObject instanceof Group))
      {
         syntax.add(nextObject);
         nextObject = stack.popStack();
      }

      TeXObjectList definition = ((Group)nextObject).toList();

      if (cs instanceof ControlSequence)
      {
         parser.putControlSequence(isLocal, 
           new GenericCommand(((ControlSequence)cs).getName(),
             syntax, definition));
      }
      else
      {
         // TODO
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject cs = parser.popStack();

      TeXObjectList syntax = new TeXObjectList();
      TeXObject nextObject = parser.popStack(isShort);

      while (!(nextObject instanceof Group))
      {
         syntax.add(nextObject);
         nextObject = parser.popStack(isShort);
      }

      TeXObjectList definition = ((Group)nextObject).toList();

      if (cs instanceof ControlSequence)
      {
         parser.putControlSequence(isLocal, 
           new GenericCommand(((ControlSequence)cs).getName(),
             syntax, definition));
      }
      else
      {
         // TODO
      }
   }

   private boolean isShort, isLocal;
}
