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

public class Special extends Primitive implements Expandable
{
   public Special()
   {
      this("special");
   }

   public Special(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Special(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      TeXObjectList expanded = null;

      if (arg instanceof Expandable)
      {
         expanded = ((Expandable)arg).expandfully(parser, stack);
      }

      if (expanded != null)
      {
         arg = expanded;
      }

      return parser.getListener().special(arg.toString(parser));
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObject arg = parser.popNextArg();

      TeXObjectList expanded = null;

      if (arg instanceof Expandable)
      {
         expanded = ((Expandable)arg).expandfully(parser);
      }

      if (expanded != null)
      {
         arg = expanded;
      }

      return parser.getListener().special(arg.toString(parser));
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser, stack);

      if (expanded != null)
      {
         expanded.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser);

      if (expanded != null)
      {
         expanded.process(parser);
      }
   }
}
