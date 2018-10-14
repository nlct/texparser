/*
    Copyright (C) 2018 Nicola L.C. Talbot
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

public class MakeFirstUc extends ControlSequence implements Expandable
{
   public MakeFirstUc()
   {
      this("makefirstuc", EXPANSION_NONE);
   }

   public MakeFirstUc(String name)
   {
      this(name, EXPANSION_NONE);
   }

   public MakeFirstUc(String name, byte expansion)
   {
      super(name);

      if (expansion == EXPANSION_NONE 
          || expansion == EXPANSION_ONCE
          || expansion == EXPANSION_FULL)
      {
         this.expansion = expansion;
      }
      else
      {
         throw new IllegalArgumentException(
           "Invalid expansion value "+expansion);
      }
   }

   public Object clone()
   {
      return new MakeFirstUc(getName(), expansion);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
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

      if (expansion == EXPANSION_ONCE)
      {
         if (arg instanceof Expandable)
         {
            TeXObjectList list;

            if (parser == stack)
            {
               list = ((Expandable)arg).expandonce(parser);
            }
            else
            {
               list = ((Expandable)arg).expandonce(parser, stack);
            }

            if (list != null)
            {
               arg = list;
            }
         }
      }
      else if (expansion == EXPANSION_FULL)
      {
         if (arg instanceof Expandable)
         {
            TeXObjectList list;

            if (parser == stack)
            {
               list = ((Expandable)arg).expandfully(parser);
            }
            else
            {
               list = ((Expandable)arg).expandfully(parser, stack);
            }

            if (list != null)
            {
               arg = list;
            }
         }
      }

      TeXObjectList expanded = new TeXObjectList();

      if (arg instanceof MathGroup)
      {
         expanded.add(arg);
      }
      else if (arg instanceof Group)
      {
         expanded.add(((Group)arg).toUpperCase(parser));
      }
      else if (arg instanceof TeXObjectList 
                && ((TeXObjectList)arg).size() > 0)
      {
         TeXObjectList list = (TeXObjectList)arg;

         TeXObject object = list.popStack(parser);

         TeXParserListener listener = parser.getListener();

         if (object instanceof TeXCsRef)
         {
            object = listener.getControlSequence(((TeXCsRef)object).getName());
         }

         while (object instanceof Protect)
         {
            expanded.add(object);

            object = list.popStack(parser);

            if (object instanceof TeXCsRef)
            {
               object = listener.getControlSequence(((TeXCsRef)object).getName());
            }
         }

         if (object instanceof CaseChangeable)
         {
            expanded.add(((CaseChangeable)object).toUpperCase(parser));
         }
         else if (object instanceof ControlSequence)
         {
            ControlSequence cs = (ControlSequence)object;

            object = list.popStack(parser);

            if (object instanceof Group && ((Group)object).size() > 0)
            {
               expanded.add(cs);
               Group grp = listener.createGroup();
               expanded.add(grp);

               grp.add(new TeXCsRef("glsmakefirstuc"));
               grp.add(object);
            }
            else
            {
               expanded.add(new TeXCsRef("glsmakefirstuc"));
               expanded.add(cs);
               expanded.add(object);
            }
         }
         else
         {
            expanded.add(object);
         }

         expanded.addAll(list);
      }
      else if (arg instanceof CaseChangeable)
      {
         expanded.add(((CaseChangeable)arg).toUpperCase(parser));
      }
      else
      {
         expanded.add(arg);
      }

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      expandonce(parser, stack).process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      expandonce(parser).process(parser);
   }

   public static final byte EXPANSION_NONE=(byte)0;
   public static final byte EXPANSION_ONCE=(byte)1;
   public static final byte EXPANSION_FULL=(byte)2;

   private byte expansion = EXPANSION_NONE;
}
