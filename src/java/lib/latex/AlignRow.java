/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.*;

import com.dickimawbooks.texparserlib.*;

public class AlignRow extends Vector<AlignCell>
{
   public AlignRow()
   {
      super();
   }

   public AlignRow(int capacity)
   {
      super(capacity);
   }

   protected void updateNoAlign(TeXSettings settings)
   {
      if (!settings.anyNoAlign())
      {
         return;
      }

      if (startRow == null)
      {
         startRow = new Vector<TeXObject>();
      }

      startRow.addAll(settings.getNoAlign());
      settings.clearNoAlign();
   }

   protected void updateEndRow()
   {
   }

   public void parse(TeXParser parser, TeXObjectList stack, String envName)
     throws IOException
   {
      clear();
      TeXSettings settings = parser.getSettings();
      TeXParserListener listener = parser.getListener();

      settings.startRow();

      AlignCell cell = listener.createAlignCell();

      while (true)
      {
         TeXObject object = parser.expandFully(
             parser.popNextTokenResolveReference(stack), stack);

         if (object == null) break;

         if (object instanceof WhiteSpace)
         {
            continue;
         }

         if (object instanceof NoAlign || object instanceof AlignSpan)
         {
            parser.processObject(object, stack);
            updateNoAlign(settings);
         }
         else if (object instanceof Tab)
         {
            add(cell);
            cell = listener.createAlignCell();
            settings.startColumn();
         }
         else if (object instanceof Cr)
         {
            add(cell);
            cell = listener.createAlignCell();
         }
         else
         {
            cell.add(object);
         }
      }

   }

   public void process(TeXParser parser, TeXObjectList stack, String envName)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();

      TeXCellAlignList alignList = settings.getAlignmentList();

      if (alignList == null)
      {
         throw new LaTeXSyntaxException(parser, 
            LaTeXSyntaxException.ERROR_NO_ALIGNMENT);
      }

      startRow(parser);

      settings.startRow();

      boolean doEnd = true;

      while (size() > 0)
      {
         TeXObject obj = pop();

         if (obj instanceof TabularNewline)
         {
            endRow(parser);

            doEnd = false;

            parser.processObject(obj, stack);
         }
         else
         {
            int currentColumn = settings.getAlignmentColumn();
            TeXCellAlign alignCell = alignList.get(currentColumn);
            parser.getSettings().startColumn();

            // obj should already be a group

            Group cell;

            if (obj instanceof Group)
            {
               cell = (Group)obj;
            }
            else
            {
               cell = parser.getListener().createGroup();
               cell.add(obj);
            }

            int colSpan = 0;

            TeXObject firstObj = cell.isEmpty() ? cell : cell.peekStack();

            if (firstObj instanceof MultiCell)
            {
               colSpan = ((MultiCell)firstObj).getColumnSpan();
            }

            processCell(parser, alignCell, cell);

            for (int i = 1; i < colSpan; i++)
            {
               parser.getSettings().startColumn();
            }
         }
      }

      if (doEnd)
      {
         endRow(parser);
      }
   }

   protected void startRow(TeXParser parser) throws IOException
   {
   }

   protected void endRow(TeXParser parser) throws IOException
   {
   }

   protected void processCell(TeXParser parser, TeXCellAlign alignCell, AlignCell cellContents)
     throws IOException
   {
      cellContents.process(parser);
   }

   public TeXObjectList getAlignSpanList()
   {
      return alignSpanList;
   }

   private TeXObjectList alignSpanList;

   private Vector<TeXObject> startRow, endRow;
}
