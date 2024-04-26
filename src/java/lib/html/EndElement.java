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
import java.util.Vector;
import java.net.URI;
import java.net.URISyntaxException;

import com.dickimawbooks.texparserlib.*;

public class EndElement extends HtmlTag
{
   public EndElement(String name)
   {
      this(name, false);
   }

   public EndElement(String name, boolean appendCR)
   {
      this(name, appendCR, name.equals("div"));
   }

   public EndElement(String name, boolean appendCR, boolean isBlock)
   {
      super(String.format("</%s>", name));

      if (name.contains("[^a-zA-Z]"))
      {
         throw new IllegalArgumentException(
          String.format("Invalid element name '%s'", name));
      }

      this.name = name;
      this.appendCR = appendCR;
      this.isBlock = isBlock;
   }

   @Override
   public Object clone()
   {
      return new EndElement(getName(), appendCR);
   }

   public String getName()
   {
      return name;
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (isBlock)
      {
         listener.endParagraph();
         listener.setCurrentBlockType(DocumentBlockType.BODY);
      }

      super.process(parser);

      if (appendCR)
      {
         listener.writeln();
      }
   }

   private String name;
   private boolean appendCR=false, isBlock=false;
}
