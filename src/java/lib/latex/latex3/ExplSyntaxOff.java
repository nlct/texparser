/*
    Copyright (C) 2022-2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Relax;

/**
 * Emulates <code>\ExplSyntaxOff</code>.
 * This command will normally be defined by ExplSyntaxOn but this
 * class is provided just in case ExplSyntaxOn isn't detected and also to provide no-op.
*/
public class ExplSyntaxOff extends ControlSequence
  implements CatCodeChanger
{
   public ExplSyntaxOff()
   {
      this("ExplSyntaxOff");
   }

   public ExplSyntaxOff(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ExplSyntaxOff(getName());
   }

   @Override
   public void applyCatCodeChange(TeXParser parser) throws IOException
   {
      parser.setCategoryCode(true, 9, CategoryCode.SPACE);
      parser.setCategoryCode(true, 32, CategoryCode.SPACE);
      parser.setCategoryCode(true, 34, CategoryCode.OTHER);
      parser.setCategoryCode(true, 38, CategoryCode.TAB);
      parser.setCategoryCode(true, 58, CategoryCode.OTHER);
      parser.setCategoryCode(true, 94, CategoryCode.SP);
      parser.setCategoryCode(true, 95, CategoryCode.SB);
      parser.setCategoryCode(true, 124, CategoryCode.OTHER);
      parser.setCategoryCode(true, 126, CategoryCode.ACTIVE);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      applyCatCodeChange(parser);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public ControlSequence getNoOpCommand()
   {
      return new Relax(getName());
   }
}
