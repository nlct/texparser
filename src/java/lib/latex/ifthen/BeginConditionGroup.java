/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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

public class BeginConditionGroup extends Command implements BeginGroupObject
{
   public BeginConditionGroup()
   {
      this("TE@lparen");
   }

   public BeginConditionGroup(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new BeginConditionGroup(getName());
   }

   @Override
   public AbstractGroup createGroup(TeXParser parser)
   {
      return new ConditionGroup();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      AbstractGroup group = createGroup(parser);
      list.add(group);

      stack.popRemainingGroup(parser, group, PopStyle.DEFAULT, this);

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      AbstractGroup group = createGroup(parser);
      list.add(group);

      parser.popRemainingGroup(parser, group, PopStyle.DEFAULT, this);

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      parser.startGroup();
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      parser.startGroup();
   }
}
