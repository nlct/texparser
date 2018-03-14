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

public class Begin extends Command
{
   public Begin()
   {
      this("begin");
   }

   public Begin(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Begin(getName());
   }

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

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject object = (parser == stack ? parser.expandedPopStack() 
         : stack.expandedPopStack(parser));

      if (object instanceof Group)
      {
         object = ((Group)object).toList();
      }

      String name = object.toString(parser);

      if (name.equals("document"))
      {
         listener.beginDocument();
         return;
      }

      if (listener.isVerbEnv(name))
      {
         ControlSequence cs = listener.getControlSequence(name);

         TeXObjectList contents = new TeXObjectList();
         String endEnv = String.format("%s%s%s",
            new String(Character.toChars(parser.getBgChar())), 
            name, 
            new String(Character.toChars(parser.getEgChar())));

         while (true)
         {
            object = stack.popStack(parser, TeXObjectList.POP_RETAIN_IGNOREABLES);

            if (object instanceof End
             || (object instanceof TeXCsRef
                 && ((TeXCsRef)object).getName().equals("end")))
            {
               TeXObject arg = stack.popStack(parser);

               if (endEnv.equals(arg.toString(parser)))
               {
                  break;
               }
               else
               {
                  contents.add(object);
                  contents.add(arg);
               }
            }
            else
            {
               contents.add(object);
            }
         }

         cs.process(parser, contents);

         return;
      }

      parser.startGroup();

      parser.putControlSequence(true,// local
        new GenericCommand(true, "@currenvir", null,
           parser.getListener().createString(name)));

      if (stack == null)
      {
         doBegin(parser, parser, name);
      }
      else
      {
         doBegin(parser, stack, name);
      }
   }

   protected void doBegin(TeXParser parser, TeXObjectList stack, String name)
     throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(name);

      if (stack == parser)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
