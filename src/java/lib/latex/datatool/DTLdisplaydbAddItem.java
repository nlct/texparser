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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.latex3.*;

public class DTLdisplaydbAddItem extends ControlSequence
{
   public DTLdisplaydbAddItem()
   {
      this("DTLdisplaydbAddItem");
   }

   public DTLdisplaydbAddItem(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLdisplaydbAddItem(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TokenListCommand contentTl = listener.popTokenListCommand(parser, stack);
      TeXObject item = popArg(parser, stack);
      TeXObject fmt = popArg(parser, stack);
      popArg(parser, stack); // type
      popArg(parser, stack); // row
      popArg(parser, stack); // row idx
      popArg(parser, stack); // col
      popArg(parser, stack); // col idx

      contentTl.append(fmt);
      contentTl.append(TeXParserUtils.createGroup(listener, item));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
