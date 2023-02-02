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

import com.dickimawbooks.texparserlib.*;

public class AddToCounter extends ControlSequence
{
   public AddToCounter()
   {
      this("addtocounter");
   }

   public AddToCounter(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AddToCounter(getName());
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject counter = list.popArg(parser);

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser, list);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      Numerical increment = list.popNumericalArg(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.addtocounter(counter.toString(parser), increment);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject counter = parser.popNextArg();

      if (counter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)counter).expandfully(parser);

         if (expanded != null)
         {
            counter = expanded;
         }
      }

      Numerical increment = parser.popNumericalArg();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.addtocounter(counter.toString(parser), increment);
   }
}
