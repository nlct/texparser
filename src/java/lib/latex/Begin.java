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

public class Begin extends Command
{
   public Begin()
   {
   }

   public String getName()
   {
      return "begin";
   }

   public Object clone()
   {
      return new Begin();
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (list == null)
      {
         process(parser);
         return;
      }

      TeXObject object = list.popArg();

      String name;
      TeXObjectList expanded = null;

      if (object instanceof Expandable)
      {
         expanded = ((Expandable)object).expandfully(parser, list);
      }

      if (expanded == null)
      {
         name = object.toString(parser);
      }
      else
      {
         name = expanded.toString();
      }

      if (name.equals("document"))
      {
         listener.beginDocument(parser);
         return;
      }

      Environment env = listener.createEnvironment(name);

      env.popGroup(parser, list);

      env.process(parser, list);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject object = parser.popNextArg();

      String name;
      TeXObjectList expanded = null;

      if (object instanceof Expandable)
      {
         expanded = ((Expandable)object).expandfully(parser);
      }

      if (expanded == null)
      {
         name = object.toString(parser);
      }
      else
      {
         name = expanded.toString(parser);
      }

      if (name.equals("document"))
      {
         listener.beginDocument(parser);
         return;
      }

      Environment env = listener.createEnvironment(name);

      env.popGroup(parser);

      env.process(parser);
   }

}
