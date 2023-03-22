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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.util.Vector;

public class Group extends TeXObjectList
{
   public Group()
   {
      super();
   }

   public Group(int capacity)
   {
      super(capacity);
   }

   public Group(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   @Override
   public boolean isStack()
   {
      return false;
   }

   public boolean isMathGroup()
   {
      return false;
   }

   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList(size());

      list.addAll(this);

      return list;
   }

   @Override
   public TeXObjectList createList()
   {
      return new Group(capacity());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK))
      {
         parser.logMessage(String.format("PROCESSING %s: %s SUBSTACK: ",
          getClass().getSimpleName(), toString(parser),
           stack.toString(parser)));
      }

      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK_LIST))
      {
         parser.logMessage("PROCESSING GROUP: %s" + toString()+stack);
      }

      StackMarker marker = new StackMarker();

      stack.push(marker);
      stack.addAll(0, this);
      clear();

      startGroup(parser);
      stack.processList(parser, marker);
      endGroup(parser);

   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK))
      {
         parser.logMessage(String.format("PROCESSING %s: %s",
          getClass().getSimpleName(), toString(parser)));
      }

      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK_LIST))
      {
         parser.logMessage("PROCESSING GROUP: %s" + toString());
      }

      StackMarker marker = new StackMarker();
      TeXObjectList stack = toList();
      stack.add(marker);
      clear();

      startGroup(parser);

      stack.processList(parser, marker);

      endGroup(parser);

      parser.push(stack, true);
   }

   public void startGroup(TeXParser parser)
    throws IOException
   {
      parser.startGroup();
   }

   public void endGroup(TeXParser parser)
    throws IOException
   {
      parser.endGroup();
   }

   public TeXObjectList splitTokens(TeXParser parser)
   {
      TeXObjectList list = new TeXObjectList();

      TeXObject obj = getBegin(parser);

      if (obj instanceof MultiToken)
      {
         list.addAll(((MultiToken)obj).splitTokens(parser));
      }
      else
      {
         list.add(obj);
      }

      list.addAll(this);

      obj = getEnd(parser);

      if (obj instanceof MultiToken)
      {
         list.addAll(((MultiToken)obj).splitTokens(parser));
      }
      else
      {
         list.add(obj);
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      return expanded.expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      if (stack != null && stack != parser)
      {
         while (stack.size() > 0)
         {
            expanded.add(stack.remove(0));
         }
      }

      return expanded.expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      return expanded.expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      StackMarker marker = null;

      if (stack != null && stack != parser && !stack.isEmpty())
      {
         marker = new StackMarker();
         expanded.add(marker);

         while (stack.size() > 0)
         {
            expanded.add(stack.remove(0), true);
         }
      }

      TeXObjectList result = expanded.expandfully(parser);

      if (marker != null)
      {
         int n = -1;

         for (int i = 0; i < result.size(); i++)
         {
            TeXObject obj = result.get(i);

            if (n != -1)
            {
               stack.add(obj);
            }
            else if (obj.equals(marker))
            {
               n = i;
            }
         }

         if (n != -1)
         {
            result.setSize(n);
         }
      }

      return result;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%s%s%s", getBegin(parser).toString(parser),
         super.toString(parser),
          getEnd(parser).toString(parser));
   }

   @Override
   public String toString()
   {
      return String.format("%s{%s}", getClass().getSimpleName(),
       super.toString());
   }

   @Override
   public String format()
   {
      return "{"+super.format()+"}";
   }

   public TeXObject getBegin(TeXParser parser)
   {
      return parser.getListener().getBgChar(parser.getBgChar());
   }

   public TeXObject getEnd(TeXParser parser)
   {
      return parser.getListener().getEgChar(parser.getEgChar());
   }
}

