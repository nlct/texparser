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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class IfDefString extends AbstractEtoolBoxCommand
{
   public IfDefString()
   {
      this("ifdefstring", false);
   }

   public IfDefString(String name, boolean isCsname)
   {
      super(name, isCsname);
   }

   public Object clone()
   {
      return new IfDefString(getName(), isCsname);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = popCsArg(parser, stack);
      TeXObject arg;

      if (cs instanceof GenericCommand)
      {
         arg = ((GenericCommand)cs).getDefinition();
      }
      else
      {
         arg = TeXParserUtils.expandOnce(cs, parser, stack);
      }

      TeXObject strArg = popArg(parser, stack);

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      TeXObjectList list = parser.getListener().createStack();

      if (!parser.isUndefined(arg)
           && arg.toString(parser).equals(strArg.toString(parser)))
      {
         list.add(truePart, true);
      }
      else
      {
         list.add(falsePart, true);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack)
      {
         expandonce(parser).process(parser);
      }
      else
      {
         expandonce(parser, stack).process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
