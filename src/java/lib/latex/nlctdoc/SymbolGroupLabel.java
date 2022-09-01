/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class SymbolGroupLabel extends Command
{
   public SymbolGroupLabel()
   {
      this("nlctdocsymbolgrouplabel");
   }

   public SymbolGroupLabel(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new SymbolGroupLabel(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject arg1 = popArg(parser, stack);
      TeXObject arg2 = popArg(parser, stack);
      TeXObject arg3 = popArg(parser, stack);

      if (arg1.toString(parser).equals("@"))
      {
         return listener.createString("@");
      }
      else
      {
         return listener.createString("glssymbols");
      }
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }
}
