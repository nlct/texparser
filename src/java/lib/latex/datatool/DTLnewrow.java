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

public class DTLnewrow extends ControlSequence
{
   public DTLnewrow(DataToolSty sty)
   {
      this("DTLnewrow", sty);
   }

   public DTLnewrow(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new DTLnewrow(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.popArg(parser);

      boolean isStar = false;

      if (object instanceof CharObject
        && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         object = stack.popArg(parser);
      }

      try
      {
         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser,
             stack);

            if (expanded != null)
            {
               object = expanded;
            }
         }

         sty.addNewRow(object.toString(parser));
      }
      catch (LaTeXSyntaxException e)
      {
         if (!isStar)
         {
            throw (e);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject object = parser.popNextArg();

      boolean isStar = false;

      if (object instanceof CharObject
        && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         object = parser.popNextArg();
      }

      try
      {
         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser);

            if (expanded != null)
            {
               object = expanded;
            }
         }

         sty.addNewRow(object.toString(parser));
      }
      catch (LaTeXSyntaxException e)
      {
         if (!isStar)
         {
            throw (e);
         }
      }
   }

   protected DataToolSty sty;
}
