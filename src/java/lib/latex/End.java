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

public class End extends ControlSequence
{
   public End()
   {
   }

   public String getName()
   {
      return "end";
   }

   public Object clone()
   {
      return new End();
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject arg = list.popArg();

      TeXObjectList expanded = null;

      if (arg instanceof Expandable)
      {
         expanded = ((Expandable)arg).expandfully(parser, list);
      }

      String name;

      if (expanded == null)
      {
         name = arg.toString(parser);
      }
      else
      {
         name = expanded.toString(parser);
      }

      if (name.equals("document"))
      {
         listener.endDocument(parser);
         return;
      }

      throw new LaTeXSyntaxException(LaTeXSyntaxException.ERROR_EXTRA_END, name);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject arg = parser.popNextArg();

      TeXObjectList expanded = null;

      if (arg instanceof Expandable)
      {
         expanded = ((Expandable)arg).expandfully(parser);
      }

      String name;

      if (expanded == null)
      {
         name = arg.toString(parser);
      }
      else
      {
         name = expanded.toString(parser);
      }

      if (name.equals("document"))
      {
         listener.endDocument(parser);
         return;
      }

      throw new LaTeXSyntaxException(LaTeXSyntaxException.ERROR_EXTRA_END, name);
   }

}
