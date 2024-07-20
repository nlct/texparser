/*
    Copyright (C) 2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DeclareGraphicsExtensions extends ControlSequence
{
   public DeclareGraphicsExtensions()
   {
      this("DeclareGraphicsExtensions");
   }

   public DeclareGraphicsExtensions(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DeclareGraphicsExtensions(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String list = popLabelString(parser, stack);

      ((LaTeXParserListener)parser.getListener()).setImageExtensions(
        list.split("\\s*,[\\s,]*"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
