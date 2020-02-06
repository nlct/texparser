/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

import java.util.Iterator;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;

public class GobbleOpt extends Command
{
   public GobbleOpt(String name)
   {
      this(name, 1, 0);
   }

   public GobbleOpt(String name, int numOptional, int numMandatory, int... modifiers)
   {
      super(name);
      this.numOptional = numOptional;
      this.numMandatory = numMandatory;
      this.modifiers = modifiers;
   }

   public Object clone()
   {
      return new GobbleOpt(getName(), getNumOptional(), getNumMandatory(), getModifiers());
   }

   public int getNumOptional()
   {
      return numOptional;
   }

   public int getNumMandatory()
   {
      return numMandatory;
   }

   public int[] getModifiers()
   {
      return modifiers;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      process(parser);
      return new TeXObjectList();
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      process(parser);
      return new TeXObjectList();
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

   public boolean isModifier(int cp)
   {
      for (int modifier : modifiers)
      {
         if (cp == modifier)
         {
            return true;
         }
      }

      return false;
   }

   public void process(TeXParser parser) throws IOException
   {
      if (modifiers != null && modifiers.length > 0)
      {
         TeXObject obj = parser.popStack();

         if (!(obj instanceof CharObject && isModifier(((CharObject)obj).getCharCode())))
         {
            parser.push(obj);
         }
      }

      for (int i = 0; i < numOptional; i++)
      {
         TeXObject obj = parser.popNextArg('[', ']');

         if (obj == null)
         {
            break;
         }
      }

      for (int i = 0; i < numMandatory; i++)
      {
         parser.popNextArg();
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      if (modifiers != null && modifiers.length > 0)
      {
         TeXObject obj = list.pop();

         if (!(obj instanceof CharObject && isModifier(((CharObject)obj).getCharCode())))
         {
            list.push(obj);
         }
      }

      for (int i = 0; i < numOptional; i++)
      {
         TeXObject obj = list.popArg(parser, '[', ']');

         if (obj == null)
         {
            break;
         }
      }

      for (int i = 0; i < numMandatory; i++)
      {
         list.popArg(parser);
      }
   }

   private int numOptional, numMandatory;
   private int[] modifiers;
}
