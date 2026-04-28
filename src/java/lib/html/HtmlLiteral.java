/*
    Copyright (C) 2026 Nicola L.C. Talbot
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

public class HtmlLiteral extends AbstractTeXObject
{
   public HtmlLiteral(String htmlCode)
   {
      this.htmlCode = htmlCode;
   }

   @Override
   public Object clone()
   {
      return new HtmlLiteral(getHtmlCode());
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public TeXObjectList string(TeXParser parser)
   {
      return parser.getListener().createString(getHtmlCode());
   }

   @Override
   public String toString()
   {
      return String.format("%s[htmlCode=%s]", 
        getClass().getSimpleName(), getHtmlCode());
   }

   @Override
   public String format()
   {
      return getHtmlCode();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return getHtmlCode();
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      parser.getListener().getWriteable().writeliteral(htmlCode);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   public String getHtmlCode()
   {
      return htmlCode;
   }

   private String htmlCode;
}
