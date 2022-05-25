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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class If extends Primitive implements Expandable
{
   public If()
   {
      this("if");
   }

   public If(String name)
   {
      super(name, true);
   }

   @Override
   public Object clone()
   {
      return new If(getName());
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      if (istrue(parser, stack))
      {
         doTruePart(parser, stack, list);
      }
      else
      {
         doFalsePart(parser, stack, list);
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      if (istrue(parser, parser))
      {
         doTruePart(parser, parser, list);
      }
      else
      {
         doFalsePart(parser, parser, list);
      }

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      pending = null;

      if (istrue(parser, stack))
      {
         doTruePart(parser, stack);
      }
      else
      {
         doFalsePart(parser, stack);
      }

      if (pending != null)
      {
         stack.push(pending, true);
         pending = null;
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      pending = null;

      if (istrue(parser, parser))
      {
         doTruePart(parser, parser);
      }
      else
      {
         doFalsePart(parser, parser);
      }

      if (pending != null)
      {
         parser.push(pending, true);
         pending = null;
      }
   }

   public boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXObject firstArg = parser.popToken(popStyle);

      TeXObject secondArg = parser.popToken(popStyle);

      if (firstArg instanceof TeXCsRef)
      {
         firstArg = parser.getControlSequence(((TeXCsRef)firstArg).getName());
      }

      if (secondArg instanceof TeXCsRef)
      {
         secondArg = parser.getControlSequence(((TeXCsRef)secondArg).getName());
      }

      if (firstArg == secondArg)
      {
         return true;
      }

      if (firstArg == null || secondArg == null)
      {
         return false;
      }

      if (firstArg instanceof AssignedMacro)
      {
         firstArg = ((AssignedMacro)firstArg).getUnderlying();
      }

      if (secondArg instanceof AssignedMacro)
      {
         secondArg = ((AssignedMacro)secondArg).getUnderlying();
      }

      return firstArg.equals(secondArg);
   }

   protected void doTruePart(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      doTruePart(parser, stack, null);
   }

   protected void doTruePart(TeXParser parser, TeXObjectList stack,
     TeXObjectList list)
   throws IOException
   {
      TeXObject obj = stack.expandedPopStack(parser);

      if (obj instanceof Else)
      {
         skipToFi(parser, stack);
      }
      else if (!(obj instanceof Fi))
      {
         if (list == null)
         {
            if (pending != null)
            {
               pending.add(obj);
            }
            else if (obj instanceof Paragraph 
               && ((Paragraph)obj).isIncomplete())
            {
               pending = parser.getListener().createStack();
               pending.add(obj);
            }
            else if (parser == stack)
            {
               obj.process(parser);
            }
            else
            {
               obj.process(parser, stack);
            }
         }
         else
         {
            list.add(obj);
         }

         doTruePart(parser, stack, list);
      }
   }

   protected void skipToFi(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj = stack.popToken();

      if (obj instanceof TeXCsRef)
      {
         obj = parser.getListener().getControlSequence(
           ((TeXCsRef)obj).getName());
      }

      if (obj == null)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_EXPECTED, "\\fi");
      }

      if (!(obj instanceof Fi))
      {
         skipToFi(parser, stack);
      }
   }

   protected boolean skipToElse(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject obj = stack.popToken();

      if (obj instanceof TeXCsRef)
      {
         obj = parser.getListener().getControlSequence(
           ((TeXCsRef)obj).getName());
      }

      if (obj == null)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_EXPECTED, "\\fi");
      }

      if (obj instanceof Fi)
      {
         return false;
      }

      if (obj instanceof Else)
      {
         return true;
      }

      return skipToElse(parser, stack);
   }

   protected void doFalsePart(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      doFalsePart(parser, stack, null);
   }

   protected void doFalsePart(TeXParser parser, TeXObjectList stack,
      TeXObjectList expanded)
   throws IOException
   {
      if (!skipToElse(parser, stack))
      {
         return; // no \else
      }

      doRemainingFalsePart(parser, stack, expanded);
   }

   protected void doRemainingFalsePart(TeXParser parser, TeXObjectList stack,
     TeXObjectList list)
   throws IOException
   {
      TeXObject obj = stack.expandedPopStack(parser);

      if (!(obj instanceof Fi))
      {
         if (list == null)
         {
            if (pending != null)
            {
               pending.add(obj);
            }
            else if (obj instanceof Paragraph 
               && ((Paragraph)obj).isIncomplete())
            {
               pending = parser.getListener().createStack();
               pending.add(obj);
            }
            else if (parser == stack)
            {
               obj.process(parser);
            }
            else
            {
               obj.process(parser, stack);
            }
         }
         else
         {
            list.add(obj);
         }

         doRemainingFalsePart(parser, stack, list);
      }
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == this) return true;

      if (obj == null || !(obj instanceof TeXObject))
      {
         return false;
      }

      if (obj instanceof AssignedMacro)
      {
         return ((AssignedMacro)obj).getUnderlying().equals(this);
      }

      return obj.getClass().getName().equals(getClass().getName());
   }

   protected TeXObjectList pending = null;
}
