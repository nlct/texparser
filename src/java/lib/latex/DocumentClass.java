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
      this("documentclass", false);
   }

   public DocumentClass(String name, boolean loadParentOptions)
   {
      super(name);
      this.loadParentOptions = loadParentOptions;
   }

   public Object clone()
   {
      return new DocumentClass(getName(), loadParentOptions);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;

      TeXObject options;
      TeXObject cls;

      if (parser == stack)
      {
         options = stack.popArg(parser, popStyle, '[', ']');
         cls = stack.popArg(parser, popStyle);
      }
      else
      {
         options = parser.popNextArg(popStyle, '[', ']');
         cls = parser.popNextArg(popStyle);
      }

      TeXObjectList expanded = null;

      if (cls instanceof Expandable)
      {
         if (stack == parser)
         {
            expanded = ((Expandable)cls).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)cls).expandfully(parser, stack);
         }
      }

      TeXObject version;

      if (parser == stack)
      {
         version = stack.popArg(parser, popStyle, '[', ']');
      }
      else
      {
         version = parser.popNextArg(popStyle, '[', ']');
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

      loadDocumentClass(listener, keyValList, clsName);
   }

   protected void loadDocumentClass(LaTeXParserListener listener,
       KeyValList options, String clsName)
    throws IOException
   {
      listener.documentclass(options, clsName, loadParentOptions);
   }

   protected boolean loadParentOptions;
}
