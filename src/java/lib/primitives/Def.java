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
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

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
      setShort(isShort);
      this.isLocal = isLocal;
   }

   public Object clone()
   {
      return new Def(getName(), isShort, isLocal);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject cs = stack.popStack(parser);

      if (cs == null)
      {
         stack = parser;
         cs = stack.popStack(parser);
      }

      TeXObjectList syntax = new TeXObjectList();
      TeXObject nextObject = stack.popStack(parser);

      while (!(nextObject instanceof Group))
      {
         syntax.add(nextObject);
         nextObject = stack.popStack(parser);
      }

      TeXObjectList definition = ((Group)nextObject).toList();

      if (cs instanceof ControlSequence)
      {
         parser.putControlSequence(isLocal, 
           new GenericCommand(isShort(), ((ControlSequence)cs).getName(),
             syntax, definition));
      }
      else
      {
// TODO
         throw new LaTeXSyntaxException(parser,
           LaTeXSyntaxException.ERROR_UNACCESSIBLE,
           cs.toString(parser));
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   public boolean isLocal()
   {
      return this.isLocal;
   }

   private boolean isLocal;
}
