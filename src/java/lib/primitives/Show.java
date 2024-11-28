/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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

public class Show extends Primitive
{
   public Show()
   {
      this("show");
   }

   public Show(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Show(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject obj = stack.popToken();

      if (obj instanceof TeXCsRef)
      {
         obj = parser.getListener().getControlSequence(((TeXCsRef)obj).getName());
      }

      String msg = String.format("%s %s: %n%s",
        toString(parser), obj.toString(parser), obj);

      parser.getListener().getTeXApp().message(msg);
      parser.logMessage(msg);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
