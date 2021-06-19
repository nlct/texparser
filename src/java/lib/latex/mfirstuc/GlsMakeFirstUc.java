/*
    Copyright (C) 2018-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsMakeFirstUc extends Command
{
   public GlsMakeFirstUc()
   {
      this("glsmakefirstuc");
   }

   public GlsMakeFirstUc(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsMakeFirstUc(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg;

      if (stack == parser || stack == null)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      TeXObjectList expanded = new TeXObjectList();

      if (arg instanceof MathGroup)
      {
         expanded.add(arg);
      }
      else if (!(arg instanceof AbstractTeXObjectList 
                   && ((AbstractTeXObjectList)arg).size() == 0))
      {
         ControlSequence cs = parser.getControlSequence("MakeTextUppercase");

         if (cs == null)
         {
            expanded.add(new TeXCsRef("MakeUppercase"));
         }
         else
         {
            expanded.add(cs);
         }

         if (arg instanceof TeXObjectList)
         {
            expanded.addAll((TeXObjectList)arg);
         }
         else
         {
            expanded.add(arg);
         }
      }

      return expanded;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      expandonce(parser, stack).process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      expandonce(parser).process(parser);
   }

}
