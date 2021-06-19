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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class Index extends ControlSequence
{
   public Index()
   {
      this("index");
   }

   public Index(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Index(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      PopStyle popStyle = PopStyle.SHORT;

      TeXObject optArg = parser.popOptionalExpandFully(stack, popStyle);

      String opt = null;

      if (optArg != null)
      {
         opt = optArg.toString(parser);
      }

      TeXObject arg1 = parser.popRequired(stack, popStyle);

      ((LaTeXParserListener)parser.getListener()).index(opt, arg1);
   }

}
