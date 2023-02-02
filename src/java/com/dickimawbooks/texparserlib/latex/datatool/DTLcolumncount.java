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

public class DTLcolumncount extends Command
{
   public DTLcolumncount(DataToolSty sty)
   {
      this("DTLcolumncount", sty);
   }

   public DTLcolumncount(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new DTLcolumncount(getName(), sty);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject dbArg = (stack == null || stack==parser ? 
         parser.popNextArg() : stack.popArg(parser));

      if (dbArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (stack == null || stack == parser)
         {
             expanded = ((Expandable)dbArg).expandfully(parser);
         }
         else
         {
             expanded = ((Expandable)dbArg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            dbArg = expanded;
         }
      }

      TeXObjectList list = new TeXObjectList();

      list.add(new UserNumber(sty.getColumnCount(dbArg.toString(parser))));

      return list;
   }


   protected DataToolSty sty;
}
