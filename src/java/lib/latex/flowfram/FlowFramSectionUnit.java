/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class FlowFramSectionUnit extends ControlSequence
{
   public FlowFramSectionUnit()
   {
      this("FlowFramSectionUnit");
   }

   public FlowFramSectionUnit(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new FlowFramSectionUnit(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String csname = popLabelString(parser, stack);
      boolean isStar = popModifier(parser, stack, '*') == '*';
      TeXObject tocTitle = popOptArg(parser, stack);
      popOptArg(parser, stack);// ignore
      TeXObject title = popArg(parser, stack);

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence(csname));

      if (isStar)
      {
         list.add(listener.getOther('*'));
      }

      if (tocTitle != null)
      {
         list.add(listener.getOther('['));
         list.add(tocTitle, true);
         list.add(listener.getOther(']'));
      }

      list.add(TeXParserUtils.createGroup(listener, title));

      TeXParserUtils.process(list, parser, stack);
   }
}
