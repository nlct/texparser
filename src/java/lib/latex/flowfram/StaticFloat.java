/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

// make staticfigure and statictable act like figure and table
public class StaticFloat extends GatherEnvContents
{
   public StaticFloat(String name, String floatName)
   {
      super(name);
      this.floatName = floatName;
   }

   @Override
   public Object clone()
   {
      return new StaticFloat(getName(), floatName);
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = popContents(parser, stack);

      list.push(listener.createGroup(floatName));
      list.push(listener.getControlSequence("begin"));

      list.add(listener.getControlSequence("end"));
      list.add(listener.createGroup(floatName));

      TeXParserUtils.process(list, parser, stack);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public boolean isInLine()
   {
      return false;
   }

   String floatName;
}
