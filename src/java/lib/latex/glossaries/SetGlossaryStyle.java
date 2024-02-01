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

public class SetGlossaryStyle extends ControlSequence
{
   public SetGlossaryStyle()
   {
      this("setglossarystyle");
   }

   public SetGlossaryStyle(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new SetGlossaryStyle(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String styleName = popLabelString(parser, stack);

      ControlSequence cs = parser.getControlSequence("@glsstyle@"+styleName);

      if (cs == null)
      {
         TeXApp texApp = parser.getListener().getTeXApp();
         texApp.warning(parser,
            texApp.getMessage(GlossariesSty.GLOSSARY_STYLE_NOT_DEFINED, styleName));
      }
      else
      {
         TeXParserUtils.process(cs, parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
