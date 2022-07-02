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

public class CharSetCatCodeNN extends ControlSequence
  implements CatCodeChanger
{
   public CharSetCatCodeNN()
   {
      this("CharSetCatCodeNN");
   }

   public CharSetCatCodeNN(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new CharSetCatCodeNN(getName());
   }

   @Override
   public void applyCatCodeChange(TeXParser parser) throws IOException
   {
      Numerical cp = TeXParserUtils.popNumerical(parser, stack);
      Numerical cat = TeXParserUtils.popNumerical(parser, stack);

      parser.setCatCode(true, cp.number(parser), cat.number(parser));

      parser.push(TeXParserUtils.createGroup(cat));
      parser.push(TeXParserUtils.createGroup(cp));
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int cp = TeXParserUtils.popInt(parser, stack);
      int cat = TeXParserUtils.popInt(parser, stack);

      parser.setCatCode(true, cp, cat);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
