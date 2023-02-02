/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class TeXParserSetUndefAction extends ControlSequence
{
   public TeXParserSetUndefAction()
   {
      this(UndefAction.UNKNOWN);
   }

   public TeXParserSetUndefAction(UndefAction action)
   {
      this("TeXParserSetUndefAction", action);
   }

   public TeXParserSetUndefAction(String name, UndefAction action)
   {
      super(name);
      this.action = action;
   }

   @Deprecated
   public TeXParserSetUndefAction(int action)
   {
      this("TeXParserSetUndefAction", action);
   }

   @Deprecated
   public TeXParserSetUndefAction(String name, int actionId)
   {
      super(name);

      switch (actionId)
      {
         case 0:
           action = UndefAction.ERROR;
         break;
         case 1:
           action = UndefAction.WARN;
         break;
         case 2:
           action = UndefAction.MESSAGE;
         break;
         case 3:
           action = UndefAction.IGNORE;
         break;
         default:
            action = UndefAction.UNKNOWN;
      }
   }

   @Override
   public Object clone()
   {
      return new TeXParserSetUndefAction(getName(), action);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      UndefAction currentAction = action;

      if (action == UndefAction.UNKNOWN)
      {
         Numerical num = popNumericalArg(parser, stack);

         int id = num.number(parser);

         switch (id)
         {
            case 0:
              currentAction = UndefAction.ERROR;
            break;
            case 1:
              currentAction = UndefAction.WARN;
            break;
            case 2:
              currentAction = UndefAction.MESSAGE;
            break;
            case 3:
              currentAction = UndefAction.IGNORE;
            break;
            default:
              throw new TeXSyntaxException(parser, 
               TeXSyntaxException.ERROR_GENERIC, "Invalid undef action: "+id);
         }
      }

      ((DefaultTeXParserListener)parser.getListener()).setUndefinedAction(currentAction);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   private UndefAction action=UndefAction.UNKNOWN;
}

