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

public class Options extends ControlSequence
{
   public Options()
   {
      this("options", "and");
   }

   public Options(String name, String andText)
   {
      super(name);
      this.andText = andText;
   }

   @Override
   public Object clone()
   {
      return new Options(getName(), andText);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String label = popLabelString(parser, stack);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      parser.startGroup();

      parser.putControlSequence(true, new TextualContentCommand("andname", andText));

      parser.putControlSequence(true, new AssignedControlSequence(
           "glsseeitemformat", listener.getControlSequence("optionlistitemformat")));

      TeXObjectList content = listener.createStack();

      content.add(listener.getControlSequence("glsxtrtaggedlist"));

      content.add(listener.getControlSequence("optionlisttag"));
      content.add(listener.getControlSequence("optionlisttags"));
      content.add(listener.getControlSequence("optionlistprefix"));
      content.add(listener.createGroup(label));

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

   protected String andText;
}
