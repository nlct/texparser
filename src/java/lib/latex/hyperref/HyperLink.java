/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class HyperLink extends Command
{
   public HyperLink()
   {
      this("hyperlink");
   }

   public HyperLink(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new HyperLink(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String link = popLabelString(parser, stack);

      TeXObject text = popArg(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(parser.getListener().createLink(link, text));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String link = popLabelString(parser, stack);

      TeXObject text = popArgExpandFully(parser, stack);

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(parser.getListener().createLink(link, text));

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

}
