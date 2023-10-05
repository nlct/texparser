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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public abstract class NumericRegister extends Register implements Numerical
{
   public NumericRegister(String name)
   {
      super(name);
   }

   /**
    * Increments value by given amount. 
    */ 
   public abstract void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException;

   /**
    * Divides this value by given amount. 
    */ 
   public abstract void divide(int divisor);

   /**
    * Multiplies this value by given amount. 
    */ 
   public abstract void multiply(int factor);

   /**
    * Sets this value to the given amount. 
    */ 
   public abstract void setValue(TeXParser parser, Numerical value)
    throws TeXSyntaxException;

   protected TeXObject popValue(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object;

      if (parser == stack)
      {
         object = parser.popNumerical();
      }
      else
      {
         object = stack.popNumerical(parser);
      }

      return object;
   }

   /**
    * Sets the content of this register. The object must expand to a
    * numeric value. 
    */ 
   public void setContents(TeXParser parser, TeXObject object)
    throws TeXSyntaxException
   {
      if (object instanceof AssignedMacro)
      {
         object = ((AssignedMacro)object).getBaseUnderlying();
      }

      if (!(object instanceof Numerical))
      {
         if (object instanceof Expandable)
         {
            try
            {
               TeXObjectList expanded = ((Expandable)object).expandfully(parser);

               if (expanded != null)
               {
                  object = expanded;
               }
            }
            catch (IOException e)
            {
               throw new TeXSyntaxException(e, parser,
                 TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
            }
         }

         object = new UserNumber(parser, object.format());
      }

      if (!(object instanceof Numerical))
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
      }

      setValue(parser, (Numerical)object);
   }
}
