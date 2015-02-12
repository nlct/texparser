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
package com.dickimawbooks.texparserlib.latex.textcase;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public abstract class MakeTextChangeCase extends ControlSequence
{
   public MakeTextChangeCase(String name)
   {
      super(name);
   }

   public abstract TeXObject doChangeCase(CaseChangeable arg, TeXParser parser);

   public TeXObjectList changecase(TeXObjectList list, TeXParser parser)
    throws IOException
   {
      if (list instanceof MathGroup)
      {
         return list;
      }

      TeXObjectList newList = list.createList();

      while (list.size() > 0)
      {
         TeXObject arg = list.expandedPopStack(parser);

         if (arg instanceof TeXObjectList)
         {
            newList.add(changecase((TeXObjectList)arg, parser));
         }
         else if (arg instanceof NoCaseChange)
         {
            newList.add(arg);

            if (list.size() > 0)
            {
               newList.add(list.popStack(parser));
            }
         }
         else if (arg instanceof MathDeclaration)
         {
            newList.add(arg);
            MathDeclaration decl = (MathDeclaration)arg;

            while (list.size() > 0)
            {
               arg = list.expandedPopStack(parser);
               newList.add(arg);

               if (arg instanceof EndDeclaration
                && arg == decl.getEndDeclaration())
               {
                  break;
               }
            }
         }
         else if (arg instanceof CaseChangeable)
         {
            newList.add(doChangeCase((CaseChangeable)arg, parser));
         }
         else
         {
            newList.add(arg);
         }
      }

      return newList;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      if (arg instanceof TeXObjectList)
      {
         arg = changecase((TeXObjectList)arg, parser);
      }
      else if (arg instanceof CaseChangeable)
      {
         arg = doChangeCase((CaseChangeable)arg, parser);
      }

      arg.process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      if (arg instanceof TeXObjectList)
      {
         arg = changecase((TeXObjectList)arg, parser);
      }
      else if (arg instanceof CaseChangeable)
      {
         arg = doChangeCase((CaseChangeable)arg, parser);
      }

      arg.process(parser);
   }
}
