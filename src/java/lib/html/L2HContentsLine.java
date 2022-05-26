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

public class L2HContentsLine extends ContentsLine
{
   public L2HContentsLine()
   {
      this("contentsline");
   }

   public L2HContentsLine(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HContentsLine(getName());
   }

   @Override
   public TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page, TeXObject link)
      throws IOException
   {
      String linkStr = link.toString(parser);

      if (linkStr.isEmpty())
      {
         return contentsline(parser, type, title, page);
      }

      String typeStr = type.toString(parser);
      TeXObjectList list = new TeXObjectList();

      list.add(new HtmlTag(
        String.format("<div class=\"toc-%s\"><a href=\"#%s\">",
        typeStr, HtmlTag.getUriFragment(linkStr))));
      list.add(title);
      list.add(new HtmlTag(String.format("</a></div><!-- end of toc-%s -->%n", typeStr)));

      return list;
   }

   @Override
   public TeXObjectList contentsline(TeXParser parser, TeXObject type,
    TeXObject title, TeXObject page)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      String typeStr = type.toString(parser);

      list.add(new HtmlTag(String.format("<div class=\"toc-%s\">",
        typeStr)));
      list.add(title);
      list.add(new HtmlTag(String.format("</div><!-- end of toc-%s -->%n", typeStr)));

      return list;
   }

}
