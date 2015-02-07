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

public abstract class Command extends ControlSequence implements Expandable
{
   public Command(String name)
   {
      super(name);
   }

   public Command(String name, boolean isShort)
   {
      super(name, isShort);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, null);
   }

   public abstract TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException;

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList expanded = expandonce(parser);

      if (expanded == null)
      {
         return null;
      }

      TeXObjectList result = expanded.expandfully(parser);

      return result == null ? expanded : result;
   }

   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      if (stack == null)
      {
         return expandfully(parser);
      }

      TeXObjectList expanded = expandonce(parser, stack);

      if (expanded == null)
      {
         return null;
      }

      stack.addAll(0, expanded);

      TeXObjectList list = new TeXObjectList();

      while (stack.size() > 0)
      {
         TeXObject object = stack.remove(0);

         if (object instanceof Ignoreable)
         {
         }
         else if (object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser, stack);

            if (expanded == null)
            {
               list.add(object);
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

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser, stack);

      if (expanded != null)
      {
         stack.addAll(0, expanded);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObjectList expanded = expandonce(parser);

      if (expanded != null)
      {
         parser.addAll(0, expanded);
      }
   }

}
