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
package com.dickimawbooks.texparserlib.aux;

import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class AuxIgnoreable extends ControlSequence
{
   public AuxIgnoreable(String name)
   {
      super(name);
   }

   public AuxIgnoreable(String name, boolean hasStarredForm, boolean[] margs)
   {
      super(name);
      this.star = hasStarredForm;
      this.margs = margs;
   }

   public Object clone()
   {
      return new AuxIgnoreable(getName(), star, margs);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = null;

      if (star)
      {
         object = stack.peekStack();

         if (object instanceof CharObject
             && ((CharObject)object).getCharCode() == (int)'*')
         {
            object = (stack == parser ? parser.popNextArg() 
              : stack.popArg(parser));
         }
      }

      if (margs == null)
      {
         return;
      }

      for (boolean isMandatoryArg : margs)
      {
         if (isMandatoryArg)
         {
            object = (stack == parser ?
              parser.popNextArg() : stack.popArg(parser));
         }
         else
         {
            object = (stack == parser ?
                       parser.popNextArg(true, '[', ']')
                       : stack.popArg(parser, '[', ']'));
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean star=false;
   private boolean[] margs = null;
}
