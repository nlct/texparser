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
import com.dickimawbooks.texparserlib.latex.CsvList;

public class WidgetMenu extends Command
{
   public WidgetMenu(String name, String sepCsName)
   {
      this(name, new TeXCsRef(sepCsName));
   }

   public WidgetMenu(String name, TeXObject sep)
   {
      super(name);
      this.menuSep = sep;
   }

   public Object clone()
   {
      return new WidgetMenu(getName(), (TeXObject)menuSep.clone());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      CsvList csvList = TeXParserUtils.popCsvList(parser, stack);

      TeXObjectList list = listener.createStack();

      String kbdTag = listener.isHtml5() ? "kbd" : "span";

      // https://developer.mozilla.org/en-US/docs/Web/HTML/Element/kbd

      StartElement startElem = new StartElement(kbdTag);

      startElem.putAttribute("class", "menu");

      list.add(startElem);

      if (csvList.size() == 1)
      {
         list.add(listener.newHtml5StartElement("samp", "span"));

         list.add(csvList.getValue(0), true);

         list.add(listener.newHtml5EndElement("samp", "span"));
      }
      else
      {
         TeXObject sep = menuSep;

         if (!menuSep.isSingleToken())
         {
            sep = (TeXObject)menuSep.clone();
         }

         for (int i = 0; i < csvList.size(); i++)
         {
            if (i > 0)
            {
               list.add(sep, true);
            }

            startElem = new StartElement(kbdTag);
            startElem.putAttribute("class", "menuitem");
            list.add(startElem);

            list.add(listener.newHtml5StartElement("samp", "span"));

            list.add(csvList.getValue(i), true);

            list.add(listener.newHtml5EndElement("samp", "span"));

            list.add(new EndElement(kbdTag));
         }
      }

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

   protected TeXObject menuSep;
}
