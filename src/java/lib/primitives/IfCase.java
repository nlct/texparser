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

public class IfCase extends If
{
   public IfCase()
   {
      this("ifcase");
   }

   public IfCase(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new IfCase(getName());
   }

   public boolean istrue(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      Numerical arg = stack.popNumerical(parser);

      return arg.number(parser) == 0;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      Numerical arg = stack.popNumerical(parser);
      int num = arg.number(parser);

      if (num == 0)
      {
         doCase(parser, stack, list);
      }
      else
      {
         skipToCase(num, parser, stack, list);
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }

   protected void doCase(TeXParser parser, TeXObjectList stack, 
      TeXObjectList list)
   throws IOException
   {
      TeXObject obj = stack.popToken();

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)obj).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)obj).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            stack.addAll(0, expanded);
            obj = stack.popToken();
         }
      }

      if (obj instanceof Else || obj instanceof Or)
      {
         skipToFi(parser, stack);
      }
      else if (!(obj instanceof Fi))
      {
         if (list == null)
         {
            if (parser == stack)
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

         doCase(parser, stack, list);
      }
   }

   protected void skipToCase(int num, TeXParser parser, TeXObjectList stack,
      TeXObjectList list)
   throws IOException
   {
      int currentCase = 0;

      while (currentCase > -1 && currentCase < num)
      {
         currentCase = skipToCase(currentCase, num, parser, stack, list);
      }

      if (currentCase > -1)
      {
         doCase(parser, stack, list);
      }
   }

   protected int skipToCase(int currentCase, int num, TeXParser parser, 
     TeXObjectList stack, TeXObjectList list)
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

      if (obj instanceof Or)
      {
         return currentCase+1;
      }
      else if (obj instanceof Else)
      {
         return num;
      }
      else if (obj instanceof Fi)
      {
         return -1;
      }
      else
      {
         return skipToCase(currentCase, num, parser, stack, list);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.add(0, expandonce(parser, stack));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.add(0, expandonce(parser));
   }
}
