/*
    Copyright (C) 2024 Nicola L.C. Talbot
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

public class DTLparse extends ControlSequence
{
   public DTLparse(DataToolBaseSty sty)
   {
      this("DTLparse", false, sty);
   }

   public DTLparse(String name, boolean expand, DataToolBaseSty sty)
   {
      super(name);
      this.expand = expand;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLparse(getName(), expand, sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = TeXParserUtils.popControlSequence(parser, stack);
      String csname = cs.getName();
      TeXObject arg = popArg(parser, stack);

      if (expand)
      {
         arg = TeXParserUtils.expandFully(arg, parser, stack);
      }

      parser.putControlSequence(true, DatumCommand.create(sty, csname, arg));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean expand = false;
   protected DataToolBaseSty sty;
}
