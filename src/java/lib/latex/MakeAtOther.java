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
import com.dickimawbooks.texparserlib.primitives.Relax;

public class MakeAtOther extends ControlSequence
  implements CatCodeChanger
{
   public MakeAtOther()
   {
      this("makeatother");
   }

   public MakeAtOther(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new MakeAtOther(getName());
   }

   @Override
   public void applyCatCodeChange(TeXParser parser)
      throws IOException
   {
      parser.setCatCode(true, '@', TeXParser.TYPE_OTHER);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      process(parser);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      applyCatCodeChange(parser);
   }

   @Override
   public ControlSequence getNoOpCommand()
   {
      return new Relax(getName());
   }
}
