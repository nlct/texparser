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
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class CapitaliseFmtWords extends CapitaliseWords
{
   public CapitaliseFmtWords(MfirstucSty sty)
   {
      this(sty, "capitalisefmtwords", MakeFirstUc.EXPANSION_NONE);
   }

   public CapitaliseFmtWords(MfirstucSty sty, String name)
   {
      this(sty, name, MakeFirstUc.EXPANSION_NONE);
   }

   public CapitaliseFmtWords(MfirstucSty sty, String name, byte expansion)
   {
      super(sty, name, expansion);
   }

   public Object clone()
   {
      return new CapitaliseFmtWords(sty, getName(), expansion);
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

      boolean isStar = false;

      if (arg instanceof CharObject && ((CharObject)arg).getCharCode() == '*')
      {
         isStar = true;

         if (stack == parser)
         {
            arg = parser.popNextArg();
         }
         else
         {
            arg = stack.popArg(parser);
         }
      }

      if (expansion == MakeFirstUc.EXPANSION_ONCE)
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
      else if (expansion == MakeFirstUc.EXPANSION_FULL)
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
         expanded.add(new TeXCsRef("MFUcapword"));
         expanded.add(arg);
      }
      else if (arg instanceof TeXObjectList 
                && ((TeXObjectList)arg).size() > 0)
      {
         TeXObjectList list = (TeXObjectList)arg;

         TeXParserListener listener = parser.getListener();

         TeXObject object;

         int wordIdx = 0;

         do
         {
            object = list.peekStack();

            while (object != null && 
                    (isPunctuation(object) || isWordBoundary(parser, object)))
            {
               object = list.popStack(parser);
               expanded.add(object);
               object = list.peekStack();
            }

            if (object == null)
            {
               break;
            }

            if (object instanceof ControlSequence 
                 && (!isStar || (isStar && wordIdx == 0)))
            {
               object = list.popStack(parser);

               expanded.add(object);
               Group grp = listener.createGroup();
               expanded.add(grp);

               grp.add(new TeXCsRef("capitalisewords"));
               object = list.popStack(parser);

               if (object != null)
               {
                  grp.add(object);
                  object = list.peekStack();
               }
            }
            else
            {
               TeXObjectList word = new TeXObjectList();

               while (object != null && !isWordBoundary(parser, object))
               {
                  object = list.popStack(parser);
                  word.add(object);
                  object = list.peekStack();
               }

               if (wordIdx > 0 && sty.isException(word))
               {
                  expanded.addAll(word);
               }
               else
               {
                  expanded.add(new TeXCsRef("MFUcapword"));
                  Group grp = listener.createGroup();
                  grp.addAll(word);
                  expanded.add(grp);
               }
            }

            wordIdx++;
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

}
