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

public class AtSecondOfTwo extends Command
{
   public AtSecondOfTwo()
   {
      this("@secondoftwo");
   }

   public AtSecondOfTwo(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new AtSecondOfTwo(getName());
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
      popArg(parser, stack);// ignore
      TeXObject arg = popArg(parser, stack);

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
      popArg(parser, stack);// ignore
      TeXObject arg = popArg(parser, stack);

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
      parser.popNextArg();// ignore
      TeXObject arg = parser.popNextArg();

      arg.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.popArg(parser);// ignore
      TeXObject arg = stack.popArg(parser);

      arg.process(parser, stack);
   }

}
