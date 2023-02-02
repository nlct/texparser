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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLifDbEmpty extends ControlSequence
{
   public DTLifDbEmpty(DataToolSty sty)
   {
      this("DTLifdbempty", sty);
   }

   public DTLifDbEmpty(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new DTLifDbEmpty(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject dbArg = stack.popArg(parser);

      if (dbArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)dbArg).expandfully(parser, 
            stack);

         if (expanded != null)
         {
            dbArg = expanded;
         }
      }

      TeXObject trueArg = stack.popArg(parser);
      TeXObject falseArg = stack.popArg(parser);

      if (sty.dbEmpty(dbArg.toString(parser)))
      {
         trueArg.process(parser, stack);
      }
      else
      {
         falseArg.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject dbArg = parser.popNextArg();

      if (dbArg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)dbArg).expandfully(parser);

         if (expanded != null)
         {
            dbArg = expanded;
         }
      }

      TeXObject trueArg = parser.popNextArg();
      TeXObject falseArg = parser.popNextArg();

      if (sty.dbEmpty(dbArg.toString(parser)))
      {
         trueArg.process(parser);
      }
      else
      {
         falseArg.process(parser);
      }
   }

   protected DataToolSty sty;
}
