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

public class ExpandAfter extends Primitive implements Expandable
{
   public ExpandAfter()
   {
      this("expandafter");
   }

   public ExpandAfter(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new ExpandAfter(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject firstArg = stack.popStack(parser);

      if (firstArg == null)
      {
         return expandonce(parser);
      }

      TeXObject secondArg = stack.popStack(parser);

      if (secondArg == null)
      {
         secondArg = parser.popStack();
      }

      TeXObjectList list = new TeXObjectList();

      if (secondArg instanceof Group)
      {
         Group grp = (Group)secondArg;

         TeXObject obj = grp.pop();

         if (obj instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)obj).expandonce(parser, grp);

            if (expanded == null)
            {
               grp.push(obj);
            }
            else
            {
               grp.addAll(0, expanded);
            }
         }
         else
         {
            grp.push(obj);
         }
      }
      else if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)secondArg).expandonce(parser, stack);

         if (expanded != null)
         {
            secondArg = expanded;
         }
      }

      if (secondArg instanceof TeXObjectList
      && !(secondArg instanceof Group))
      {
         list.addAll(0, (TeXObjectList)secondArg);
      }
      else
      {
         list.push(secondArg);
      }

      list.push(firstArg);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      TeXObject firstArg = parser.popStack();

      TeXObject secondArg = parser.popStack();

      TeXObjectList list = new TeXObjectList();

      if (secondArg instanceof Group)
      {
         Group grp = (Group)secondArg;

         TeXObject obj = grp.pop();

         if (obj instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)obj).expandonce(parser, grp);

            if (expanded == null)
            {
               grp.push(obj);
            }
            else
            {
               grp.addAll(0, expanded);
            }
         }
         else
         {
            grp.push(obj);
         }
      }
      else if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)secondArg).expandonce(parser);

         if (expanded != null)
         {
            secondArg = expanded;
         }
      }

      if (secondArg instanceof TeXObjectList
      && !(secondArg instanceof Group))
      {
         list.addAll(0, (TeXObjectList)secondArg);
      }
      else
      {
         list.push(secondArg);
      }

      list.push(firstArg);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject firstArg = stack.popStack(parser);

      if (firstArg == null)
      {
         process(parser);
         return;
      }

      TeXObject secondArg = stack.popStack(parser);

      if (secondArg == null)
      {
         secondArg = parser.popStack();
      }

      if (secondArg instanceof Group)
      {
         Group grp = (Group)secondArg;

         TeXObject obj = grp.pop();

         if (obj instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)obj).expandonce(parser, grp);

            if (expanded == null)
            {
               grp.push(obj);
            }
            else
            {
               grp.addAll(0, expanded);
            }
         }
         else
         {
            grp.push(obj);
         }
      }
      else if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)secondArg).expandonce(parser, stack);

         if (expanded != null)
         {
            secondArg = expanded;
         }
      }

      if (secondArg instanceof TeXObjectList
      && !(secondArg instanceof Group))
      {
         stack.addAll(0, (TeXObjectList)secondArg);
      }
      else
      {
         stack.push(secondArg);
      }

      stack.push(firstArg);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject firstArg = parser.popStack();

      TeXObject secondArg = parser.popStack();

      if (secondArg instanceof Group)
      {
         Group grp = (Group)secondArg;

         TeXObject obj = grp.pop();

         if (obj instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)obj).expandonce(parser, grp);

            if (expanded == null)
            {
               grp.push(obj);
            }
            else
            {
               grp.addAll(0, expanded);
            }
         }
         else
         {
            grp.push(obj);
         }
      }
      else if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)secondArg).expandonce(parser);

         if (expanded != null)
         {
            secondArg = expanded;
         }
      }

      if (secondArg instanceof TeXObjectList
      && !(secondArg instanceof Group))
      {
         parser.addAll(0, (TeXObjectList)secondArg);
      }
      else
      {
         parser.push(secondArg);
      }

      parser.push(firstArg);
   }


}
