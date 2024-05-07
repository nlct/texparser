/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.CsvList;

public class Widget extends Command
{
   public Widget(String name, String cssClassName)
   {
      super(name);
      this.cssClassName = cssClassName;
   }

   public Object clone()
   {
      return new Widget(getName(), cssClassName);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject arg = popArg(parser, stack);

      TeXObjectList list = listener.createStack();

      String kbdTag = listener.isHtml5() ? "kbd" : "span";

      StartElement startElem = new StartElement(kbdTag);

      startElem.putAttribute("class", cssClassName);

      list.add(listener.newHtml5StartElement("samp", true));

      list.add(arg, true);

      list.add(listener.newHtml5EndElement("samp", true));

      list.add(new EndElement(kbdTag));

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   protected String cssClassName;
}
