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

public class Divide extends Primitive
{
   public Divide()
   {
      this("divide");
   }

   public Divide(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Divide(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      Register reg = stack.popRegister(parser);

      if (!(reg instanceof NumericRegister))
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_REGISTER_NOT_NUMERIC);
      }

      TeXObject object = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (object instanceof CharObject
           && ((CharObject)object).getCharCode() == 'b')
      {
         TeXObject prevObject = object;
         object = stack.popToken();

         if (!(object instanceof CharObject
               && ((CharObject)object).getCharCode() == 'y'))
         {
            stack.push(object);
            stack.push(prevObject);

            throw new TeXSyntaxException(parser, 
             TeXSyntaxException.ERROR_NUMBER_EXPECTED,
              'b');
         }
      }
      else
      {
         stack.push(object);
      }

      Numerical num = stack.popNumerical(parser);

      ((NumericRegister)reg).divide(num.number(parser));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }


}
