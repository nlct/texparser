/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public abstract class AbstractGroup extends AbstractTeXObjectList
{
   public AbstractGroup()
   {
      super();
   }

   public AbstractGroup(int capacity)
   {
      super(capacity);
   }

   public AbstractGroup(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public AbstractGroup createGroup()
   {
      return (AbstractGroup)createList();
   }

   @Override
   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList();
      list.addAll(this);
      return list;
   }

   @Override
   public StackMarker createStackMarker()
   {
      return new GroupMarker();
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      StackMarker marker = null;

      if (stack != parser && stack != null)
      {
         marker = createStackMarker();
         add(marker);

         addAll(stack);
         stack.clear();
      }

      startGroup(parser);
      processList(parser, marker);

      if (!isEmpty())
      {
         stack.addAll(0, this);
         clear();
      }

      endGroup(parser);
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      int index = indexOfMarker(marker);

      if (index == -1)
      {
         process(parser, stack);
         return false;
      }

      startGroup(parser);

      processList(parser, marker);

      if (isEmpty())
      {
         endGroup(parser);
         return false;
      }

      stack.push(getEnd(parser));
      stack.addAll(0, this);
      clear();

      return true;
   }

   @Override
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
      TeXObjectList list = toList();

      MidControlSequence midcs = null;

      for (int i = 0; i < list.size(); i++)
      {
         TeXObject object = list.get(i);

         if (object == null || object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object instanceof AssignedMacro)
         {
            object = ((AssignedMacro)object).getBaseUnderlying();
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

         while (!list.isEmpty())
         {
            TeXObject object = list.remove(0);

            if (object.equals(marker))
            {
               break;
            }

            if (object instanceof TeXCsRef)
            {
               object = parser.getListener().getControlSequence(
                  ((TeXCsRef)object).getName());
            }

            if (object.process(parser, list, marker))
            {
               break;
            }
         }
      }
      else
      {
         midcs.process(parser, before, after);
      }

      clear();

      if (!list.isEmpty())
      {
         addAll(list);
      }
   }

   @Override
   public TeXObjectList deconstruct(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(getBegin(parser));
      list.addAll(this);
      list.add(getEnd(parser));

      return list;
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList deconstructed = deconstruct(parser);

      TeXObjectList list = deconstructed.pop().string(parser);

      list.addAll(deconstructed);

      return list;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return String.format("%s%s%s", getBegin(parser).toString(parser),
         super.toString(parser),
          getEnd(parser).toString(parser));
   }

   public abstract BeginGroupObject getBegin(TeXParser parser);

   public abstract EndGroupObject getEnd(TeXParser parser);

   @Override
   public boolean isPar()
   {
      return false;
   }

}
