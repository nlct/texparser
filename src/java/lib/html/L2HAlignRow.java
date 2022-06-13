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

public class L2HAlignRow extends AlignRow
{
   public L2HAlignRow()
   {
      super();
   }

   public L2HAlignRow(int capacity)
   {
      super(capacity);
   }

   public L2HAlignRow(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      this();
      parse(parser, stack);
   }

   @Override
   protected void startRow(TeXParser parser, TeXObjectList stack) throws IOException
   {// don't push onto stack
      hlines = new int[parser.getSettings().getAlignmentColumnCount()];

      for (int i = 0; i < hlines.length; i++)
      {
         hlines[i] = 0;
      }

      TeXObjectList list = getAlignSpanList();

      int rowlines = 0;

      for (TeXObject obj : list)
      {
         if (obj instanceof AlignSpan)
         {
            AlignSpan span = (AlignSpan)obj;

            int start = span.firstColumn();
            int end = span.lastColumn();

            if (start == -1 || end == -1)
            {
               rowlines++;
            }
            else
            {
               for (int i = start; i <= end; i++)
               {
                  hlines[i-1]++;
               }
            }
         }
      }

      L2HConverter listener = (L2HConverter)parser.getListener();

      TeXObjectList substack = listener.createStack();

      StartElement startElem = new StartElement("tr");

      HashMap<String,String> css = new HashMap<String,String>();

      switch (rowlines)
      {
         case 1: css.put("border-top", "solid"); break;
         case 2: css.put("border-top", "double"); break;
      }

      startElem.putStyle(listener, css);

      substack.add(startElem);

      if (isEmpty())
      {
         css = new HashMap<String,String>();

         for (int i = 0; i < hlines.length; i++)
         {
            switch (hlines[i])
            {
               case 0: css.put("border-top", "none"); break;
               case 1: css.put("border-top", "solid"); break;
               default: css.put("border-top", "double");
            }

            startElem = new StartElement("td");
            startElem.putStyle(listener, css);

            substack.add(startElem);
            substack.add(new EndElement("td"));
         }
      }

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }
   }

   @Override
   protected void endRow(TeXParser parser, TeXObjectList stack) throws IOException
   {// don't push onto stack
      parser.getListener().getWriteable().write("</tr>");
   }

   public TeXDimension getDefaultColSep(TeXParser parser)
    throws TeXSyntaxException
   {
      Register reg = parser.getSettings().getRegister("tabcolsep");

      if (reg == null || !(reg instanceof DimenRegister))
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_DIMEN_EXPECTED,
           String.format("%stabcolsep", 
             new String(Character.toChars(parser.getEscChar()))));
      }

      return ((DimenRegister)reg).getDimension();
   }

   protected void getAlignStyle(TeXParser parser, TeXCellAlign alignCell,
    TeXDimension defaultColSep, HashMap<String,String> css)
   {
      int preRules = alignCell.preRuleCount();
      int postRules = alignCell.postRuleCount();

      switch (preRules)
      {
         case 0: css.put("border-left", "none"); break;
         case 1: css.put("border-left", "solid"); break;
         default: css.put("border-left", "double");
      }

      switch (postRules)
      {
         case 0: css.put("border-right", "none"); break;
         case 1: css.put("border-right", "solid"); break;
         default: css.put("border-right", "double");
      }

      int currentColumn = parser.getSettings().getAlignmentColumn();

      switch (hlines[currentColumn-1])
      {
         case 0: css.put("border-top", "none"); break;
         case 1: css.put("border-top", "solid"); break;
         default: css.put("border-top", "double");
      }

      String colSep = defaultColSep.format();

      if (alignCell.getBefore() == null)
      {
         css.put("padding-left", colSep);
      }
      else
      {
         css.put("padding-left", "0px");
      }

      if (alignCell.getAfter() == null)
      {
         css.put("padding-right", colSep);
      }
      else
      {
         css.put("padding-right", "0px");
      }

      TeXDimension width = alignCell.getWidth();

      if (width != null)
      {
         css.put("width", width.toString(parser));
      }

      switch (alignCell.getAlign())
      {
         case 'c': css.put("text-align", "center"); break;
         case 'l': css.put("text-align", "left"); break;
         case 'r': css.put("text-align", "right"); break;
      }
   }

   @Override
   protected void processCell(TeXParser parser, TeXObjectList stack,
      TeXCellAlign alignCell, Group cellContents)
     throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      TeXDimension defaultColSep = getDefaultColSep(parser);

      if (defaultColSep instanceof TeXGlue)
      {
         defaultColSep = ((TeXGlue)defaultColSep).getFixed();
      }

      HashMap<String,String> css = new HashMap<String,String>();

      TeXCellAlign alignment;

      TeXObject firstObj =
         cellContents.isEmpty() ? cellContents : cellContents.peekStack();

      if (firstObj instanceof MultiCell)
      {
         MultiCell multiCell = (MultiCell)firstObj;

         alignment = multiCell.getAlignment();
         int rowSpan = multiCell.getRowSpan();
         int colSpan = multiCell.getColumnSpan();

         if (rowSpan > 1)
         {
            css.put("rowspan", ""+rowSpan);
         }

         if (colSpan > 1)
         {
            css.put("colspan", ""+colSpan);
         }
      }
      else
      {
         alignment = alignCell;
      }

      tag = "td";

      if (!cellContents.isEmpty())
      {
         TeXObject obj = cellContents.firstElement();

         if (obj instanceof ControlSequence 
               && ((ControlSequence)obj).getName().equals("bfseries"))
         {
            cellContents.remove(0);

            tag = "th";
         }
      }

      getAlignStyle(parser, alignment, defaultColSep, css);
      startCell(parser, stack, css);

      TeXObjectList contentsList = new TeXObjectList();

      TeXObject before = alignment.getBefore();
      TeXObject after = alignment.getAfter();

      if (before != null)
      {
         contentsList.add((TeXObject)before.clone());
      }

      TeXObject preShift = alignment.getPreShift();

      if (preShift != null)
      {
         cellContents.push((TeXObject)preShift.clone());
      }

      contentsList.add(cellContents);

      TeXObject postShift = alignment.getPostShift();

      if (postShift != null)
      {
         cellContents.add((TeXObject)postShift.clone());
      }

      if (after != null)
      {
         contentsList.add((TeXObject)after.clone());
      }

      contentsList.process(parser, stack);

      endCell(parser, stack);
   }

   // don't push to stack
   protected void startCell(TeXParser parser, TeXObjectList stack,  
     HashMap<String,String> css)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();
      Writeable writeable = listener.getWriteable();
      writeable.write(String.format("<%s%s>", tag, listener.getStyleOrClass(css)));
   }

   // don't push to stack
   protected void endCell(TeXParser parser, TeXObjectList stack) throws IOException
   {
      parser.getListener().getWriteable().writeln(String.format("</%s>", tag));
   }

   private int[] hlines;
   protected String tag = "td";
}
