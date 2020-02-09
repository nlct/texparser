/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public class NoBreakSpace extends Command
{
   public NoBreakSpace()
   {
      this("nobreakspace");
   }

   public NoBreakSpace(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new NoBreakSpace(getName());
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      parser.getListener().getWriteable().writeCodePoint(0x00A0);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(0x00A0));
      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

}
