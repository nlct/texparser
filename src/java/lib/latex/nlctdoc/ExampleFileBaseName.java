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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class ExampleFileBaseName extends Command
{
   public ExampleFileBaseName()
   {
      this("nlctexamplefilebasename");
   }

   public ExampleFileBaseName(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ExampleFileBaseName(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      NumericRegister reg = parser.getSettings().getNumericRegister("c@example");

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, "example");
      }

      int exNum = reg.number(parser);

      TeXObjectList expanded = listener.createStack();

      ControlSequence jobnameCs = listener.getControlSequence("jobname");

      expanded.add(TeXParserUtils.expandOnce(jobnameCs, parser, stack), true);
      expanded.addAll(listener.createString(String.format("-example%03d", exNum)));

      return expanded;
   }

}
