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

   @Override
   public Object clone()
   {
      return new Obsolete((ControlSequence)orgCommand.clone(),
        (ControlSequence)replacementCommand.clone());
   }

   @Override
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
            if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
            {
               parser.logMessage(String.format("OBSOLETE %s -> %s", orgCommand, cs));
            }

            TeXObjectList repl = new TeXObjectList();

            repl.add(cs);

            Group grp = listener.createGroup();
            repl.add(grp);

            StringBuilder builder = new StringBuilder();
            builder.append(orgCommand.toString(parser));

            TeXObject obj = TeXParserUtils.pop(parser, stack, popStyle);

            while (obj instanceof Ignoreable)
            {
               repl.add(obj);
               builder.append(obj.toString(parser));

               obj = TeXParserUtils.pop(parser, stack, popStyle);
            }

            while (obj != null)
            {
               obj = TeXParserUtils.resolve(obj, parser);

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

               obj = TeXParserUtils.pop(parser, stack, popStyle);
            }

            if (obj != null)
            {
               stack.push(obj);
            }

            listener.substituting(builder.toString(), repl.toString(parser));

            TeXParserUtils.process(repl, parser, stack);

         }
         else
         {
            TeXParserUtils.process(orgCommand, parser, stack);
         }
      }
      else
      {
         listener.substituting( 
           orgCommand.toString(parser), replacementCommand.toString(parser));

         TeXParserUtils.process(replacementCommand, parser, stack);
      }
   }

   @Override
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

   @Override
   public String toString()
   {
      return orgCommand.toString();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return orgCommand.toString(parser);
   }

   private ControlSequence orgCommand, replacementCommand;
}
