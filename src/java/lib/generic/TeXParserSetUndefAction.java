/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class TeXParserSetUndefAction extends ControlSequence
{
   public TeXParserSetUndefAction()
   {
      this(-1);
   }

   public TeXParserSetUndefAction(int action)
   {
      this("TeXParserSetUndefAction", action);
   }

   public TeXParserSetUndefAction(String name, int action)
   {
      super(name);
      this.action = action;
   }

   public Object clone()
   {
      return new TeXParserSetUndefAction(getName(), action);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      byte currentAction = (byte)action;

      if (action == -1)
      {
         Numerical num = popNumericalArg(parser, stack);

         currentAction = (byte)num.number(parser);
      }

      ((DefaultTeXParserListener)parser.getListener()).setUndefinedAction(currentAction);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   private int action=-1;
}

