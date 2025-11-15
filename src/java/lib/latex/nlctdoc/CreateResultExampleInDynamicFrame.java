/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class CreateResultExampleInDynamicFrame extends ControlSequence
{
   public CreateResultExampleInDynamicFrame()
   {
      this("createresultexampleindynamicframe");
   }

   public CreateResultExampleInDynamicFrame(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new CreateResultExampleInDynamicFrame(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      boolean isStar = popModifier(parser, stack, '*') == '*';

      TeXObject optArg = popOptArg(parser, stack);

      // ignore remaining arguments
      popArg(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("begin"));
      list.add(listener.createGroup("resultbox"));

      list.add(listener.getControlSequence("createexample"));

      if (optArg != null)
      {
        list.add(listener.getOther('['));
        list.add(optArg, true);
        list.add(listener.getOther(']'));
      }

      list.add(listener.createGroup());
      list.add(listener.createGroup());

      list.add(listener.getControlSequence("end"));
      list.add(listener.createGroup("resultbox"));

      TeXParserUtils.process(list, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
