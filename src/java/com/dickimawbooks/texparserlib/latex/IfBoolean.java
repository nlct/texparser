/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

public class IfBoolean extends Command
{
   public IfBoolean()
   {
      this("IfBooleanTF", true, true);
   }

   public IfBoolean(String name, boolean hasTrueArg, boolean hasFalseArg)
   {
      super(name, false);
      this.hasTrueArg = hasTrueArg;
      this.hasFalseArg = hasFalseArg;
   }

   public Object clone()
   {
      return new IfBoolean(getName(), hasTrueArg, hasFalseArg);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = getResult(parser, stack);

      if (arg == null)
      {
         return parser.getListener().createStack();
      }

      if (parser.isStack(arg))
      {
         return (TeXObjectList)arg;
      }

      return TeXParserUtils.createStack(parser, arg);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandfully(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = getResult(parser, stack);

      if (arg == null)
      {
         return parser.getListener().createStack();
      }

      arg = TeXParserUtils.expandFully(arg, parser, stack);

      if (parser.isStack(arg))
      {
         return (TeXObjectList)arg;
      }
      else
      {
         return TeXParserUtils.createStack(parser, arg);
      }
   }

   protected boolean isTrue(TeXObject arg, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      arg = TeXParserUtils.resolve(arg, parser);

      if (arg instanceof Numerical)
      {
         return ((Numerical)arg).number(parser) != 0;
      }

      if (arg instanceof TeXBoolean)
      {
         return ((TeXBoolean)arg).booleanValue();
      }

      String str = parser.expandToString(arg, stack).toLowerCase().trim();

      return str.equals("1") || str.equals("true");
   }

   protected TeXObject getResult(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      TeXObject trueArg = null;
      TeXObject falseArg = null;

      if (hasTrueArg)
      {
         trueArg = popArg(parser, stack);
      }

      if (hasFalseArg)
      {
         falseArg = popArg(parser, stack);
      }

      if (isTrue(arg, parser, stack))
      {
         return trueArg;
      }
      else
      {
         return falseArg;
      }
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = getResult(parser, parser);

      if (arg != null)
      {
         arg.process(parser);
      }
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = getResult(parser, stack);

      if (arg != null)
      {
         arg.process(parser, stack);
      }
   }

   protected boolean hasTrueArg, hasFalseArg;
}
