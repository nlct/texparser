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

public class MFUsentencecase extends Command
{
   public MFUsentencecase(MfirstucSty sty)
   {
      this("MFUsentencecase", sty);
   }

   public MFUsentencecase(String name, MfirstucSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new MFUsentencecase(getName(), sty);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   protected boolean toSentenceCase(TeXObjectList argList, 
     TeXObjectList list, TeXParser parser)
     throws IOException
   {
      boolean done = false;

      while (!argList.isEmpty() && !done)
      {
         TeXObject obj = argList.popStack(parser);

         if (obj instanceof ControlSequence)
         {
            if (sty.isExclusion(((ControlSequence)obj).getName()))
            {
               list.add(obj);
               obj = popArg(parser, argList);
               list.add(TeXParserUtils.createGroup(parser, obj));
            }
            else if (obj instanceof CaseChangeable)
            {
               list.add(((CaseChangeable)obj).toUpperCase(parser));
               done = true;
            }
            else
            {
               list.add(obj);
            }
         }
         else if (parser.isStack(obj))
         {
            done = toSentenceCase((TeXObjectList)obj, list, parser);
         }
         else if (obj instanceof CaseChangeable)
         {
            list.add(((CaseChangeable)obj).toUpperCase(parser));
            done = true;
         }
         else
         {
            list.add(obj);
            done = true;
         }
      }

      list.addAll(argList);

      return done;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList expanded = parser.getListener().createStack();

      TeXObject arg = popArgExpandFully(parser, stack);

      if (parser.isStack(arg))
      {
         toSentenceCase((TeXObjectList)arg, expanded, parser);
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

   protected MfirstucSty sty;
}