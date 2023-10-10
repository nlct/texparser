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
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.latex3.TokenListCommand;

public class DTLdisplaydbAddEnd extends ControlSequence
{
   public DTLdisplaydbAddEnd()
   {
      this("DTLdisplaydbAddEnd");
   }

   public DTLdisplaydbAddEnd(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLdisplaydbAddEnd(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TokenListCommand contentTl = listener.popTokenListCommand(parser, stack);

      ControlSequence cs = parser.getControlSequence("l_datatool_foot_tl");

      if (cs != null)
      {
         contentTl.appendValue(listener.getControlSequence("dtldisplaycr"), 
           parser, stack);
         contentTl.appendValue(cs, parser, stack);
      }

      contentTl.appendValue(listener.getControlSequence("dtldisplayendtab"), 
        parser, stack);

      contentTl.append(listener.getControlSequence("end"));

      contentTl.append(listener.createGroup(
        parser.expandToString(listener.getControlSequence("dtldisplaydbenv"), stack)));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
