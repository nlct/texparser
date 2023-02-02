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
package com.dickimawbooks.texparserlib.latex.pifont;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.ListDec;

public class DingList extends ListDec
{
   public DingList()
   {
      this("dinglist");
   }

   public DingList(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DingList(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      setup(parser, parser, parser.popStack());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      setup(parser, stack, stack.popStack(parser));
   }

   public void setup(TeXParser parser, TeXObjectList stack, TeXObject arg)
      throws IOException
   {
      setup(parser, stack);

      TeXObjectList label = new TeXObjectList();

      label.add(parser.getListener().getControlSequence("ding"));
      label.add(arg);

      TeXObjectList listsettings = new TeXObjectList();

      setup(parser, stack, label, listsettings);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      super.end(parser, stack);
   }

}
