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

   public Group(String text)
   {
      super(text);
   }

   public TeXObjectList toList()
   {
      return (TeXObjectList)super.clone();
   }

   public Object clone()
   {
      Group group = new Group(capacity());

      for (TeXObject object : this)
      {
         group.add((TeXObject)object.clone());
      }

      return group;
   }

   // stack is outside this group
   // TODO: Check for \\expandafter at the end of this

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
    throws IOException
   {
      parser.startGroup();

      processList(parser);

      parser.endGroup();
   }

   protected void processList(TeXParser parser)
    throws IOException
   {
      TeXObjectList before = new TeXObjectList();
      TeXObjectList after = new TeXObjectList();

      MidControlSequence midcs = null;

      TeXObjectList list = (TeXObjectList)this.clone();

      while (list.size() > 0)
      {
         TeXObject object = list.pop();

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
         while (before.size() != 0)
         {
            TeXObject object = before.pop();

            if (object == null)
            {
               break;
            }

            if (object instanceof Declaration)
            {
               pushDeclaration((Declaration)object);
            }

            object.process(parser, before);
         }
      }
      else
      {
         midcs.process(parser, before, after);
      }

      processEndDeclarations(parser);

   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, null);
   }

   public TeXObjectList expandonce(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      Group list = parser.getListener().createGroup();

      TeXObjectList remaining = (TeXObjectList)clone();

      if (stack != null)
      {
         while (stack.size() > 0)
         {
            remaining.add(stack.remove(0));
         }
      }

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.remove(0);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandonce(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      Group list = parser.getListener().createGroup();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.popStack();

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      return expandfully(parser);
   }

   public String toString(TeXParser parser)
   {
      return ""+parser.getBgChar()+super.toString(parser)+parser.getEgChar();
   }

   public String toString()
   {
      return "{"+super.toString()+"}";
   }

}

