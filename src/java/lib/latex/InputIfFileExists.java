/*
    Copyright (C) 2013 Nicola L.C. Talbot
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

   public Object clone()
   {
      return new InputIfFileExists(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      TeXParserListener listener = parser.getListener();

      TeXObject truePart = stack.popArg(parser);
      TeXObject falsePart = stack.popArg(parser);

      TeXPath texPath = new TeXPath(parser, arg.toString(parser));

      listener.addFileReference(texPath);

      if (texPath.exists())
      {
         truePart.process(parser, stack);
         listener.input(texPath);
      }
      else
      {
         falsePart.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      TeXParserListener listener = parser.getListener();

      TeXObject truePart = parser.popNextArg();
      TeXObject falsePart = parser.popNextArg();

      TeXPath texPath = new TeXPath(parser, arg.toString(parser));

      listener.addFileReference(texPath);

      if (texPath.exists())
      {
         truePart.process(parser);
         listener.input(texPath);
      }
      else
      {
         falsePart.process(parser);
      }
   }
}
