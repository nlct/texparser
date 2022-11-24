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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.Relax;

/* This will normally be defined by ExplSyntaxOn but this is
 provided just in case ExplSyntaxOn isn't detected and also to provide no-op.
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
      parser.setCatCode(true, 9, TeXParser.TYPE_SPACE);
      parser.setCatCode(true, 32, TeXParser.TYPE_SPACE);
      parser.setCatCode(true, 34, TeXParser.TYPE_OTHER);
      parser.setCatCode(true, 38, TeXParser.TYPE_TAB);
      parser.setCatCode(true, 58, TeXParser.TYPE_OTHER);
      parser.setCatCode(true, 94, TeXParser.TYPE_SP);
      parser.setCatCode(true, 95, TeXParser.TYPE_SB);
      parser.setCatCode(true, 124, TeXParser.TYPE_OTHER);
      parser.setCatCode(true, 126, TeXParser.TYPE_ACTIVE);
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
