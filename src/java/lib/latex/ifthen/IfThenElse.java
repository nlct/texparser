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
package com.dickimawbooks.texparserlib.latex.ifthen;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class IfThenElse extends ControlSequence
{
   public IfThenElse(IfThenSty sty)
   {
      this(sty, "ifthenelse");
   }

   public IfThenElse(IfThenSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new IfThenElse(sty, getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject test = stack.popArg(parser);
      TeXObject thenClause = stack.popArg(parser);
      TeXObject elseClause = stack.popArg(parser);

      if (sty.evaluate(test))
      {
         thenClause.process(parser, stack);
      }
      else
      {
         elseClause.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject test = parser.popNextArg();
      TeXObject thenClause = parser.popNextArg();
      TeXObject elseClause = parser.popNextArg();

      if (sty.evaluate(test))
      {
         thenClause.process(parser);
      }
      else
      {
         elseClause.process(parser);
      }
   }

   protected IfThenSty sty;
}
