/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HItem extends ListItem
{
   public L2HItem()
   {
      this("item");
   }

   public L2HItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HItem(getName());
   }

   @Override
   public void makelabel(TeXParser parser, TeXObjectList stack,
     TrivListDec trivList, TeXObject label)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (trivList.isInLine())
      {
         listener.writeliteral("<span class=\"inlineitem\">");
      }
      else
      {
         if (listener.isIfTrue(listener.getControlSequence("if@nmbrlist")))
         {
            listener.writeliteral("<li><span class=\"numitem\">");
         }
         else
         {
            listener.writeliteral("<li><span class=\"bulletitem\">");
         }
      }

      if (parser == stack || stack == null)
      {
         label.process(parser);
      }
      else
      {
         label.process(parser, stack);
      }

      listener.writeliteral("</span>");
   }

}
