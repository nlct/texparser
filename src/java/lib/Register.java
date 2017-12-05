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
   
   protected TeXObject popValue(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object;

      if (parser == stack)
      {
         object = parser.popNextArg();
      }
      else
      {
         object = stack.popArg(parser);
      }

      return object;
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject object = parser.popStack(parser, true);

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser);

         if (expanded != null)
         {
            parser.addAll(expanded);
            object = parser.popStack(parser, true);
         }
      }

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == '=')
      {
         object = parser.popStack(parser, true);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser);

            if (expanded != null)
            {
               parser.addAll(expanded);
               object = parser.popStack(parser, true);
            }
         }
      }

      TeXObject value;

      if (object instanceof Register)
      {
         value = ((Register)object).getContents(parser);
      }
      else
      {
         parser.push(object);
         value = popValue(parser, parser);
      }

      if (getPrefix() == PREFIX_GLOBAL)
      {
         parser.getSettings().globalSetRegister(getName(), value);
      }
      else
      {
         parser.getSettings().localSetRegister(getName(), value);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object = stack.popStack(parser, true);

      if (object instanceof Expandable)
      {
         TeXObjectList expanded =
            ((Expandable)object).expandfully(parser, stack);

         if (expanded != null)
         {
            stack.addAll(expanded);
            object = stack.popStack(parser, true);
         }
      }

      if (object instanceof CharObject
       && ((CharObject)object).getCharCode() == '=')
      {
         object = stack.popStack(parser, true);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded =
               ((Expandable)object).expandfully(parser, stack);

            if (expanded != null)
            {
               stack.addAll(expanded);
               object = stack.popStack(parser, true);
            }
         }
      }

      TeXObject value;

      if (object instanceof Register)
      {
         value = ((Register)object).getContents(parser);
      }
      else
      {
         parser.push(object);
         value = popValue(parser, stack);
      }

      if (getPrefix() == PREFIX_GLOBAL)
      {
         parser.getSettings().globalSetRegister(getName(), value);
      }
      else
      {
         parser.getSettings().localSetRegister(getName(), value);
      }
   }

   protected int allocation = -1;
}
