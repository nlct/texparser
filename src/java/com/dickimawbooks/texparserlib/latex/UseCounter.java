/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public class UseCounter extends ControlSequence
{
   public UseCounter()
   {
      this("usecounter");
   }

   public UseCounter(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new UseCounter(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String ctr = popLabelString(parser, stack);

      ControlSequence cs = listener.getControlSequence("@nmbrlisttrue");

      if (parser == stack || stack == null)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }

      parser.putControlSequence(true,
        new TextualContentCommand("@listctr", ctr));

      listener.setcounter(ctr, listener.ZERO);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
