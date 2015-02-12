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

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();
      TeXObject ignore = parser.popNextArg();

      if (arg instanceof TeXObjectList) return (TeXObjectList)arg;

      TeXObjectList list = new TeXObjectList();
      list.add(arg);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);
      TeXObject ignore = stack.popArg(parser);

      if (arg instanceof TeXObjectList) return (TeXObjectList)arg;

      TeXObjectList list = new TeXObjectList();
      list.add(arg);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();
      TeXObject ignore = parser.popNextArg();

      TeXObjectList list;

      if (arg instanceof Expandable)
      {
         list = ((Expandable)arg).expandfully(parser);

         if (list != null)
         {
            return list;
         }

         if (arg instanceof TeXObjectList)
         {
            return (TeXObjectList)arg;
         }
      }

      list = new TeXObjectList();
      list.add(arg);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);
      TeXObject ignore = stack.popArg(parser);

      TeXObjectList list;

      if (arg instanceof Expandable)
      {
         list = ((Expandable)arg).expandfully(parser, stack);

         if (list != null)
         {
            return list;
         }

         if (arg instanceof TeXObjectList)
         {
            return (TeXObjectList)arg;
         }
      }

      list = new TeXObjectList();
      list.add(arg);

      return list;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();
      TeXObject ignore = parser.popNextArg();

      arg.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject arg = list.popArg(parser);
      TeXObject ignore = list.popArg(parser);

      arg.process(parser, list);
   }

}
