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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsXtrWrGlossCounterMark extends Command
{
   public AtGlsXtrWrGlossCounterMark()
   {
      this("@glsxtrwrglosscountermark");
   } 

   public AtGlsXtrWrGlossCounterMark(String name)
   {
      super(name);
   } 

   @Override
   public TeXObject clone()
   {
      return new AtGlsXtrWrGlossCounterMark(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject arg = popArg(parser, stack);
      TeXObjectList expanded = parser.getListener().createStack();

      if (listener.isInDocEnv())
      {
         expanded.add(listener.getControlSequence("glsxtrwrglosscountermark"));

         expanded.add(TeXParserUtils.createGroup(parser, arg));
      }

      return expanded;
   }
}
