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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.GobbleNumber;

public class CatCodeCs extends Primitive 
   implements CatCodeChanger,NumericExpansion,InternalQuantity
{
   public CatCodeCs()
   {
      this("catcode");
   }

   public CatCodeCs(String name)
   {
      super(name, true);
   }

   @Override
   public Object clone()
   {
      return new CatCodeCs(getName());
   }

   @Override
   public void setQuantity(TeXParser parser, TeXObject quantity)
     throws TeXSyntaxException
   {
   }

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandToNumber(parser, stack);
   }

   public TeXNumber expandToNumber(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      return new UserNumber(parser.getCategoryCode(arg.number(parser)).getId());
   }

   @Override
   public void applyCatCodeChange(TeXParser parser) throws IOException
   {
      Numerical arg1 = parser.popNumerical();

      TeXObject eqs = parser.peekStack();

      if (eqs instanceof CharObject 
         && ((CharObject)eqs).getCharCode()=='=')
      {
         eqs = parser.popToken();
      }
      else
      {
         eqs = null;
      }

      try
      {
         Numerical catNum = parser.popNumerical();

         int catCodeId = catNum.number(parser);

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
            catNum = UserNumber.ZERO;
         }

         parser.setCategoryCode(true, arg1.number(parser), catCode);

         parser.push(catNum);
      }
      catch (TeXSyntaxException e)
      {// do nothing (may have been preceded by \the or similar)
      }
      finally
      {
         if (eqs != null)
         {
            parser.push(eqs);
         }

         parser.push(arg1);
      }

   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Numerical arg1 = stack.popNumerical(parser);

      int charCode = arg1.number(parser);

      TeXObject obj = stack.peekStack();

      if (obj instanceof CharObject 
         && ((CharObject)obj).getCharCode()=='=')
      {
         stack.popToken();
      }

      Numerical catNum = stack.popNumerical(parser);

      int catCodeId = catNum.number(parser);

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
         catNum = UserNumber.ZERO;
      }

      parser.setCategoryCode(true, charCode, catCode);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      Numerical arg1 = parser.popNumerical();

      int charCode = arg1.number(parser);

      TeXObject obj = parser.peekStack();

      if (obj instanceof CharObject 
         && ((CharObject)obj).getCharCode()=='=')
      {
         parser.popToken();
      }

      Numerical catNum = parser.popNumerical();

      int catCodeId = catNum.number(parser);

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
         catNum = UserNumber.ZERO;
      }

      parser.setCategoryCode(true, charCode, catCode);
   }

   public ControlSequence getNoOpCommand()
   {
      return new GobbleNumber(getName(), true);
   }
}
