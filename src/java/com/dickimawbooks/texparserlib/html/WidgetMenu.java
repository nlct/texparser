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
      super(name);
      this.sepCsName = sepCsName;
   }

   public Object clone()
   {
      return new WidgetMenu(getName(), sepCsName);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      CsvList csvList = TeXParserUtils.popCsvList(parser, stack);

      TeXObjectList list = parser.getListener().createStack();

      StartElement startElem = new StartElement("kbd");
      startElem.putAttribute("class", "menu");

      list.add(startElem);

      ControlSequence sepCs = parser.getListener().getControlSequence(sepCsName);

      for (int i = 0; i < csvList.size(); i++)
      {
         if (i > 0)
         {
            list.add(sepCs);
         }

         startElem = new StartElement("kbd");
         startElem.putAttribute("class", "menuitem");
         list.add(startElem);

         list.add(new StartElement("samp"));

         list.add(csvList.getValue(i));

         list.add(new EndElement("samp"));

         list.add(new EndElement("kbd"));
      }

      list.add(new EndElement("kbd"));

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

   protected String sepCsName;
}
