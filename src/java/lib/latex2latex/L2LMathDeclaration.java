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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2LMathDeclaration extends MathDeclaration
{
   public L2LMathDeclaration()
   {
      super();
   }

   public L2LMathDeclaration(String name)
   {
      super(name);
   }

   public L2LMathDeclaration(String name, int mode)
   {
      super(name, mode);
   }

   public L2LMathDeclaration(String name, int mode, boolean numbered)
   {
      super(name, mode, numbered);
   }

   public Object clone()
   {
      return new L2LMathDeclaration(getName(), getMode(), isNumbered());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      doModeSwitch(parser);

      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      EndDeclaration endDec = getEndDeclaration();

      if (endDec != null)
      {
         Writeable writeable = parser.getListener().getWriteable();

         writeable.writeCodePoint(parser.getEscChar());

         writeable.write(getName());

         TeXObject nextObj = stack.peekStack(TeXObjectList.POP_RETAIN_IGNOREABLES);

         if (nextObj instanceof Ignoreable)
         {
            writeable.write(nextObj.toString(parser));

            if (parser == stack)
            {
               parser.popStack(TeXObjectList.POP_RETAIN_IGNOREABLES);
            }
            else
            {
               stack.popStack(parser, TeXObjectList.POP_RETAIN_IGNOREABLES);
            }
         }
      }
      else
      {
         listener.writeCodePoint(parser.getEscChar());
         listener.write("begin");
         listener.writeCodePoint(parser.getBgChar());
         listener.write(getName());
         listener.writeCodePoint(parser.getEgChar());
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void end(TeXParser parser) throws IOException
   {
      revertModeSwitch(parser);

      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      EndDeclaration endDec = getEndDeclaration();

      if (endDec != null)
      {
         Writeable writeable = parser.getListener().getWriteable();

         writeable.writeCodePoint(parser.getEscChar());

         writeable.write(endDec.getName());
   
         if (parser.isLetter(endDec.getName().codePointAt(0)))
         {
            if (parser.size() == 0)
            {
               parser.fetchNext();
            }
      
            TeXObject nextObj = parser.firstElement();

            if (nextObj instanceof Letter)
            {
               writeable.write(" ");
            }
         }
      }
      else
      {
         listener.writeCodePoint(parser.getEscChar());
         listener.write("end");
         listener.writeCodePoint(parser.getBgChar());
         listener.write(getName());
         listener.writeCodePoint(parser.getEgChar());
      }
   }
}
