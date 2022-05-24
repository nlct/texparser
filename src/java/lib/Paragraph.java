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
import java.util.Vector;
import java.util.ArrayDeque;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;

/**
 * A paragraph with margins. A null margin indicates 0pt.
 */

public class Paragraph extends DataObjectList
{
   public Paragraph()
   {
      super();
   }

   public Paragraph(int capacity)
   {
      super(capacity);
   }

   public Paragraph(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public void setLeftMargin(TeXDimension margin)
   {
      leftMargin = margin;
   }

   public void setRightMargin(TeXDimension margin)
   {
      rightMargin = margin;
   }

   public void setTopMargin(TeXDimension margin)
   {
      topMargin = margin;
   }

   public void setBottomMargin(TeXDimension margin)
   {
      bottomMargin = margin;
   }

   public void setParIndent(TeXDimension indent)
   {
      parIndent = indent;
   }

   public TeXDimension getLeftMargin()
   {
      return leftMargin;
   }

   public TeXDimension getRightMargin()
   {
      return rightMargin;
   }

   public TeXDimension getTopMargin()
   {
      return topMargin;
   }

   public TeXDimension getBottomMargin()
   {
      return bottomMargin;
   }

   public TeXDimension getParIndent()
   {
      return parIndent;
   }

   @Override
   public boolean isStack()
   {
      return false;
   }

   @Override
   public TeXObjectList createList()
   {
      return new Paragraph(capacity());
   }

   protected TeXObject getHead(TeXParser parser)
   {
      return null;
   }

   protected TeXObject getTail(TeXParser parser)
   {
      return parser.getListener().getPar();
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject head = getHead(parser);

      if (head != null)
      {
         head.process(parser, stack);
      }

      super.process(parser, stack);

      TeXObject tail = getTail(parser);

      if (tail != null)
      {
         tail.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject head = getHead(parser);

      if (head != null)
      {
         head.process(parser);
      }

      super.process(parser);

      TeXObject tail = getTail(parser);

      if (tail != null)
      {
         tail.process(parser);
      }
   }

   protected TeXDimension leftMargin, rightMargin, topMargin, bottomMargin, parIndent;
}
