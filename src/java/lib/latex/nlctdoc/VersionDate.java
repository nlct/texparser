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
import com.dickimawbooks.texparserlib.latex.*;

public class VersionDate extends ControlSequence
{
   public VersionDate()
   {
      this("versiondate");
   }

   public VersionDate(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new VersionDate(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject versionArg = TeXParserUtils.popArgExpandFully(parser, stack);
      TeXObject dateArg = TeXParserUtils.popArgExpandFully(parser, stack);

      TeXObjectList list = TeXParserUtils.toList(versionArg, parser);
      list.trim();

      parser.putControlSequence(true, new GenericCommand(true,
        "nlct@version", null, list));

      TeXObjectList combinedList = listener.createStack();

      if (!list.isEmpty())
      {
         combinedList.addAll(listener.createString("version"));
         combinedList.add(listener.getSpace());
         combinedList.add(new TeXCsRef("nlct@version"));
      }

      list = TeXParserUtils.toList(dateArg, parser);
      list.trim();

      parser.putControlSequence(true, new GenericCommand(true,
        "nlct@date", null, list));

      if (!list.isEmpty())
      {
         combinedList.add(listener.getSpace());
         combinedList.add(new TeXCsRef("nlct@date"));
      }

      parser.putControlSequence(true,
        new GenericCommand(true, "@date", null, combinedList));
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
