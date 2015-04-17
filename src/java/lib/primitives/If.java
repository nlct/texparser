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
         firstArg = parser.popToken();
      }
      else
      {
         firstArg = stack.popToken();

         if (firstArg == null)
         {
            firstArg = parser.popToken();
         }
      }

      TeXObject secondArg;

      if (parser == stack || stack == null)
      {
         secondArg = parser.popToken();
      }
      else
      {
         secondArg = stack.popToken();

         if (secondArg == null)
         {
            secondArg = parser.popToken();
         }
      }

      if (firstArg instanceof TeXCsRef)
      {
         firstArg = parser.getControlSequence(((TeXCsRef)firstArg).getName());
      }

      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getControlSequence(((TeXCsRef)secondArg).getName());
      }

      if (firstArg == secondArg)
      {
         return true;
      }

      if (firstArg == null || secondArg == null)
      {
         return false;
      }

      if (firstArg instanceof AssignedMacro)
      {
         firstArg = ((AssignedMacro)firstArg).getUnderlying();
      }

      if (secondArg instanceof AssignedMacro)
      {
         secondArg = ((AssignedMacro)secondArg).getUnderlying();
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
            if (obj instanceof Group || expanded.isEmpty())
            {
               obj = expanded;
            }
            else
            {
               obj = expanded.remove(0);

               if (!expanded.isEmpty())
               {
                  if (stack == null)
                  {
                     parser.addAll(0, expanded);
                  }
                  else
                  {
                     stack.addAll(0, expanded);
                  }
               }
            }
         }
      }

      if (obj.equals(ELSE))
      {
         return true;
      }
      else if (obj.equals(FI))
      {
         return false;
      }
      else if (obj instanceof TeXObjectList
           && !(obj instanceof Group))
      {
         TeXObjectList list = (TeXObjectList)obj;

         for (int i = 0, n = list.size(); i < n; i++)
         {
            obj = list.get(i);

            boolean isElse = obj.equals(ELSE);

            if (isElse || obj.equals(FI))
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

               return isElse;
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

      doRemainingFalsePart(parser, stack, expanded);
   }

   protected void doRemainingFalsePart(TeXParser parser, TeXObjectList stack,
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
            if (obj instanceof Group || expandedObj.isEmpty())
            {
               obj = expandedObj;
            }
            else
            {
               obj = expandedObj.remove(0);

               if (!expandedObj.isEmpty())
               {
                  if (stack == null)
                  {
                     parser.addAll(0, expandedObj);
                  }
                  else
                  {
                     stack.addAll(0, expandedObj);
                  }
               }
            }
         }
      }

      if (obj.equals(FI))
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

            if (obj.equals(FI))
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

         doRemainingFalsePart(parser, stack, expanded);
      }
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;

      if (obj == null || !(obj instanceof TeXObject))
      {
         return false;
      }

      if (obj instanceof AssignedMacro)
      {
         return ((AssignedMacro)obj).getUnderlying().equals(this);
      }

      return obj.getClass().getName().equals(getClass().getName());
   }

   public static final Else ELSE = new Else();
   public static final Fi FI = new Fi();
}
