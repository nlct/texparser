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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class WidgetKeyStroke extends Command
{
   public WidgetKeyStroke(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new WidgetKeyStroke(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObject arg = popArg(parser, stack);

      TeXObjectList list = listener.createStack();

      /*
        Swing's HTMLDocument only has limited support so this assumes the HTML
        output is for Swing if not HTML5.
       */
      String kbdTag = listener.isHtml5() ? "kbd" : "font";

      StartElement startElem = new StartElement(kbdTag);
      startElem.putAttribute("class", "keystroke");

      list.add(startElem);

      list.add(arg);

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

}
