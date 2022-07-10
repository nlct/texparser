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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;

public class Protect extends NoExpand
{
   public Protect()
   {
      this("protect");
   }

   public Protect(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Protect(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject obj = stack.popStack(parser);

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(new AssignedControlSequence(getName()+" ", this, true));

      if (obj.canExpand())
      {
         String csname;

         if (obj instanceof ControlSequence)
         {
            csname = ((ControlSequence)obj).getName()+" ";
         }
         else
         {
            csname = obj.format()+" ";
         }

         expanded.add(new AssignedControlSequence(csname, obj, true));
      }
      else
      {
         expanded.add(obj);
      }

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
   }
}
