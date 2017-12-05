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
   
   protected void processNext(TeXParser parser, TeXObjectList stack)
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

      setContents(parser, object);
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

      if (object instanceof Register)
      {
         setContents(parser, object);
         return;
      }

      parser.push(object);
      processNext(parser, parser);
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

      if (object instanceof Register)
      {
         setContents(parser, object);
         return;
      }

      parser.push(object);
      processNext(parser, stack);
   }

   protected int allocation = -1;
}
