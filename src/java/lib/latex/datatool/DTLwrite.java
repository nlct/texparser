/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

public class DTLwrite extends ControlSequence
{
   public DTLwrite(DataToolSty sty)
   {
      this("DTLwrite", sty);
   }

   public DTLwrite(String name, DataToolSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DTLwrite(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);

      String filename = popLabelString(parser, stack);

      TeXParserListener listener = parser.getListener();
      TeXApp texApp = listener.getTeXApp();

      parser.startGroup();

      if (options != null)
      {
         sty.processIOKeys(options, stack);
      }

      stack.push(listener.getControlSequence("endgroup"));

      DataBase.write(sty, filename, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolSty sty;
}
