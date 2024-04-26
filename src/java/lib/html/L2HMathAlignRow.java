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

   @Override
   protected void processCell(TeXParser parser, TeXObjectList stack, 
      TeXCellAlign alignCell, Group cellContents)
     throws IOException
   {
      for (int i = cellContents.size()-1; i >= 0; i--)
      {
         TeXObject object = cellContents.get(i);

         if (object instanceof Label && i < cellContents.size()-1)
         {
            TeXObject arg = cellContents.get(i+1);

            if (arg instanceof Group)
            {
               cellContents.remove(i+1);
               String label = parser.expandToString(((Group)arg).toList(), stack);

               object = new HtmlTag(String.format("<a id=\"%s\"></a>", label));
            }
         }

         if (object instanceof HtmlTag)
         {
            cellContents.remove(i);
            tags.push((HtmlTag)object);
         }
      }

      super.processCell(parser, stack, alignCell, cellContents);
   }

   @Override
   protected void startRow(TeXParser parser, TeXObjectList stack) throws IOException
   {// don't push to stack
      super.startRow(parser, stack);

      parser.getListener().getWriteable().writeliteral("<td class=\"left-outer\"></td>");
   }

   @Override
   protected void endRow(TeXParser parser, TeXObjectList stack) throws IOException
   {// don't push to stack
      L2HConverter listener = (L2HConverter)parser.getListener();

      Writeable writeable = parser.getListener().getWriteable();

      if (isNumbered)
      {
         listener.stepcounter("equation");

         writeable.writeliteral("<td class=\"right-outer\"><span class=\"eqno\">");
         writeable.write('(');

         ControlSequence cs = parser.getListener().getControlSequence("theequation");

         String eqnum = parser.expandToString(cs, stack);

         writeable.write(eqnum);

         writeable.write(')');
         writeable.writeliteral("</span></td>");
      }
      else
      {
         writeable.writeliteral("<td class=\"right-outer\"></td>");
      }

      super.endRow(parser, stack);
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


   @Override
   protected void startCell(TeXParser parser, TeXObjectList stack, 
      HashMap<String,String> css)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      super.startCell(parser, stack, css);

      if (listener.useMathJax())
      {
         listener.writeliteral(listener.mathJaxStartInline()+"\\displaystyle ");
      }
   }

   @Override
   protected void endCell(TeXParser parser, TeXObjectList stack) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.useMathJax())
      {
         listener.writeliteral(listener.mathJaxEndInline());
      }

      while (tags.size() > 0)
      {
         if (parser == stack || stack == null)
         {
            tags.pop().process(parser);
         }
         else
         {
            tags.pop().process(parser, stack);
         }
      }

      super.endCell(parser, stack);
   }

   private boolean isNumbered;

   private ArrayDeque<HtmlTag> tags = new ArrayDeque<HtmlTag>();
}
