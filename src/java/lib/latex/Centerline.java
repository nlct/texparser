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

import com.dickimawbooks.texparserlib.*;

public class Centerline extends ControlSequence
{
   public Centerline()
   {
      this("centerline");
   }

   public Centerline(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Centerline(getName());
   }

   private void processArg(TeXParser parser, TeXObject arg, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String original = toString(parser)+arg.toString(parser);
      TeXObjectList replacement = new TeXObjectList();

      Group group;

      if (arg instanceof Group)
      {
         group = (Group)arg;
      }
      else
      {
         group = parser.getListener().createGroup();

         group.add(arg);
      }

      group.add(0, listener.getControlSequence("par"));
      group.add(1, listener.getControlSequence("centering"));

      group.add(listener.getControlSequence("par"));

      replacement.add(group);

      listener.substituting(original, replacement.toString(parser));

      if (stack == null)
      {
         replacement.process(parser);
      }
      else
      {
         replacement.process(parser, stack);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      processArg(parser, stack.popStack(parser), stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      processArg(parser, parser.popStack(), null);
   }
}
