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

public class PrintUnsrtGlossary extends ControlSequence
{
   public PrintUnsrtGlossary(GlossariesSty sty)
   {
      this("printunsrtglossary", sty);
   }

   public PrintUnsrtGlossary(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new PrintUnsrtGlossary(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');

      parser.startGroup();

      KeyValList options = sty.popOptKeyValList(parser, stack);

      TeXObject initCode = null;

      if (isStar)
      {
         initCode = popArg(parser, stack);
      }

      Glossary glossary = sty.initPrintGloss(options, stack);

// TODO

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private GlossariesSty sty;
}
