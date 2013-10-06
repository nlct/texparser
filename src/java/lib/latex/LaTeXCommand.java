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

import com.dickimawbooks.texparserlib.*;

public class LaTeXCommand extends Command
{
   public LaTeXCommand(String name, boolean isShort, int numParams,
     TeXObject defValue, TeXObject definition)
   {
      this.name = name;
      this.isShort = isShort;
      this.numParams = numParams;
      this.defValue = defValue;
      this.definition = definition;
   }

   public String getName()
   {
      return name;
   }

   public Object clone()
   {
      return new LaTeXCommand(name, isShort, numParams,
        defValue == null ? null : (TeXObject)defValue.clone(),
        (TeXObject)definition.clone());
   }

   // TODO: need to implement this
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      if (numParams == 0)
      {
         definition.process(parser, list);
         return;
      }

      TeXObject[] params = new TeXObject[numParams];

      if (defValue == null)
      {
         params[0] = list.popArg();
      }
      else
      {
         params[0] = list.popArg(parser, '[', ']');

         if (params[0] == null)
         {
            params[0] = defValue;
         }
      }

      for (int i = 1; i < numParams; i++)
      {
         params[i] = list.popArg();
      }

      if (definition instanceof TeXObjectList)
      {
         TeXObjectList stack = (TeXObjectList)definition.clone();

         while (stack.size() > 0)
         {
            TeXObject object = stack.pop();

            if (object == null)
            {
               break;
            }

            if (object instanceof Param)
            {
               params[((Param)object).getDigit()].process(parser, stack);
            }
            else
            {
               object.process(parser, stack);
            }
         }
      }
      else
      {
         if (definition instanceof Param)
         {
            params[((Param)definition).getDigit()].process(parser, list);
         }
         else
         {
            definition.process(parser, list);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      if (numParams == 0)
      {
         definition.process(parser);
         return;
      }

      TeXObject[] params = new TeXObject[numParams];

      if (defValue == null)
      {
         params[0] = parser.popNextArg(isShort);
      }
      else
      {
         params[0] = parser.popNextArg(isShort, '[', ']');

         if (params[0] == null)
         {
            params[0] = defValue;
         }
      }

      for (int i = 1; i < numParams; i++)
      {
         params[i] = parser.popNextArg(isShort);
      }

      if (definition instanceof TeXObjectList)
      {
         TeXObjectList stack = (TeXObjectList)definition.clone();

         while (stack.size() > 0)
         {
            TeXObject object = stack.pop();

            if (object == null)
            {
               break;
            }

            if (object instanceof Param)
            {
               params[((Param)object).getDigit()].process(parser, stack);
            }
            else
            {
               object.process(parser, stack);
            }
         }
      }
      else
      {
         if (definition instanceof Param)
         {
            params[((Param)definition).getDigit()].process(parser);
         }
         else
         {
            definition.process(parser);
         }
      }
   }


   private String name;
   private int numParams;
   private boolean isShort;
   private TeXObject defValue, definition;
}
