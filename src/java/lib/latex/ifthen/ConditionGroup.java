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
package com.dickimawbooks.texparserlib.latex.ifthen;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ConditionGroup extends Group
{
   public ConditionGroup()
   {
      super();
   }

   public ConditionGroup(int capacity)
   {
      super(capacity);
   }

   public Object clone()
   {
      ConditionGroup grp = new ConditionGroup(capacity());

      for (TeXObject obj : this)
      {
         grp.add((TeXObject)obj.clone());
      }

      return grp;
   }

   public TeXObjectList createList()
   {
      return new ConditionGroup(capacity());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObjectList grp = createList();
      list.add(grp);

      flatten();

      TeXObjectList remaining = (TeXObjectList)clone();

      StackMarker marker = null;

      if (stack != null && stack != parser)
      {
         marker = new StackMarker();
         remaining.add(marker);
         remaining.addAll(stack);
         stack.clear();
      }

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.remove(0);

         if (object.equals(marker))
         {
            break;
         }

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandonce(parser, remaining);
         }
         if (expanded == null)
         {
            grp.add(object);
         }
         else
         {
            grp.addAll(expanded);
         }
      }

      if (!remaining.isEmpty())
      {
         stack.addAll(remaining);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObjectList grp = createList();
      list.add(grp);

      flatten();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.remove(0);

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandonce(parser, remaining);
         }

         if (expanded == null)
         {
            grp.add(object);
         }
         else
         {
            grp.addAll(expanded);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObjectList grp = createList();
      list.add(grp);

      flatten();

      TeXObjectList remaining = (TeXObjectList)clone();

      StackMarker marker = null;

      if (stack != null && stack != parser)
      {
         marker = new StackMarker();
         remaining.add(marker);
         remaining.addAll(stack);
         stack.clear();
      }

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.remove(0);

         if (object.equals(marker))
         {
            break;
         }

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser, remaining);
         }
         if (expanded == null)
         {
            grp.add(object);
         }
         else
         {
            grp.addAll(expanded);
         }
      }

      if (!remaining.isEmpty())
      {
         stack.addAll(remaining);
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObjectList grp = createList();
      list.add(grp);

      flatten();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.remove(0);

         TeXObjectList expanded = null;

         if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser, remaining);
         }

         if (expanded == null)
         {
            grp.add(object);
         }
         else
         {
            grp.addAll(expanded);
         }
      }

      return list;
   }
}
