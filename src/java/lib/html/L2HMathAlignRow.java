/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
import java.util.*;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HMathAlignRow extends L2HAlignRow
{
   public L2HMathAlignRow(boolean isNumbered)
   {
      super();
      this.isNumbered = isNumbered;
   }

   public L2HMathAlignRow(int capacity, boolean isNumbered)
   {
      super(capacity);
      this.isNumbered = isNumbered;
   }

   public L2HMathAlignRow(TeXParser parser, TeXObjectList stack,
     boolean isNumbered)
   throws IOException
   {
      this(isNumbered);
      parse(parser, stack);
   }

   protected void processCell(TeXParser parser, TeXCellAlign alignCell,
      Group cellContents)
     throws IOException
   {
      for (int i = cellContents.size()-1; i >= 0; i--)
      {
         TeXObject object = cellContents.get(i);

         if (object instanceof HtmlTag)
         {
            cellContents.remove(i);
            tags.push((HtmlTag)object);
         }
      }

      super.processCell(parser, alignCell, cellContents);
   }

   protected void startRow(TeXParser parser) throws IOException
   {
      super.startRow(parser);

      parser.getListener().getWriteable().write("<td style=\"width: 50%; \"></td>");
   }

   protected void endRow(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (isNumbered)
      {
         listener.stepcounter("equation");
         listener.write("<td style=\"width: 50%; align: right;\"><span class=\"eqno\">(");
         listener.getControlSequence("theequation").process(parser);
         listener.write(")</span></td>");
      }
      else
      {
         listener.write("<td style=\"width: 50%; \"></td>");
      }

      super.endRow(parser);
   }

   public TeXDimension getDefaultColSep(TeXParser parser)
    throws TeXSyntaxException
   {
      Register reg = parser.getSettings().getRegister("arraycolsep");

      if (reg == null || !(reg instanceof DimenRegister))
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED,
           String.format("%sarraycolsep",
             new String(Character.toChars(parser.getEscChar()))));
      }

      return ((DimenRegister)reg).getDimension();
   }


   protected void startCell(TeXParser parser, String span, String style)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      super.startCell(parser, span, style);

      if (listener.useMathJax())
      {
         listener.write(listener.mathJaxStartInline()+"\\displaystyle ");
      }
   }

   protected void endCell(TeXParser parser) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.useMathJax())
      {
         listener.write(listener.mathJaxEndInline());
      }

      while (tags.size() > 0)
      {
         tags.pop().process(parser);
      }

      super.endCell(parser);
   }

   private boolean isNumbered;

   private ArrayDeque<HtmlTag> tags = new ArrayDeque<HtmlTag>();
}
