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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class TextBlockCommand extends ControlSequence
{
   public TextBlockCommand(String name, Declaration declaration)
   {
      super(name);
      setSyntax(1);
      this.declaration = declaration;
   }

   public Object clone()
   {
      return new TextBlockCommand(getName(), (Declaration)declaration.clone());
   }

   public void process(TeXParser parser) throws IOException
   {
      declaration.process(parser);
      // If argument grouped, keep it a group, so use popStack() instead
      // of popNextArg()
      parser.popStack().process(parser);
      declaration.end(parser);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      declaration.process(parser, list);
      // If argument grouped, keep it a group, so use pop() instead
      // of popArg()
      list.pop().process(parser, list);
      declaration.end(parser);
   }

   private Declaration declaration;
}
