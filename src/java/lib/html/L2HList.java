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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HList extends ListDec
{
   public L2HList()
   {
      this("list");
   }

   public L2HList(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HList(getName());
   }

   @Override
   public void setup(TeXParser parser, TeXObjectList stack, TeXObject labelCs,
     TeXObject listsettings)
   throws IOException
   {
      super.setup(parser, stack, labelCs, listsettings);

      parser.getListener().getWriteable().writeliteral(
        String.format("<ul class=\"%s\">",
           isInLine() ? "inlinelist" : "displaylist"));
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      parser.getListener().getWriteable().writeliteral("</ul>");

      super.end(parser, stack);
   }
}
