/*
    Copyright (C) 2018-2022 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class CapitaliseWords extends Command
{
   public CapitaliseWords(MfirstucSty sty)
   {
      this(sty, "capitalisewords", MakeFirstUc.EXPANSION_NONE);
   }

   public CapitaliseWords(MfirstucSty sty, String name)
   {
      this(sty, name, MakeFirstUc.EXPANSION_NONE);
   }

   public CapitaliseWords(MfirstucSty sty, String name, byte expansion)
   {
      super(name);

      if (expansion == MakeFirstUc.EXPANSION_NONE 
          || expansion == MakeFirstUc.EXPANSION_ONCE
          || expansion == MakeFirstUc.EXPANSION_FULL)
      {
         this.expansion = expansion;
      }
      else
      {
         throw new IllegalArgumentException(
           "Invalid expansion value "+expansion);
      }

      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new CapitaliseWords(sty, getName(), expansion);
   }

   public boolean isWordBreakCs(TeXObject object)
   {
      return ((object instanceof ControlSequence) 
              && ((ControlSequence)object).getName().equals("MFUwordbreak"));
   }

   public boolean isWordBoundary(TeXParser parser, TeXObject object)
   {
      return (object instanceof Space || isWordBreakCs(object));
   }

   public boolean isPunctuation(TeXObject object)
   {
      return object instanceof CharObject
       && !Character.isAlphabetic(((CharObject)object).getCharCode());
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
      TeXObject arg = popArg(parser, stack);

      if (expansion == MakeFirstUc.EXPANSION_ONCE)
      {
         arg = TeXParserUtils.expandOnce(arg, parser, stack);
      }
      else if (expansion == MakeFirstUc.EXPANSION_FULL)
      {
         arg = TeXParserUtils.expandFully(arg, parser, stack);
      }

      ControlSequence wordCs;

      if (TeXParserUtils.isTrue("ifMFUhyphen", parser))
      {
         wordCs = parser.getListener().getControlSequence("MFUhyphencapword");
      }
      else
      {
         wordCs = parser.getListener().getControlSequence("MFUcapword");
      }

      TeXObjectList expanded = new TeXObjectList();
      boolean isStart = true;
      boolean spaceFound = false;

      if (arg instanceof MathGroup)
      {
         expanded.add(arg);
      }
      else if (arg instanceof Group)
      {
         expanded.add(wordCs);
         expanded.add(arg);
      }
      else if (parser.isStack(arg) && !arg.isEmpty())
      {
         TeXObjectList list = (TeXObjectList)arg;

         TeXParserListener listener = parser.getListener();

         TeXObject object;

         do
         {
            object = list.peekStack();

            while (object != null && 
                    (isPunctuation(object) || isWordBoundary(parser, object)))
            {
               object = list.popStack(parser);

               if (!spaceFound)
               {
                  spaceFound = (object instanceof Space);
               }

               if (isWordBreakCs(object))
               {
                  object = list.popArg(parser);
               }

               expanded.add(object);

               object = list.peekStack();
            }

            if (object == null)
            {
               break;
            }

            TeXObjectList word = new TeXObjectList();

            while (object != null && !isWordBoundary(parser, object))
            {
               object = list.popStack(parser);
               word.add(object);
               object = list.peekStack();
            }

            if (!spaceFound)
            {
               spaceFound = (object instanceof Space);
            }

            if (!isStart && sty.isException(word))
            {
               expanded.addAll(word);
            }
            else
            {
               expanded.add(wordCs);
               Group grp = listener.createGroup();
               grp.addAll(word);
               expanded.add(grp);
            }

            if (spaceFound)
            {
               isStart = false;
            }
         }
         while (object != null);
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

   protected byte expansion = MakeFirstUc.EXPANSION_NONE;
   protected MfirstucSty sty;
}
