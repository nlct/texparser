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

   protected void startRow(TeXParser parser) throws IOException
   {
      hlines = new int[parser.getSettings().getAlignmentColumnCount()];

      for (int i = 0; i < hlines.length; i++)
      {
         hlines[i] = 0;
      }

      TeXObjectList list = getAlignSpanList();

      for (TeXObject obj : list)
      {
         if (obj instanceof AlignSpan)
         {
            AlignSpan span = (AlignSpan)obj;

            int start = span.firstColumn();
            int end = span.lastColumn();

            if (start == -1 || end == -1)
            {
               start = 1;
               end = hlines.length;
            }

            for (int i = start; i <= end; i++)
            {
               hlines[i-1]++;
            }
         }
      }

      TeXParserListener listener = parser.getListener();

      listener.getWriteable().write("<tr>");

      if (isEmpty())
      {
         for (int i = 0; i < hlines.length; i++)
         {
            String style = null;

            switch (hlines[i])
            {
               case 0: style = "border-top: none; "; break;
               case 1: style = "border-top: solid; "; break;
               default: style = "border-top: double; ";
            }

            if (style == null)
            {
               listener.getWriteable().write("<td></td>");
            }
            else
            {
               listener.getWriteable().write("<td style=\""+style+"\"></td>");
            }
         }
      }
   }

   protected void endRow(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().writeln("</tr>");
   }

   protected String getAlignStyle(TeXParser parser, TeXCellAlign alignCell)
   {
      String style = "";

      int preRules = alignCell.preRuleCount();
      int postRules = alignCell.postRuleCount();

      switch (preRules)
      {
         case 0: style += "border-left: none; "; break;
         case 1: style += "border-left: solid; "; break;
         default: style += "border-left: double; ";
      }

      switch (postRules)
      {
         case 0: style += "border-right: none; "; break;
         case 1: style += "border-right: solid; "; break;
         default: style += "border-right: double; ";
      }

      int currentColumn = parser.getSettings().getAlignmentColumn();

      switch (hlines[currentColumn-1])
      {
         case 0: style += "border-top: none; "; break;
         case 1: style += "border-top: solid; "; break;
         default: style += "border-top: double; ";
      }

      if (alignCell.getBefore() != null)
      {
         style += "padding-left: 0px; ";
      }

      if (alignCell.getAfter() != null)
      {
         style += "padding-right: 0px; ";
      }

      TeXDimension width = alignCell.getWidth();

      if (width != null)
      {
         style += "width: "+width.toString(parser)+"; ";
      }

      return style;
   }

   protected void processCell(TeXParser parser, TeXCellAlign alignCell,
      Group cellContents)
     throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      String span = "";

      TeXCellAlign alignment;

      TeXObject firstObj =
         cellContents.isEmpty() ? cellContents : cellContents.firstElement();

      if (firstObj instanceof MultiCell)
      {
         MultiCell multiCell = (MultiCell)firstObj;

         alignment = multiCell.getAlignment();
         int rowSpan = multiCell.getRowSpan();
         int colSpan = multiCell.getColumnSpan();

         if (rowSpan > 1)
         {
            span = " rowspan=\""+rowSpan+"\"";
         }

         if (colSpan > 1)
         {
            span += " colspan=\""+colSpan+"\"";
         }
      }
      else
      {
         alignment = alignCell;
      }

      writeable.write("<td"+span+" style=\"");

      writeable.write(getAlignStyle(parser, alignment));

      writeable.write("\">");

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

      while (contentsList.size() > 0)
      {
         contentsList.pop().process(parser, contentsList);
      }

      writeable.writeln("</td>");
   }

   private int[] hlines;
}
