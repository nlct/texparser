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
      this(name, cssClassName, "font");
   }

   public Widget(String name, String cssClassName, String nonHtml5Tag)
   {
      super(name);
      this.cssClassName = cssClassName;
      this.nonHtml5Tag = nonHtml5Tag;
   }

   public Object clone()
   {
      return new Widget(getName(), cssClassName, nonHtml5Tag);
   }

   protected TeXObjectList expand(TeXObject arg, TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList list = listener.createStack();

      if (listener.isHtml5())
      {
         StartElement startElem = new StartElement("kbd");

         startElem.putAttribute("class", cssClassName);

         list.add(startElem);

         list.add(new StartElement("samp"));

         list.add(arg, true);

         list.add(new EndElement("samp"));

         list.add(new EndElement("kbd"));
      }
      else
      {
         StartElement startElem = new StartElement(nonHtml5Tag);

         if (cssClassName == null || cssClassName.isEmpty())
         {
            startElem.putAttribute("class", "kbd samp");
         }
         else
         {
            startElem.putAttribute("class", 
              String.format("%s kbd samp", cssClassName));
         }

         list.add(startElem);

         list.add(arg, true);

         list.add(new EndElement(nonHtml5Tag));
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      return expand(arg, parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      return expand(TeXParserUtils.expandFully(arg, parser, stack), parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandfully(parser, parser);
   }

   protected String cssClassName, nonHtml5Tag;
}
