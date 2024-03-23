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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.TeXParserSection;

public class L2HTeXParserSection extends TeXParserSection
{
   public L2HTeXParserSection()
   {
      this("texparser@section", "section");
   }

   public L2HTeXParserSection(String name, String sectionCsname)
   {
      super(name, sectionCsname);
   }

   @Override
   public Object clone()
   {
      return new L2HTeXParserSection(getName(), sectionCsname);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject title = popArg(parser, stack);
      String label = popLabelString(parser, stack);

      L2HConverter listener = (L2HConverter)parser.getListener();

      String tag = L2HSection.getTag(sectionCsname);

      listener.startSection(false, tag, sectionCsname, label, stack);

      TeXObjectList substack = listener.createStack();

      StartElement elem = new StartElement(tag == null ? "div" : tag, true);

      if (tag == null)
      {
         elem.putAttribute("class", sectionCsname);
      }

      substack.add(elem);

      substack.add(new HtmlTag(String.format("<!-- start of %s header -->",
            sectionCsname)));

      substack.add(title);

      if (listener.isLinkBoxEnabled())
      {
         substack.add(listener.createLinkBox(label));
      }

      substack.add(new EndElement(tag == null ? "div" : tag));

      substack.add(new HtmlTag(String.format("<!-- end of %s header -->%n",
            sectionCsname)));

      TeXParserUtils.process(substack, parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

}
