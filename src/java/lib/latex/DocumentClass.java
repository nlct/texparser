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

public class DocumentClass extends ControlSequence
{
   public DocumentClass()
   {
   }

   public String getName()
   {
      return "documentclass";
   }

   public Object clone()
   {
      return new DocumentClass();
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      TeXObject options = list.popArg(parser, '[', ']');

      TeXObject cls = list.popArg();

      TeXObjectList expanded = null;

      if (cls instanceof Expandable)
      {
         expanded = ((Expandable)cls).expandfully(parser, list);
      }

      String clsName;

      if (expanded == null)
      {
         clsName = cls.toString(parser);
      }
      else
      {
         clsName = expanded.toString(parser);
      }

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.documentclass(parser, keyValList, clsName);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject options = parser.popNextArg(true, '[', ']');

      TeXObject cls = parser.popNextArg(true);

      TeXObjectList expanded = null;

      if (cls instanceof Expandable)
      {
         expanded = ((Expandable)cls).expandfully(parser);
      }

      String clsName;

      if (expanded == null)
      {
         clsName = cls.toString(parser);
      }
      else
      {
         clsName = expanded.toString(parser);
      }

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      listener.documentclass(parser, keyValList, clsName);
   }
}
