/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.latex.AtSecondOfTwo;

public class NewToggle extends ControlSequence
{
   public NewToggle()
   {
      this("newtoggle");
   }

   public NewToggle(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new NewToggle(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String toggleName = popLabelString(parser, stack);
      String csname = "etb@tgl@"+toggleName;

      ControlSequence cs = parser.getControlSequence(csname);

      if (cs != null)
      {
         throw new LaTeXSyntaxException(parser,
          "etoolbox.toggle_already_defined", toggleName);
      }

      parser.putControlSequence(new AtSecondOfTwo(csname));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
