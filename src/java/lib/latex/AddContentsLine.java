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

public class AddContentsLine extends ControlSequence
{
   public AddContentsLine()
   {
      this("addcontentsline");
   }

   public AddContentsLine(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AddContentsLine(getName());
   }

   public void addcontentsline(TeXParser parser, 
     TeXObject toc, TeXObject type, TeXObject title)
   throws IOException
   {
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject toc = parser.popNextArg();
      TeXObject type = parser.popNextArg();
      TeXObject title =parser.popNextArg();

      addcontentsline(parser, toc, type, title);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject toc = stack.popArg(parser);
      TeXObject type = stack.popArg(parser);
      TeXObject title = stack.popArg(parser);

      addcontentsline(parser, toc, type, title);
   }

}
