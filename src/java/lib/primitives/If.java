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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class If extends Primitive implements Expandable
{
   public If()
   {
      this("if");
   }

   public If(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new If(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      if (istrue(parser, stack))
      {
         doTruePart(parser, stack, list);
      }
      else
      {
         doFalsePart(parser, stack, list);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      if (istrue(parser, parser))
      {
         doTruePart(parser, parser, list);
      }
      else
      {
         doFalsePart(parser, parser, list);
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (istrue(parser, stack))
      {
         doTruePart(parser, stack);
      }
      else
      {
         doFalsePart(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      if (istrue(parser, parser))
      {
         doTruePart(parser, parser);
      }
      else
      {
         doFalsePart(parser, parser);
      }
   }

   protected boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject firstArg;

      if (parser == stack || stack == null)
      {
         firstArg = parser.popStack();
      }
      else
      {
         firstArg = stack.popStack(parser);

         if (firstArg == null)
         {
            firstArg = parser.popStack();
         }
      }

      TeXObject secondArg;

      if (parser == stack || stack == null)
      {
         secondArg = parser.popStack();
      }
      else
      {
         secondArg = stack.popStack(parser);

         if (secondArg == null)
         {
            secondArg = parser.popStack();
         }
      }

      return firstArg.equals(secondArg);
   }

   protected void doTruePart(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      doTruePart(parser, stack, null);
   }

   protected void doTruePart(TeXParser parser, TeXObjectList stack,
     TeXObjectList expanded)
   throws IOException
   {
      TeXObject obj;

      if (parser == stack || stack == null)
      {
         obj = parser.popStack();
      }
      else
      {
         obj = stack.popStack(parser);

         if (obj == null)
         {
            obj = parser.popStack();
         }
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expandedObj;

         if (parser == stack || stack == null)
         {
            expandedObj = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expandedObj = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expandedObj != null)
         {
            obj = expandedObj;
         }
      }

      if (obj instanceof Fi)
      {
         return;
      }
      else if (obj instanceof Else)
      {
         skipToFi(parser, stack);
      }
      else if (obj instanceof TeXObjectList
           && !(obj instanceof Group))
      {
         TeXObjectList list = (TeXObjectList)obj;

         for (int i = 0, n = list.size(); i < n; i++)
         {
            obj = list.get(i);

            if (obj instanceof Fi)
            {
               if (i != n-1)
               {
                  if (stack == parser || stack == null)
                  {
                     parser.addAll(0, list.subList(i+1, n-1));
                  }
                  else
                  {
                     stack.addAll(0, list.subList(i+1, n-1));
                  }
               }

               return;
            }
            else if (obj instanceof Else)
            {
               if (i != n-1)
               {
                  if (stack == parser || stack == null)
                  {
                     parser.addAll(0, list.subList(i+1, n-1));
                  }
                  else
                  {
                     stack.addAll(0, list.subList(i+1, n-1));
                  }
               }

               skipToFi(parser, stack);
               return;
            }
            else
            {
               if (expanded == null)
               {
                  if (stack == parser || stack == null)
                  {
                     obj.process(parser);
                  }
                  else
                  {
                     obj.process(parser, stack);
                  }
               }
               else
               {
                  expanded.add(obj);
               }
            }
         }
      }
      else
      {
         if (expanded == null)
         {
            if (parser == stack || stack == null)
            {
               obj.process(parser);
            }
            else
            {
               obj.process(parser, stack);
            }
         }
         else
         {
            expanded.add(obj);
         }

         doTruePart(parser, stack, expanded);
      }
   }

   protected void skipToFi(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj;

      if (parser == stack || stack == null)
      {
         obj = parser.popStack();
      }
      else
      {
         obj = stack.popStack(parser);

         if (obj == null)
         {
            obj = parser.popStack();
         }
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof Fi)
      {
         return;
      }
      else if (obj instanceof TeXObjectList
           && !(obj instanceof Group))
      {
         TeXObjectList list = (TeXObjectList)obj;

         for (int i = 0, n = list.size(); i < n; i++)
         {
            obj = list.get(i);

            if (obj instanceof Fi)
            {
               if (i != n-1)
               {
                  if (stack == parser || stack == null)
                  {
                     parser.addAll(0, list.subList(i+1, n-1));
                  }
                  else
                  {
                     stack.addAll(0, list.subList(i+1, n-1));
                  }
               }

               return;
            }
         }
      }
      else
      {
         skipToFi(parser, stack);
      }
   }

   protected boolean skipToElse(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj;

      if (parser == stack || stack == null)
      {
         obj = parser.popStack();
      }
      else
      {
         obj = stack.popStack(parser);

         if (obj == null)
         {
            obj = parser.popStack();
         }
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof Else || obj instanceof Fi)
      {
         return (obj instanceof Else);
      }
      else if (obj instanceof TeXObjectList
           && !(obj instanceof Group))
      {
         TeXObjectList list = (TeXObjectList)obj;

         for (int i = 0, n = list.size(); i < n; i++)
         {
            obj = list.get(i);

            if (obj instanceof Else || obj instanceof Fi)
            {
               if (i != n-1)
               {
                  if (stack == parser || stack == null)
                  {
                     parser.addAll(0, list.subList(i+1, n-1));
                  }
                  else
                  {
                     stack.addAll(0, list.subList(i+1, n-1));
                  }
               }

               return (obj instanceof Else);
            }
         }
      }

      return skipToElse(parser, stack);
   }

   protected void doFalsePart(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      doFalsePart(parser, stack, null);
   }

   protected void doFalsePart(TeXParser parser, TeXObjectList stack,
      TeXObjectList expanded)
   throws IOException
   {
      if (!skipToElse(parser, stack))
      {
         return; // no \else
      }

      TeXObject obj;

      if (parser == stack || stack == null)
      {
         obj = parser.popStack();
      }
      else
      {
         obj = stack.popStack(parser);

         if (obj == null)
         {
            obj = parser.popStack();
         }
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expandedObj;

         if (parser == stack || stack == null)
         {
            expandedObj = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expandedObj = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expandedObj != null)
         {
            obj = expandedObj;
         }
      }

      if (obj instanceof Fi)
      {
         return;
      }
      else if (obj instanceof TeXObjectList
           && !(obj instanceof Group))
      {
         TeXObjectList list = (TeXObjectList)obj;

         for (int i = 0, n = list.size(); i < n; i++)
         {
            obj = list.get(i);

            if (obj instanceof Fi)
            {
               if (i != n-1)
               {
                  if (stack == parser || stack == null)
                  {
                     parser.addAll(0, list.subList(i+1, n-1));
                  }
                  else
                  {
                     stack.addAll(0, list.subList(i+1, n-1));
                  }
               }

               return;
            }
            else
            {
               if (expanded == null)
               {
                  if (stack == parser || stack == null)
                  {
                     obj.process(parser);
                  }
                  else
                  {
                     obj.process(parser, stack);
                  }
               }
               else
               {
                  expanded.add(obj);
               }
            }
         }
      }
      else
      {
         if (expanded == null)
         {
            if (parser == stack || stack == null)
            {
               obj.process(parser);
            }
            else
            {
               obj.process(parser, stack);
            }
         }
         else
         {
            expanded.add(obj);
         }

         doFalsePart(parser, stack, expanded);
      }
   }

}
