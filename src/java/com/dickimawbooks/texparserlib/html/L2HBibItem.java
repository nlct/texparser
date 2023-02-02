/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public class L2HBibItem extends BibItem
{
   public L2HBibItem()
   {
      this("bibitem");
   }

   public L2HBibItem(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HBibItem(getName());
   }

   protected void pushPostItem(TeXParser parser, TeXObjectList stack,
     TeXObject arg)
    throws IOException
   {
      if (parser == stack || stack == null)
      {
         parser.push(new EndElement("div"));
         parser.push(new HtmlTag("<!-- end of bibitem -->"));
         parser.push(new StartElement("div"));
      }
      else
      {
         stack.push(new EndElement("div"));
         stack.push(new HtmlTag("<!-- end of bibitem -->"));
         stack.push(new StartElement("div"));
      }

      super.pushPostItem(parser, stack, arg);
      
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (parser == stack || stack == null)
      {
         parser.push(new EndElement("a"));
      }
      else
      {
         stack.push(new EndElement("a"));
      }
   }

   protected void pushPreItem(TeXParser parser, TeXObjectList stack,
      TeXObject arg)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String label = parser.expandToString(arg, stack);

      label = HtmlTag.getUriFragment(label);

      StartElement elem = new StartElement("a");
      elem.putAttribute("id", label);

      if (parser == stack || stack == null)
      {
         parser.push(elem);
      }
      else
      {
         stack.push(elem);
      }

      super.pushPreItem(parser, stack, arg);

      elem = new StartElement("div");
      elem.putAttribute("class", "bibitem");

      if (parser == stack || stack == null)
      {
         parser.push(new EndElement("div"));
         parser.push(new HtmlTag("<!-- end of bibitem -->"));
         parser.push(elem);
      }
      else
      {
         stack.push(new EndElement("div"));
         stack.push(new HtmlTag("<!-- end of bibitem -->"));
         stack.push(elem);
      }

   }
}
