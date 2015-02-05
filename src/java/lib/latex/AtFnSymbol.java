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

public class AtFnSymbol extends Command
{
   public AtFnSymbol()
   {
      this("@fnsymbol");
   }

   public AtFnSymbol(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new AtFnSymbol(getName());
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

      switch (arg.number(parser))
      {
         case 0: return new TeXObjectList();
         case 1: return listener.getOther('*');
         case 2: return listener.getOther('†');
         case 3: return listener.getOther('‡');
         case 4: return listener.getOther('§');
         case 5: return listener.getOther('¶');
         case 6: return listener.getOther('‖');
         case 7: return listener.createString("**");
         case 8: return listener.createString("††");
         case 9: return listener.createString("‡‡");
      }

      return new TeXCsRef("@ctrerr");
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

}
