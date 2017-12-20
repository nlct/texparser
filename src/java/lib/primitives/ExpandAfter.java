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

   protected void pushTo(TeXParser parser, TeXObjectList stack,
    TeXObjectList list)
   throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXObject firstArg = stack.popToken(popStyle);

      TeXObject secondArg = stack.popToken(popStyle);

      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getControlSequence(
           ((TeXCsRef)secondArg).getName());
      }

      if (secondArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)secondArg).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)secondArg).expandonce(parser, stack);
         }

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
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      pushTo(parser, stack, list);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      pushTo(parser, parser, list);

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
      pushTo(parser, stack, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      pushTo(parser, parser, parser);
   }
}
