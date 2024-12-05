/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class AtFirstOfTwo extends Command
{
   public AtFirstOfTwo()
   {
      this("@firstoftwo");
   }

   public AtFirstOfTwo(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AtFirstOfTwo(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      popArg(parser, stack);// ignore

      if (parser.isStack(arg)) return (TeXObjectList)arg;

      TeXObjectList list = parser.getListener().createStack();
      list.add(arg);

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      popArg(parser, stack);// ignore

      TeXObject expanded = TeXParserUtils.expandFully(arg, parser, stack);

      if (parser.isStack(expanded))
      {
         return (TeXObjectList)expanded;
      }

      TeXObjectList list = parser.getListener().createStack();

      list.add(expanded);

      return list;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();
      parser.popNextArg();// ignore

      arg.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = stack.popArg(parser);
      stack.popArg(parser);// ignore

      arg.process(parser, stack);
   }

}
