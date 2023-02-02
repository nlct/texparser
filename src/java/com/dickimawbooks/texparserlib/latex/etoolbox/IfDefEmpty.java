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

public class IfDefEmpty extends AbstractEtoolBoxCommand
{
   public IfDefEmpty()
   {
      this("ifdefempty", false);
   }

   public IfDefEmpty(String name, boolean isCsname)
   {
      super(name, isCsname);
   }

   public Object clone()
   {
      return new IfDefEmpty(getName(), isCsname);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = popCsArg(parser, stack);

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      TeXObjectList list = parser.getListener().createStack();

      TeXObject arg = TeXParserUtils.resolve(cs, parser);

      if (arg.isEmpty())
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
      ControlSequence cs = popCsArg(parser, stack);

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      TeXObject arg = TeXParserUtils.resolve(cs, parser);

      TeXObject doCode;

      if (arg.isEmpty())
      {
         doCode = truePart;
      }
      else
      {
         doCode = falsePart;
      }

      TeXParserUtils.process(doCode, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
