/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

public class InlineCode extends ControlSequence
{
   public InlineCode()
   {
      this("code");
   }

   public InlineCode(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new InlineCode(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();
      ControlSequence cs = parser.getControlSequence("setupcodeenvfmts");

      TeXObjectList content = listener.createStack();

      parser.startGroup();

      if (cs != null)
      {
         parser.putControlSequence(true, cs);
      }

      content.add(new TeXCsRef("let"));
      content.add(new TeXCsRef("cmd"));
      content.add(new TeXCsRef("cmdfmt"));

      content.add(new TeXCsRef("@code"));
      content.add(TeXParserUtils.createGroup(listener, arg));

      TeXParserUtils.process(content, parser, stack);

      if (parser.isDebugMode(TeXParser.DEBUG_SETTINGS))
      {
         parser.logMessage("ENDING GROUP AFTER PROCESSING "
          + toString() + " REMAINING STACK: "+stack);
      }

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
