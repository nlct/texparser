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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HContentsLine extends ControlSequence
{
   public L2HContentsLine()
   {
      this("contentsline");
   }

   public L2HContentsLine(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HContentsLine(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject type = stack.popArg();
      TeXObject title = stack.popArg();
      TeXObject page = stack.popArg();
      TeXObject link = null;

      if (listener.isStyLoaded("hyperref"))
      {
         link = stack.popArg();
      }
   }

   public void process(TeXParser parser)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject type = parser.popNextArg();
      TeXObject title = parser.popNextArg();
      TeXObject page = parser.popNextArg();
      TeXObject link = null;

      if (listener.isStyLoaded("hyperref"))
      {
         link = parser.popNextArg();
      }
   }

}
