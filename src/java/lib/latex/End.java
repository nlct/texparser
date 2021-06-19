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
import com.dickimawbooks.texparserlib.primitives.EndCs;

public class End extends EndCs
{
   public End()
   {
      this("end");
   }

   public End(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new End(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String name = parser.popRequiredString(stack);

      if (name.equals("document"))
      {
         listener.endDocument();
         return;
      }

      TeXObject currenv = parser.getControlSequence("@currenvir");

      if (currenv == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_EXTRA_END, name);
      }

      currenv = parser.expandFully(currenv, stack);

      doEnd(parser, name);

      parser.endGroup();

      if (!name.equals(currenv.toString(parser)))
      {
         throw new LaTeXSyntaxException(parser, 
             LaTeXSyntaxException.ERROR_EXTRA_END, name);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected void doEnd(TeXParser parser, String name)
      throws IOException
   {
      TeXObject endObj = parser.resolveReference(
         parser.getListener().getControlSequence(name));

      if (endObj instanceof Declaration)
      {
         ((Declaration)endObj).end(parser);
      }
   }
}
