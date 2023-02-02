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

public class TextBlockCommand extends ControlSequence
{
   public TextBlockCommand(String name, Declaration declaration)
   {
      super(name);
      this.declaration = declaration;
   }

   public Object clone()
   {
      return new TextBlockCommand(getName(), (Declaration)declaration.clone());
   }

   public void process(TeXParser parser) throws IOException
   {
      Group grp = parser.getListener().createGroup();

      grp.add(declaration);
      String argTypes = declaration.getArgTypes();

      if (argTypes != null)
      {
         for (int i = 0; i < argTypes.length(); i++)
         {
            char c = argTypes.charAt(i);

            switch (c)
            {
               case 'm':
                  grp.add(parser.popStack());
               break;
               case 'o':
                  TeXObject obj = parser.popNextArg('[', ']');

                  if (obj != null)
                  {
                     grp.add(parser.getListener().getOther('['));
                     grp.add(obj);
                     grp.add(parser.getListener().getOther(']'));
                  }

               break;
               default:
                 throw new LaTeXSyntaxException(parser, 
                   LaTeXSyntaxException.ILLEGAL_ARG_TYPE, c);
            }
         }
      }

      TeXObject arg = parser.popNextArg();

      grp.add(arg);

      grp.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      Group grp = parser.getListener().createGroup();

      grp.add(declaration);
      String argTypes = declaration.getArgTypes();

      if (argTypes != null)
      {
         for (int i = 0; i < argTypes.length(); i++)
         {
            char c = argTypes.charAt(i);

            switch (c)
            {
               case 'm':
                  grp.add(stack.popStack(parser));
               break;
               case 'o':
                  TeXObject obj = stack.popArg(parser, '[', ']');

                  if (obj != null)
                  {
                     grp.add(parser.getListener().getOther('['));
                     grp.add(obj);
                     grp.add(parser.getListener().getOther(']'));
                  }

               break;
               default:
                 throw new LaTeXSyntaxException(parser, 
                   LaTeXSyntaxException.ILLEGAL_ARG_TYPE, c);
            }
         }
      }


      TeXObject arg = stack.popArg(parser);

      grp.add(arg);

      grp.process(parser, stack);
   }

   private Declaration declaration;
}
