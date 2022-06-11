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
import com.dickimawbooks.texparserlib.latex.*;

public class L2HParagraph extends Paragraph
{
   public L2HParagraph()
   {
      super();
   }

   public L2HParagraph(int capacity)
   {
      super(capacity);
   }

   public L2HParagraph(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   @Override
   public TeXObjectList createList()
   {
      return new L2HParagraph(capacity());
   }

   @Override
   protected TeXObject getHead(TeXParser parser)
   {
      String style="";

      TeXDimension dim = getLeftMargin();

      if (dim != null)
      {
         style += String.format("margin-left: %s; ", dim.format());
      }

      dim = getRightMargin();

      if (dim != null)
      {
         style += String.format("margin-right: %s; ", dim.format());
      }

      dim = getTopMargin();

      if (dim != null)
      {
         style += String.format("margin-top: %s; ", dim.format());
      }

      dim = getBottomMargin();

      if (dim != null)
      {
         style += String.format("margin-bottom: %s; ", dim.format());
      }

      dim = getParIndent();

      if (dim != null)
      {
         style += String.format("text-indent: %s; ", dim.format());
      }

      StartElement startTag = new StartElement("div");

      if (!style.isEmpty())
      {
         startTag.putAttribute("style", style);
      }

      return startTag;
   }

   @Override
   protected TeXObject getTail(TeXParser parser)
   {
      return new EndElement("div");
   }
}
