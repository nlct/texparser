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

public abstract class Register extends ControlSequence
{
   public Register(String name)
   {
      super(name);
      setAllowsPrefix(true);
   }

   public void setAllocation(int alloc)
   {
      this.allocation = alloc;
   }

   public int getAllocation()
   {
      return allocation;
   }

   public abstract TeXObject getContents(TeXParser parser)
     throws TeXSyntaxException;

   public abstract void setContents(TeXParser parser, TeXObject contents)
     throws TeXSyntaxException;
   
   protected abstract TeXObject popValue(TeXParser parser, TeXObjectList stack)
      throws IOException;

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == '=')
      {
         object = stack.popToken(TeXObjectList.POP_IGNORE_LEADING_SPACE);
      }

      if (object instanceof TeXCsRef)
      {
         ControlSequence cs = parser.getControlSequence(
          ((TeXCsRef)object).getName());

         if (cs instanceof Register)
         {
            object = cs;
         }
         else
         {
            stack.push(object);
            object = popValue(parser, stack);
         }
      }
      else if (!(object instanceof Register))
      {
         stack.push(object);
         object = popValue(parser, stack);
      }

      if (getPrefix() == PREFIX_GLOBAL)
      {
         parser.getSettings().globalSetRegister(getName(), object);
      }
      else
      {
         parser.getSettings().localSetRegister(getName(), object);
      }
   }

   protected int allocation = -1;
}
