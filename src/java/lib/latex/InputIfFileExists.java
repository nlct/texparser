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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class InputIfFileExists extends ControlSequence
{
   public InputIfFileExists()
   {
      this("InputIfFileExists");
   }

   public InputIfFileExists(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new InputIfFileExists(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String filename = popLabelString(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      TeXPath texPath = new TeXPath(parser, filename);

      listener.addFileReference(texPath);

      if (texPath.exists())
      {
         truePart.process(parser, stack);
         listener.input(texPath, stack);
      }
      else
      {
         falsePart.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      String filename = popLabelString(parser, parser);

      TeXParserListener listener = parser.getListener();

      TeXObject truePart = parser.popNextArg();
      TeXObject falsePart = parser.popNextArg();

      TeXPath texPath = new TeXPath(parser, filename);

      listener.addFileReference(texPath);

      if (texPath.exists())
      {
         truePart.process(parser);
         listener.input(texPath, null);
      }
      else
      {
         falsePart.process(parser);
      }
   }
}
