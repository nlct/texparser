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

public class CatCodeCs extends Primitive 
   implements CatCodeChanger,NumericExpansion
{
   public CatCodeCs()
   {
      this("catcode");
   }

   public CatCodeCs(String name)
   {
      super(name, true);
   }

   public Object clone()
   {
      return new CatCodeCs(getName());
   }

   public TeXNumber expandToNumber(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      return new UserNumber(parser.getCatCode(arg.number(parser)));
   }

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
         Numerical arg2 = parser.popNumerical();

         parser.push(arg2);

         parser.setCatCode(true, arg1.number(parser), arg2.number(parser));
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

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Numerical arg1 = stack.popNumerical(parser);

      int num1 = arg1.number(parser);

      TeXObject obj = stack.peekStack();

      if (obj instanceof CharObject 
         && ((CharObject)obj).getCharCode()=='=')
      {
         stack.popToken();
      }

      Numerical arg2 = stack.popNumerical(parser);

      int num2 = arg2.number(parser);

      parser.setCatCode(true, num1, num2);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      Numerical arg1 = parser.popNumerical();

      int num1 = arg1.number(parser);

      TeXObject obj = parser.peekStack();

      if (obj instanceof CharObject 
         && ((CharObject)obj).getCharCode()=='=')
      {
         parser.popToken();
      }

      Numerical arg2 = parser.popNumerical();

      int num2 = arg2.number(parser);

      parser.setCatCode(true, num1, num2);
   }

}
