/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class AtRefAtNumName extends Command
{
   public AtRefAtNumName()
   {
      this("@ref@numname", false);
   }

   public AtRefAtNumName(String name, boolean isStar)
   {
      super(name);
      this.isStar = isStar;
   }

   @Override
   public Object clone()
   {
      return new AtRefAtNumName(getName(), isStar);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String label = popLabelString(parser, stack);

      TeXObjectList expanded = listener.createStack();

      expanded.add(listener.getControlSequence("ref"));

      if (isStar)
      {
         expanded.add(listener.getOther('*'));
      }

      expanded.add(listener.createGroup(label));

      expanded.add(listener.getSpace());

      expanded.add(listener.getOther('('));

      expanded.add(listener.getControlSequence("nameref"));

      if (isStar)
      {
         expanded.add(listener.getOther('*'));
      }

      expanded.add(listener.createGroup(label));

      expanded.add(listener.getOther(')'));

      return expanded;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }

   protected boolean isStar;
}
