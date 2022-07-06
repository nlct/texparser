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

public class MakeFirstUc extends Command
{
   public MakeFirstUc(MfirstucSty sty)
   {
      this("makefirstuc", EXPANSION_NONE, sty);
   }

   public MakeFirstUc(String name, MfirstucSty sty)
   {
      this(name, EXPANSION_NONE, sty);
   }

   public MakeFirstUc(String name, byte expansion, MfirstucSty sty)
   {
      super(name);
      this.sty = sty;

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
      return new MakeFirstUc(getName(), expansion, sty);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   protected void convert(TeXObjectList argList, TeXObjectList substack,
     TeXParser parser)
     throws IOException
   {
      if (argList.isEmpty())
      {
         return;
      }

      boolean done = true;

      TeXObject object = argList.popStack(parser);

      TeXParserListener listener = parser.getListener();

      if (object instanceof TeXCsRef)
      {
         object = listener.getControlSequence(((TeXCsRef)object).getName());
      }

      if (object instanceof ControlSequence)
      {
         String csname = ((ControlSequence)object).getName();

         if (csname.equals("protect"))
         {// skip
            done = false;
         }
         else if (sty.isBlocker(csname))
         {// finish
         }
         else
         {
            TeXObject map = sty.getMapping(csname);

            if (map != null)
            {
               substack.add(map);
            }
            else if (sty.isExclusion(csname))
            {
               substack.add(object);
               substack.add(TeXParserUtils.createGroup(listener,
                popArg(parser, argList)));

               done = false;
            }
            else if (object instanceof CaseChangeable)
            {
               substack.add(((CaseChangeable)object).toUpperCase(parser));
            }
            else
            {
               TeXObject nextObj = argList.peekStack();

               if ((nextObj instanceof Group) || parser.isBeginGroup(object) != null)
               {
                  nextObj = popArg(parser, argList);

                  if (nextObj.isEmpty())
                  {
                     substack.add(object);
                     substack.add(TeXParserUtils.createGroup(listener, nextObj));
                  }
                  else
                  {
                     substack.add(object);
                     Group grp = listener.createGroup();
                     substack.add(grp);

                     if (parser.isStack(nextObj))
                     {
                        convert((TeXObjectList)nextObj, grp, parser);
                     }
                     else if (nextObj instanceof CaseChangeable)
                     {
                        grp.add(((CaseChangeable)nextObj).toUpperCase(parser));
                     }
                     else
                     {
                        grp.add(listener.getControlSequence("glsmakefirstuc"));
                        grp.add(TeXParserUtils.createGroup(listener, nextObj));
                     }
                  }
               }
               else
               {
                  substack.add(object);
               }
            }
         }

         if (done)
         {
            substack.addAll(argList);
         }
         else if (!argList.isEmpty())
         {
            convert(argList, substack, parser);
         }
      }
      else
      {
         substack.add(listener.getControlSequence("glsmakefirstuc"));
         Group grp = listener.createGroup();
         substack.add(grp);
         grp.add(object);
         grp.addAll(argList);
      }
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

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
      else if (parser.isStack(arg))
      {
         convert((TeXObjectList)arg, expanded, parser);
      }
      else if (arg instanceof CaseChangeable)
      {
         expanded.add(((CaseChangeable)arg).toUpperCase(parser));
      }
      else
      {
         expanded.add(parser.getListener().getControlSequence("glsmakefirstuc"));
         expanded.add(TeXParserUtils.createGroup(parser, arg));
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

   protected MfirstucSty sty;
}
