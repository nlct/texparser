/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

/**
 * LaTeX environments are actually declarations that may or may
 * not have a corresponding end command. For example, the itemize
 * environment is comprised of the commands <code>\\itemize</code> and
 * <code>\\enditemize</code>, but those commands aren't used
 * explicitly. Instead <code>\\begin{itemize}</code> is used to
 * start the environment. This starts a local scope, defines
 * <code>\\@currenvir</code> to the environment name
 * (<code>itemize</code>) and then does <code>\\itemize</code>.
 * The environment is ended with <code>\\end{itemize}</code>, which
 * does <code>\\enditemize</code> and then ends the scope.
 *
 * Not all declarations have a corresponding end command. For
 * example, <code>\\begin{bfseries}text\\end{bfseries}</code>
 * is essentially equivalent to
 * <code>{\\def\\@currenvir{bfseries}\\bfseries text}</code>.
 * In this case <code>\\end{bfseries}</code> simply ends the local
 * scope as the command <code>endbfseries</code> doesn't exist.
 *
 * (This is why <code>\\newcommand</code> doesn't allow command names that
 * start with "end", in case the substring after "end" happens to
 * match a declaration, as it will then be used at the end of the
 * environment, whenever that declaration name is used as an
 * environment.)
 */ 
public class Begin extends ControlSequence
{
   public Begin()
   {
      this("begin");
   }

   public Begin(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Begin(getName());
   }

   /**
    * Hook performed after environment name is read but before scope
    * starts.
    */ 
   protected void beginHook(String name, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
   }

   protected void doBegin(TeXParser parser, TeXObjectList stack, String name)
     throws IOException
   {
      ControlSequence cs = parser.getListener().getControlSequence(name);

      TeXParserUtils.process(cs, parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String name = popLabelString(parser, stack);

      if (parser.isDebugMode(TeXParser.DEBUG_DECL))
      {
         parser.logMessage("BEGIN ENV: "+name);
      }

      beginHook(name, parser, stack);

      if (name.equals("document"))
      {
         listener.beginDocument(stack);
         return;
      }

      if (listener.isVerbEnv(name))
      {
         ControlSequence verbCs = listener.getControlSequence(name);

         TeXObjectList contents = new TeXObjectList();

         while (true)
         {
            TeXObject token = stack.pop();

            if (token == null)
            {
               parser.debugMessage(TeXParser.DEBUG_IO, 
                 "End of stack found while peeking in "+name);
            }

            if (token instanceof ControlSequence
              && ((ControlSequence)token).getName().equals("end"))
            {
               ControlSequence cs = (ControlSequence)token;
               token = stack.peekStack();

               if (parser.isBeginGroup(token) != null)
               {
                  TeXObject arg = popArg(parser, stack);

                  if (name.equals(arg.toString(parser)))
                  {
                     break;
                  }
               }
               else
               {
                  contents.add(cs);
               }
            }
            else
            {
               contents.add(token);
            }
         }

         verbCs.process(parser, contents);

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

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
