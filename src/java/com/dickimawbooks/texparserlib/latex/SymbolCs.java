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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class SymbolCs extends Command
{
   public SymbolCs()
   {
      this("symbol");
   }

   public SymbolCs(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new SymbolCs(getName());
   }

   protected int popCharCode(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg;

      if (parser == stack)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      if (arg instanceof Numerical)
      {
         return ((Numerical)arg).number(parser);
      }
      else if (arg instanceof TeXObjectList)
      {
         return ((TeXObjectList)arg).popNumerical(parser).number(parser);
      }
      else
      {
         String str = arg.toString(parser);

         try
         {
            return Integer.parseInt(str);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser, 
             TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
         }
      }

   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int num = popCharCode(parser, stack);

      TeXObjectList list = new TeXObjectList();

      list.add(parser.getListener().getOther(num));

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   public void process(TeXParser parser) throws IOException
   {
      int num = popCharCode(parser, parser);

      parser.getListener().getOther(num).process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      int num = popCharCode(parser, stack);

      parser.getListener().getOther(num).process(parser, stack);
   }

}
