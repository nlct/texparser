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

public class L2HMaketitle extends Maketitle
{
   public L2HMaketitle()
   {
      this("maketitle");
   }

   public L2HMaketitle(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HMaketitle(getName());
   }

   @Override
   protected TeXObjectList createTitle(TeXParser parser)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(new StartElement("header"));

      StartElement elem = new StartElement("div");
      elem.putAttribute("class", "title");

      list.add(elem);
      list.add(listener.getControlSequence("@title"));
      list.add(new EndElement("div"));
      list.add(new HtmlTag("<!-- end of title -->"));

      elem = new StartElement("div");
      elem.putAttribute("class", "author");

      list.add(elem);
      list.add(listener.getControlSequence("@author"));
      list.add(new EndElement("div"));
      list.add(new HtmlTag("<!-- end of author -->"));

      elem = new StartElement("div");
      elem.putAttribute("class", "date");

      list.add(elem);
      list.add(listener.getControlSequence("@date"));
      list.add(new EndElement("div"));
      list.add(new HtmlTag("<!-- end of date -->"));

      list.add(new EndElement("header"));

      return list;
   }

}
