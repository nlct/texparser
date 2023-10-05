/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

public abstract class Register extends ControlSequence implements InternalQuantity
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

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return (TeXObject)getContents(parser).clone();
   }

   @Override
   public void setQuantity(TeXParser parser, TeXObject quantity)
    throws TeXSyntaxException
   {
      parser.getSettings().localSetRegister(getName(), quantity);
   }

   public abstract TeXObject getContents(TeXParser parser)
     throws TeXSyntaxException;

   public abstract void setContents(TeXParser parser, TeXObject contents)
     throws TeXSyntaxException;
   
   public void setContents(TeXParser parser, int value)
    throws TeXSyntaxException
   {
      setContents(parser, new UserNumber(value));
   }

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

      object = TeXParserUtils.resolve(object, parser);

      if (!(object instanceof Register))
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
