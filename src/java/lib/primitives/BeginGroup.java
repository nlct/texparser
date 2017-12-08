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

public class BeginGroup extends Primitive implements Expandable
{
   public BeginGroup()
   {
      this("begingroup");
   }

   public BeginGroup(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new BeginGroup(getName());
   }

   protected Group createGroup(TeXParser parser)
   {
      return parser.getListener().createGroup();
   }

   protected void popRemainingGroup(Group group, TeXParser parser, 
     TeXObjectList stack)
   throws IOException
   {
      TeXObject object = stack.popStack(parser);

      if (object == null)
      {
         throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_NO_EG);
      }

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
            ((TeXCsRef)object).getName());
      }

      if (object instanceof EndGroup || object instanceof EgChar)
      {
         return;
      }

      if (object instanceof BeginGroup)
      {
         Group subGroup = createGroup(parser);
         group.add(subGroup);

         popRemainingGroup(subGroup, parser, stack);
      }
      else
      {
         group.add(object);
      }

      popRemainingGroup(group, parser, stack); 
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      Group group = createGroup(parser);
      list.add(group);

      popRemainingGroup(group, parser, stack);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      Group group = createGroup(parser);
      list.add(group);

      popRemainingGroup(group, parser, parser);

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
      parser.startGroup();
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.startGroup();
   }
}
