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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class TeXCsRef extends Command
{
   public TeXCsRef(String name)
   {
      super(name);
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObject cs = parser.getListener().getControlSequence(getName());

      if (cs == null) return null;

      if (cs instanceof AssignedMacro)
      {
         cs = ((AssignedMacro)cs).getBaseUnderlying();
      }

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      TeXObjectList expanded = ((Expandable)cs).expandonce(parser);

      if (expanded == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return expanded;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject cs = parser.getListener().getControlSequence(getName());

      if (cs == null) return null;

      if (cs instanceof AssignedMacro)
      {
         cs = ((AssignedMacro)cs).getBaseUnderlying();
      }

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      TeXObjectList expanded = ((Expandable)cs).expandonce(parser, stack);

      if (expanded == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      TeXObject cs = parser.getListener().getControlSequence(getName());

      if (cs == null) return null;

      if (cs instanceof AssignedMacro)
      {
         cs = ((AssignedMacro)cs).getBaseUnderlying();
      }

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      TeXObjectList expanded = ((Expandable)cs).expandfully(parser);

      if (expanded == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject cs = parser.getListener().getControlSequence(getName());

      if (cs == null) return null;

      if (cs instanceof AssignedMacro)
      {
         cs = ((AssignedMacro)cs).getBaseUnderlying();
      }

      if (!(cs instanceof Expandable))
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      TeXObjectList expanded = ((Expandable)cs).expandfully(parser, stack);

      if (expanded == null)
      {
         TeXObjectList list = new TeXObjectList();
         list.add(cs);
         return list;
      }

      return expanded;
   }

   public void process(TeXParser parser)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(getName());

      cs.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(getName());

      cs.process(parser, stack);
   }

   public Object clone()
   {
      return new TeXCsRef(getName());
   }

   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (other == null || !(other instanceof TeXCsRef)) return false;

      return getName().equals(((TeXCsRef)other).getName());
   }
}

