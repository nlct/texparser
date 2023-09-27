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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2LEnd extends End
{
   public L2LEnd()
   {
      this("end");
   }

   public L2LEnd(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2LEnd(getName());
   }

   @Override
   protected void doEnd(TeXParser parser, TeXObjectList stack, String name)
     throws IOException
   {
      LaTeX2LaTeX listener = ((LaTeX2LaTeX)parser.getListener());

      ControlSequence cs = listener.getControlSequence(name);

      if (cs instanceof MathDeclaration)
      {
         MathDeclaration decl = (MathDeclaration)cs;
         decl.revertModeSwitch(parser);
      }

      listener.write(String.format("%s%s%s%s%s",
        new String(Character.toChars(parser.getEscChar())),
        getName(), 
        new String(Character.toChars(parser.getBgChar())), 
        name, 
        new String(Character.toChars(parser.getEgChar()))));
   }
}
