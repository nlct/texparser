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

public class Index extends ControlSequence
{
   public Index()
   {
      this("index");
   }

   public Index(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Index(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;

      TeXObject optArg = parser.popNextArg(popStyle, '[', ']');
      String opt = null;

      if (optArg != null)
      {
         if (optArg instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)optArg).expandfully(parser);

            if (expanded != null)
            {
               optArg = expanded;
            }
         }

         opt = optArg.toString(parser);
      }

      TeXObject arg1 = parser.popNextArg(popStyle);

      ((LaTeXParserListener)parser.getListener()).index(opt, arg1);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      byte popStyle = TeXObjectList.POP_SHORT;

      TeXObject optArg = stack.popArg(parser, popStyle, '[', ']');

      String opt = null;

      if (optArg != null)
      {
         if (optArg instanceof Expandable)
         {
            TeXObjectList expanded = 
               ((Expandable)optArg).expandfully(parser, stack);

            if (expanded != null)
            {
               optArg = expanded;
            }
         }

         opt = optArg.toString(parser);
      }

      TeXObject arg1 = stack.popArg(parser, popStyle);

      ((LaTeXParserListener)parser.getListener()).index(opt, arg1);
   }

}
