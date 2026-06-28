/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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
      this(name, declaration, TeXMode.INHERIT);
   }

   public TextBlockCommand(String name, Declaration declaration, TeXMode mode)
   {
      super(name);
      this.declaration = declaration;
      this.mode = mode;
   }

   @Override
   public Object clone()
   {
      return new TextBlockCommand(getName(), (Declaration)declaration.clone(), mode);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      parser.startGroup();

      list.add(declaration);
      String argTypes = declaration.getArgTypes();

      if (argTypes != null)
      {
         for (int i = 0; i < argTypes.length(); i++)
         {
            char c = argTypes.charAt(i);

            switch (c)
            {
               case 'm':
                  list.add(TeXParserUtils.createGroup(parser, popArg(parser, stack)));
               break;
               case 'o':
                  TeXObject obj = popOptArg(parser, stack);

                  if (obj != null)
                  {
                     list.add(listener.getOther('['));
                     list.add(obj, true);
                     list.add(listener.getOther(']'));
                  }

               break;
               default:
                 throw new LaTeXSyntaxException(parser, 
                   LaTeXSyntaxException.ILLEGAL_ARG_TYPE, c);
            }
         }
      }

      TeXObject arg = popArg(parser, stack);

      list.add(arg, true);

      list.add(new EndDeclaration(declaration));

      TeXParserUtils.process(list, parser, stack);

      parser.endGroup();
   }

   private Declaration declaration;
   private TeXMode mode;
}
