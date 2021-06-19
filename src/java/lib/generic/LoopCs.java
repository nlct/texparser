/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.If;

public class LoopCs extends Command
{
   public LoopCs()
   {
      this("loop");
   }

   public LoopCs(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new LoopCs(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expandedList = new TeXObjectList();

      TeXObjectList loopBody = new TeXObjectList();
      If ifCs = null;
      TeXObjectList condition = null;

      TeXObject object = parser.popNextToken(stack);

      while (!parser.isControlSequence(object, "repeat"))
      {
         If objIf = parser.toIf(object);

         if (objIf != null)
         {
            if (condition == null || ifCs == null)
            {
               condition = new TeXObjectList();
            }
            else
            {
               loopBody.add(ifCs);
               loopBody.addAll(condition);
               condition.clear();
            }

            ifCs = objIf;
         }
         else if (condition == null)
         {
            loopBody.add(object);
         }
         else
         {
            condition.add(object);
         }

         object = parser.popNextToken(stack);
      }

      if (condition == null)
      {
         throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_SYNTAX,
           getName());
      }

      while (true)
      {
         expandedList.append(parser.expandOnce(loopBody, stack));

         if (!ifCs.istrue(parser, (TeXObjectList)condition.clone()))
         {
            break;
         }
      }

      return expandedList;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList expandedList = new TeXObjectList();

      TeXObjectList loopBody = new TeXObjectList();
      If ifCs = null;
      TeXObjectList condition = null;

      TeXObject object = parser.popNextToken(stack);

      while (!parser.isControlSequence(object, "repeat"))
      {
         If objIf = parser.toIf(object);

         if (objIf != null)
         {
            if (condition == null || ifCs == null)
            {
               condition = new TeXObjectList();
            }
            else
            {
               loopBody.add(ifCs);
               loopBody.addAll(condition);
               condition.clear();
            }

            ifCs = objIf;
         }
         else if (condition == null)
         {
            loopBody.add(object);
         }
         else
         {
            condition.add(object);
         }

         object = parser.popNextToken(stack);
      }

      if (condition == null)
      {
         throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_SYNTAX,
           getName());
      }

      while (true)
      {
         expandedList.append(parser.expandFully(loopBody, stack));

         if (!ifCs.istrue(parser, (TeXObjectList)condition.clone()))
         {
            break;
         }
      }

      return expandedList;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList loopBody = new TeXObjectList();
      If ifCs = null;
      TeXObjectList condition = null;

      TeXObject object = parser.popNextToken(stack);

      while (!parser.isControlSequence(object, "repeat"))
      {
         If objIf = parser.toIf(object);

         if (objIf != null)
         {
            if (condition == null || ifCs == null)
            {
               condition = new TeXObjectList();
            }
            else
            {
               loopBody.add(ifCs);
               loopBody.addAll(condition);
               condition.clear();
            }

            ifCs = objIf;
         }
         else if (condition == null)
         {
            loopBody.add(object);
         }
         else
         {
            condition.add(object);
         }

         object = parser.popNextToken(stack);

      }

      if (condition == null)
      {
         throw new TeXSyntaxException(parser, TeXSyntaxException.ERROR_SYNTAX,
           getName());
      }

      while (true)
      {
         TeXObjectList list = (TeXObjectList)loopBody.clone();

         parser.processObject(list, stack);

         if (!ifCs.istrue(parser, (TeXObjectList)condition.clone()))
         {
            return;
         }
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}

