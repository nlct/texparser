/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.primitives.Undefined;

public class TeXCsRef extends ControlSequence implements Expandable,Resolvable
{
   public TeXCsRef(String name)
   {
      super(name);
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   public ControlSequence getControlSequenceIfDefined(TeXParser parser)
   {
      ControlSequence cs = parser.getListener().getControlSequence(getName());

      if (cs == null || (cs instanceof Undefined))
      {
         return this;
      }

      return cs;
   }

   @Override
   public TeXObject resolve(TeXParser parser)
   {
      ControlSequence cs = getControlSequenceIfDefined(parser);

      if (cs instanceof AssignedMacro)
      {
         return ((AssignedMacro)cs).resolve(parser);
      }

      return cs;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = getControlSequenceIfDefined(parser);

      if (cs == this) return null;

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return ((Expandable)cs).expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = getControlSequenceIfDefined(parser);

      if (cs == this) return null;

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return ((Expandable)cs).expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = getControlSequenceIfDefined(parser);

      if (cs == this) return null;

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return ((Expandable)cs).expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = getControlSequenceIfDefined(parser);

      if (cs == this) return null;

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return ((Expandable)cs).expandfully(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(getName());

      cs.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(getName());

      cs.process(parser, stack);
   }

   @Override
   public Object clone()
   {
      return new TeXCsRef(getName());
   }

   @Override
   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (other == null || !(other instanceof TeXCsRef)) return false;

      return getName().equals(((TeXCsRef)other).getName());
   }

}

