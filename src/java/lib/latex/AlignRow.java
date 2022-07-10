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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.*;

import com.dickimawbooks.texparserlib.*;

public class AlignRow extends DataObjectList
{
   public AlignRow()
   {
      super();
   }

   public AlignRow(int capacity)
   {
      super(capacity);
   }

   public AlignRow(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      this();
      parse(parser, stack);
   }

   @Override
   public TeXObjectList createList()
   {
      return new AlignRow(capacity());
   }

   protected void parse(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      alignSpanList = new TeXObjectList();

      while (true)
      {
         TeXObject span = TeXParserUtils.resolve(stack.popStack(parser), parser);

         if (span instanceof WhiteSpace)
         {
            continue;
         }

         if (!(span instanceof AlignSpan))
         {
            stack.push(span);
            break;
         }

         if (span instanceof TeXObjectList)
         {
            alignSpanList.addAll((TeXObjectList)span);
         }
         else
         {
            alignSpanList.add(span);
         }
      }

      Group group = null;

      ArrayDeque<String> envs = new ArrayDeque<String>();

      while (true)
      {
         TeXObject obj = TeXParserUtils.resolve(stack.popStack(parser), parser);

         if (obj instanceof Tab)
         {
            if (group == null)
            {
               add(listener.createGroup());
            }
            else
            {
               add(group);
               group = null;
            }
         }
         else if (obj instanceof Cr)
         {
            if (group == null)
            {
               add(listener.createGroup());
            }
            else
            {
               add(group);
               group = null;
            }

            add(obj instanceof TabularNewline ? obj :
              listener.getControlSequence("tabularnewline"));

            break;
         }
         else if (obj instanceof Begin)
         {
            String envname = TeXParserUtils.popLabelString(parser, stack);

            envs.push(envname);

            add(obj);
            add(listener.createGroup(envname));
         }
         else if (obj instanceof End)
         {
            String envname = TeXParserUtils.popLabelString(parser, stack);

            if (envs.size() == 0)
            {
               stack.push(listener.createGroup(envname));
               stack.push(obj);
               break;
            }

            envs.pop();
            add(obj);
            add(listener.createGroup(envname));
         }
         else if (obj instanceof MultiCell)
         {
            TeXObject expanded = TeXParserUtils.expandOnce(obj, parser, stack);

            if (group == null)
            {
               group = listener.createGroup();
            }

            group.add(expanded, true);
         }
         else
         {
            if (group == null)
            {
               group = listener.createGroup();
            }

            group.add(obj);
         }
      }

      if (group != null)
      {
         add(group);
      }
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();

      TeXCellAlignList alignList = settings.getAlignmentList();

      if (alignList == null)
      {
         throw new LaTeXSyntaxException(parser, 
            LaTeXSyntaxException.ERROR_NO_ALIGNMENT);
      }

      startRow(parser, stack);

      settings.startRow();

      boolean doEnd = true;

      while (size() > 0)
      {
         TeXObject obj = TeXParserUtils.resolve(pop(), parser);

         if (obj instanceof TabularNewline)
         {
            endRow(parser, stack);

            doEnd = false;

            TeXParserUtils.process(obj, parser, stack);
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

            processCell(parser, stack, alignCell, cell);

            for (int i = 1; i < colSpan; i++)
            {
               parser.getSettings().startColumn();
            }
         }
      }

      if (doEnd)
      {
         endRow(parser, stack);
      }
   }

   /**
   * Starts the current row. This method shouldn't push onto the stack.
   * @param parser the TeX parser
   * @param stack either the current stack or the parser
   */
   protected void startRow(TeXParser parser, TeXObjectList stack) throws IOException
   {
   }

   /**
   * Ends the current row. This method shouldn't push onto the stack.
   * @param parser the TeX parser
   * @param stack either the current stack or the parser
   */
   protected void endRow(TeXParser parser, TeXObjectList stack) throws IOException
   {
   }

   protected void processCell(TeXParser parser, TeXObjectList stack,
       TeXCellAlign alignCell, Group cellContents)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         cellContents.process(parser, this);
      }
      else
      {
         StackMarker marker = new StackMarker();
         add(marker);
         addAll(stack);
         stack.clear();

         cellContents.process(parser, this);

         for (int i = 0; i < size(); i++)
         {
            TeXObject obj = get(i);

            if (obj == marker)
            {
               for (int j = i+1; j < size(); j++)
               {
                  stack.add(get(j));
               }

               setSize(i);
            }
         }
      }
   }

   public TeXObjectList getAlignSpanList()
   {
      return alignSpanList;
   }

   private TeXObjectList alignSpanList;
}
