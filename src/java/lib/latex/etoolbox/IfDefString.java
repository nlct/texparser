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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class IfDefString extends ControlSequence implements Expandable
{
   public IfDefString()
   {
      this("ifdefstring", false);
   }

   public IfDefString(String name, boolean isCsname)
   {
      super(name);
      this.isCsname = isCsname;
   }

   public Object clone()
   {
      return new IfDefString(getName(), isCsname);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg;

      if (stack == parser)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (isCsname)
      {
         if (arg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (stack == parser)
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

         arg = parser.getListener().getControlSequence(arg.toString(parser));
      }
      else if (arg instanceof TeXCsRef)
      {
         arg = parser.getListener().getControlSequence(((TeXCsRef)arg).getName());
      }

      if (arg instanceof GenericCommand)
      {
         arg = ((GenericCommand)arg).getDefinition();
      }
      else if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)arg).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      TeXObject strArg;

      if (parser == stack)
      {
         strArg = parser.popNextArg();
      }
      else
      {
         strArg = stack.popArg(parser);
      }

      TeXObject truePart;
      TeXObject falsePart;

      if (stack == parser)
      {
         truePart = parser.popNextArg();
         falsePart = parser.popNextArg();
      }
      else
      {
         truePart = stack.popArg(parser);
         falsePart = stack.popArg(parser);
      }

      if (arg.toString(parser).equals(strArg.toString(parser)))
      {
         if (truePart instanceof TeXObjectList 
              && !(truePart instanceof Group))
         {
            return (TeXObjectList)truePart;
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(truePart);
            return list;
         }
      }
      else
      {
         if (falsePart instanceof TeXObjectList 
              && !(falsePart instanceof Group))
         {
            return (TeXObjectList)falsePart;
         }
         else
         {
            TeXObjectList list = new TeXObjectList();
            list.add(falsePart);
            return list;
         }
      }
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack)
      {
         expandonce(parser).process(parser);
      }
      else
      {
         expandonce(parser, stack).process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean isCsname;
}
