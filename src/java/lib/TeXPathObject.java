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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class TeXPathObject implements TeXObject
{
   public TeXPathObject(TeXPath texPath)
   {
      this.texPath = texPath;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.push(parser.getListener().createString(texPath.getTeXPath(false)),
        true);
   }

   @Override
   public Object clone()
   {
      return new TeXPathObject(texPath);
   }

   @Override
   public String toString(TeXParser parser)
   {
      return texPath.getTeXPath(false);
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return parser.getListener().createString(toString(parser));
   }

   @Override
   public String format()
   {
      return texPath.getTeXPath(false);
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmpty()
   {
      return false;
   }

   @Override
   public String toString()
   {
      return String.format("%s[path=%s]", getClass().getSimpleName(), texPath);
   }

   public TeXPath getTeXPath()
   {
      return texPath;
   }

   protected TeXPath texPath;
}

