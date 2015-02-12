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
package com.dickimawbooks.texparserlib.plain;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Input extends ControlSequence
{
   public Input()
   {
      this("input");
   }

   public Input(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Input(getName());
   }

   private boolean fetchArg(TeXParser parser, TeXObjectList stack,
     StringBuilder builder)
     throws IOException
   {
      TeXObject next = (stack == parser ? parser.popStack() :
         stack.popStack(parser));

      TeXObjectList expanded = null;

      if (next instanceof Expandable)
      {
         expanded = ((Expandable)next).expandfully(parser, stack);
      }

      if (expanded == null)
      {
         if (next instanceof WhiteSpace)
         {
            return false; // finished
         }

         if (next instanceof CharObject)
         {
             builder.appendCodePoint(((CharObject)next).getCharCode());
             return true;
         }

         if (next instanceof UserNumber)
         {
             builder.append(next.toString(parser));
             return true;
         }
      }
      else
      {
         stack.addAll(0, expanded);
         return fetchArg(parser, stack, builder);
      }

      stack.push(next);
      return false;
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      StringBuilder builder = new StringBuilder();

      while (fetchArg(parser, stack, builder))
      {
      }

      TeXParserListener listener = parser.getListener();
      TeXPath texPath = new TeXPath(parser, builder.toString());

      listener.addFileReference(texPath);

      listener.input(texPath);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      StringBuilder builder = new StringBuilder();

      while (fetchArg(parser, parser, builder))
      {
      }

      TeXParserListener listener = parser.getListener();
      TeXPath texPath = new TeXPath(parser, builder.toString());

      listener.addFileReference(texPath);

      listener.input(texPath);
   }
}
