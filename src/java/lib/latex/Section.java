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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public abstract class Section extends ControlSequence
{
   public Section()
   {
      this("section");
   }

   public Section(String name)
   {
      super(name);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      boolean isStar = false;

      TeXObject object = stack.peekStack();

      if (object == null)
      {
         process(parser);
         return;
      }

      if (object instanceof CharObject)
      {
         if (((CharObject)object).getCharCode() == (int)'*')
         {
            isStar = true;
            stack.popStack(parser);
         }
      }

      TeXObject optArg = null;

      if (!isStar)
      {
         optArg = stack.popArg(parser, '[', ']');
      }

      TeXObject arg = stack.popStack(parser);

      if (arg == null)
      {
         arg = parser.popStack();
      }

      if (isStar)
      {
         unnumbered(parser, stack, arg);
      }
      else
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter(getName());
         numbered(parser, stack, optArg, arg);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      boolean isStar = false;

      TeXObject object = parser.peekStack();

      if (object instanceof CharObject)
      {
         if (((CharObject)object).getCharCode() == (int)'*')
         {
            isStar = true;
            parser.popStack();
         }
      }

      TeXObject optArg = null;

      if (!isStar)
      {
         optArg = parser.popNextArg('[', ']');
      }

      TeXObject arg = parser.popNextArg();

      if (isStar)
      {
         unnumbered(parser, parser, arg);
      }
      else
      {
         ((LaTeXParserListener)parser.getListener()).stepcounter(getName());
         numbered(parser, parser, optArg, arg);
      }
   }

   protected abstract void unnumbered(TeXParser parser, TeXObjectList stack,
     TeXObject title)
       throws IOException;

   protected abstract void numbered(TeXParser parser, TeXObjectList stack,
     TeXObject optArg, TeXObject arg)
       throws IOException;
}
