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

public class Csname extends Primitive
{
   public Csname()
   {
      this("csname");
   }

   public Csname(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Csname(getName());
   }

   public String csname(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      TeXObject obj = stack.popStack();

      while (!(obj instanceof EndCsname))
      {
         list.add(obj);
         obj = stack.popStack();
      }

      TeXObjectList expanded = list.expandfully(parser, stack);

      if (expanded == null)
      {
         expanded = list;
      }

      return list.toString(parser);
   }

   private String csname(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      TeXObject obj = parser.popStack();

      while (!(obj instanceof EndCsname))
      {
         list.add(obj);
         obj = parser.popStack();
      }

      TeXObjectList expanded = list.expandfully(parser);

      if (expanded == null)
      {
         expanded = list;
      }

      return list.toString(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname(parser, stack));

      if (cs != null)
      {
         cs.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname(parser));

      if (cs != null)
      {
         cs.process(parser);
      }
   }
}
