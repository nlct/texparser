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
import com.dickimawbooks.texparserlib.generic.GobbleNumber;

public class CharSetCatCodeNN extends ControlSequence
  implements CatCodeChanger
{
   public CharSetCatCodeNN()
   {
      this("char_set_catcode:nn");
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
      Numerical cp = TeXParserUtils.popNumericalArg(parser, parser);
      Numerical cat = TeXParserUtils.popNumericalArg(parser, parser, true);

      int catCodeId = cat.number(parser);
      CategoryCode catCode;

      try
      {
         catCode = CategoryCode.valueOf(catCodeId);
      }
      catch (IllegalArgumentException e)
      {
         try
         {
            throw new TeXSyntaxException(e, parser,
             TeXSyntaxException.ERROR_INVALID_CAT_CODE, catCodeId);
         }
         catch (TeXSyntaxException tse)
         {
            parser.getTeXApp().error(tse);
         }

         catCode = CategoryCode.ESC;
         cat = UserNumber.ZERO;
      }

      parser.setCategoryCode(true, cp.number(parser), catCode);

      parser.push(TeXParserUtils.createGroup(parser, cat));
      parser.push(TeXParserUtils.createGroup(parser, cp));
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int cp = TeXParserUtils.popInt(parser, stack);
      int catCodeId = TeXParserUtils.popInt(parser, stack, true);

      CategoryCode catCode;

      try
      {
         catCode = CategoryCode.valueOf(catCodeId);
      }
      catch (IllegalArgumentException e)
      {
         try
         {
            throw new TeXSyntaxException(e, parser,
             TeXSyntaxException.ERROR_INVALID_CAT_CODE, catCodeId);
         }
         catch (TeXSyntaxException tse)
         {
            parser.getTeXApp().error(tse);
         }

         catCode = CategoryCode.INVALID;
      }

      parser.setCategoryCode(true, cp, catCode);
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
      return new GobbleNumber(getName(), true);
   }
}
