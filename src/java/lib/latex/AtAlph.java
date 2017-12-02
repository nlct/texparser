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

public class AtAlph extends Command
{
   public AtAlph(String name, byte state)
   {
      super(name);

      if (state != UPPER && state != LOWER)
      {
         throw new IllegalArgumentException(String.format(
         "Invalid state '%d' for AtAlph ", state));
      }

      this.state = state;
   }

   public Object clone()
   {
      return new AtAlph(getName(), state);
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      TeXObject obj = getSymbol(parser, parser.popNumber());

      if (obj instanceof TeXObjectList)
      {
         return (TeXObjectList)obj;
      }

      TeXObjectList list = new TeXObjectList();
      list.add(obj);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject obj = getSymbol(parser, stack.popNumber(parser));

      if (obj instanceof TeXObjectList)
      {
         return (TeXObjectList)obj;
      }

      TeXObjectList list = new TeXObjectList();
      list.add(obj);

      return list;
   }

   protected TeXObject getSymbol(TeXParser parser, TeXNumber arg)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      int num = arg.number(parser);

      if (num == 0)
      {
         return new TeXObjectList();
      }

      if (num > 26)
      {
         return new TeXCsRef("@ctrerr");
      }

      return listener.getOther((state == UPPER ? 'A' : 'a')+num-1);
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject obj = getSymbol(parser, parser.popNumber());

      obj.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject obj = getSymbol(parser, stack.popNumber(parser));

      obj.process(parser, stack);
   }

   private byte state;

   public static final byte UPPER = 0;
   public static final byte LOWER = 1;
}
