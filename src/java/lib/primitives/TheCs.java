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

public class TheCs extends Primitive implements Expandable
{
   public TheCs()
   {
      this("the");
   }

   public TheCs(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new TheCs(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      Register reg = stack.popRegister(parser);

      TeXObject contents = (TeXObject)reg.getContents(parser).clone();

      if (contents instanceof TeXObjectList && !(contents instanceof Group))
      {
         return (TeXObjectList)contents;
      }

      TeXObjectList list = new TeXObjectList();

      list.add(contents);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      Register reg = parser.popRegister();

      TeXObject contents = (TeXObject)reg.getContents(parser).clone();

      if (contents instanceof TeXObjectList && !(contents instanceof Group))
      {
         return (TeXObjectList)contents;
      }

      TeXObjectList list = new TeXObjectList();

      list.add(contents);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      expandonce(parser,stack).process(parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      expandonce(parser).process(parser);
   }


}
