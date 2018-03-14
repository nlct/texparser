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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;

public class Obsolete extends ControlSequence
{
   public Obsolete(ControlSequence orgCommand, ControlSequence replacementCommand)
   {
      super(orgCommand.getName());
      this.orgCommand = orgCommand;
      this.replacementCommand = replacementCommand;
   }

   public Object clone()
   {
      return new Obsolete((ControlSequence)orgCommand.clone(),
        (ControlSequence)replacementCommand.clone());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      byte popStyle = TeXObjectList.POP_RETAIN_IGNOREABLES;

      if (parser.isMathMode() && orgCommand instanceof TeXFontDeclaration)
      {
         String mathdecl = "math"+orgCommand.getName();

         ControlSequence cs = parser.getControlSequence(mathdecl);

         if (cs != null)
         {
            TeXObjectList repl = new TeXObjectList();

            repl.add(cs);

            Group grp = listener.createGroup();
            repl.add(grp);

            StringBuilder builder = new StringBuilder();
            builder.append(orgCommand.toString(parser));

            TeXObject obj;

            if (stack == parser)
            {
               obj = parser.popStack(popStyle);
            }
            else
            {
               obj = stack.popStack(parser, popStyle);
            }

            while (obj instanceof Ignoreable)
            {
               repl.add(obj);
               builder.append(obj.toString(parser));

               if (stack == parser)
               {
                  obj = parser.popStack(popStyle);
               }
               else
               {
                  obj = stack.popStack(parser, popStyle);
               }
            }

            while (obj != null)
            {
               if (obj instanceof TeXCsRef)
               {
                  obj = listener.getControlSequence(((TeXCsRef)obj).getName());
               }

               if ((obj instanceof Declaration
                    && ((Declaration)obj).isModeSwitcher())
                 || obj instanceof Begin
                 || obj instanceof End
                 || obj instanceof EndDeclaration
                 || obj instanceof TeXFontDeclaration)
               {
                  break;
               }

               builder.append(obj.toString(parser));
               grp.add(obj);

               if (stack == parser)
               {
                  obj = parser.popStack(popStyle);
               }
               else
               {
                  obj = stack.popStack(parser, popStyle);
               }
            }

            if (obj != null)
            {
               stack.push(obj);
            }

            listener.substituting(builder.toString(), repl.toString(parser));

            if (parser == stack)
            {
               repl.process(parser);
            }
            else
            {
               repl.process(parser, stack);
            }

            return;
         }

         if (parser == stack)
         {
            orgCommand.process(parser);
         }
         else
         {
            orgCommand.process(parser, stack);
         }

         return;
      }

      listener.substituting( 
        orgCommand.toString(parser), replacementCommand.toString(parser));

      if (parser == stack)
      {
         replacementCommand.process(parser);
      }
      else
      {
         replacementCommand.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   public ControlSequence getOriginalCommand()
   {
      return orgCommand;
   }

   public ControlSequence getReplacementCommand()
   {
      return replacementCommand;
   }

   public String toString()
   {
      return orgCommand.toString();
   }

   public String toString(TeXParser parser)
   {
      return orgCommand.toString(parser);
   }

   private ControlSequence orgCommand, replacementCommand;
}
