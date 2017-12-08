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

   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList(size());

      list.addAll(this);

      return list;
   }

   public TeXObjectList createList()
   {
      return new Group(capacity());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      StackMarker marker = null;

      if (stack != parser && stack != null)
      {
         marker = new StackMarker();
         add(marker);

         addAll(stack);
         stack.clear();
      }

      startGroup(parser);
      processList(parser, marker);
      endGroup(parser);

      if (!isEmpty())
      {
         stack.addAll(this);
         clear();
      }
   }

   public void process(TeXParser parser)
    throws IOException
   {
      startGroup(parser);

      processList(parser, null);

      endGroup(parser);
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

   protected void processList(TeXParser parser, StackMarker marker)
    throws IOException
   {
      TeXObjectList before = new TeXObjectList();
      TeXObjectList after = new TeXObjectList();

      MidControlSequence midcs = null;

      for (int i = 0; i < size(); i++)
      {
         TeXObject object = get(i);

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object == null)
         {
            break;
         }

         if (object instanceof MidControlSequence)
         {
            midcs = (MidControlSequence)object;
            continue;
         }

         if (midcs == null)
         {
            before.add(object);
         }
         else
         {
            after.add(object);
         }
      }

      if (midcs == null)
      {
         before = null;
         after = null;

         while (size() != 0)
         {
            TeXObject object = remove(0);

            if (object.equals(marker) || object == null)
            {
               break;
            }

            if (object instanceof TeXCsRef)
            {
               object = parser.getListener().getControlSequence(
                  ((TeXCsRef)object).getName());
            }

            if (object instanceof Declaration)
            {
               pushDeclaration((Declaration)object);
            }

            object.process(parser, this);
         }
      }
      else
      {
         clear();
         midcs.process(parser, before, after);
      }

      processEndDeclarations(parser);
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      return expanded.expandonce(parser);
   }

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

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(getBegin(parser));
      expanded.addAll(this);
      expanded.add(getEnd(parser));

      return expanded.expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
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

      return expanded.expandfully(parser);
   }

   public String toString(TeXParser parser)
   {
      return String.format("%s%s%s", getBegin(parser).toString(parser),
         super.toString(parser),
          getEnd(parser).toString(parser));
   }

   public String toString()
   {
      return String.format("%s{%s}", getClass().getSimpleName(),
       super.toString());
   }

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

