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

import com.dickimawbooks.texparserlib.*;

public class ProvidesFile extends ControlSequence
{
   public ProvidesFile()
   {
      this("ProvidesFile");
   }

   public ProvidesFile(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new ProvidesFile(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject options;
      TeXObject nameArg;
      TeXObject version;

      if (parser == stack)
      {
         options = parser.popNextArg('[', ']');
         nameArg = parser.popNextArg();

         if (nameArg instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)nameArg).expandfully(parser);

            if (expanded != null)
            {
               nameArg = expanded;
            }
         }

         version = parser.popNextArg('[', ']');
      }
      else
      {
         options = stack.popArg(parser, '[', ']');
         nameArg = stack.popArg(parser);

         if (nameArg instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)nameArg).expandfully(parser,
               stack);

            if (expanded != null)
            {
               nameArg = expanded;
            }
         }

         version = stack.popArg(parser, '[', ']');
      }

      String name = nameArg.toString(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXPath texPath = listener.getLastFileReference();

      String ext;

      if (texPath == null)
      {
         ext = "tex";
      }
      else
      {
         ext = texPath.getExtension();
      }

      parser.putControlSequence(true, new GenericCommand("@currext", null,
        listener.createString(ext)));
      parser.putControlSequence(true, new GenericCommand("@currname", null,
        listener.createString(name)));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
