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
         TeXObject obj = TeXParserUtils.resolve(argList.popStack(parser), parser);

         if (obj instanceof ControlSequence)
         {
            String csname = ((ControlSequence)obj).getName();

            if (csname.equals("protect"))
            {
               list.add(obj);

               obj = TeXParserUtils.resolve(argList.popStack(parser), parser);
            }
         }

         if (obj instanceof ControlSequence)
         {
            String csname = ((ControlSequence)obj).getName();

            if (sty.isExclusion(csname))
            {
               list.add(obj);
               obj = popArg(parser, argList);
               list.add(TeXParserUtils.createGroup(parser, obj));
            }
            else
            {
               list.add(obj);
            }
         }
         else if (obj instanceof MathGroup)
         {
            list.add(obj);
         }
         else if (obj instanceof WhiteSpace)
         {
            list.add(obj);
         }
         else if (obj instanceof CharObject)
         {
            int cp = ((CharObject)obj).getCharCode();

            if (Character.isAlphabetic(cp))
            {
               String ucp = obj.toString(parser).toUpperCase();

               if (ucp.length() != Character.charCount(cp))
               {
                  for (int i = 0; i < ucp.length(); )
                  {
                     int c = ucp.codePointAt(i);
                     i += Character.charCount(i);

                     if (obj instanceof Other)
                     {
                        list.add(parser.getListener().getOther(c));
                     }
                     else
                     {
                        list.add(parser.getListener().getLetter(c));
                     }
                  }
               }
               else
               {
                  int tcp = Character.toTitleCase(cp);

                  if (obj instanceof Other)
                  {
                     list.add(parser.getListener().getOther(tcp));
                  }
                  else
                  {
                     list.add(parser.getListener().getLetter(tcp));
                  }
               }

               done = true;
            }
            else
            {
               list.add(obj);
            }
         }
         else if (obj instanceof TeXObjectList)
         {
            TeXObjectList sublist = ((TeXObjectList)obj).createList();

            done = toSentenceCase((TeXObjectList)obj, sublist, parser);

            list.add(sublist, true);
         }
         else if (obj instanceof CaseChangeable)
         {
            list.add(((CaseChangeable)obj).toUpperCase(parser));
            done = true;
         }
         else if (obj != null)
         {
            list.add(obj);
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
