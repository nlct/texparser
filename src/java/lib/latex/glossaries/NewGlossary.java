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

public class NewGlossary extends ControlSequence
{
   public NewGlossary(GlossariesSty sty)
   {
      this("newglossary", Overwrite.FORBID, false, sty);
   }

   public NewGlossary(String name, Overwrite overwrite, GlossariesSty sty)
   {
      this(name, overwrite, false, sty);
   }

   public NewGlossary(String name, Overwrite overwrite, boolean ignored, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
      this.overwrite = overwrite;
      this.ignored = ignored;
   }

   public Object clone()
   {
      return new NewGlossary(getName(), overwrite, ignored, getSty());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = false;

      TeXObject object = stack.peekStack();

      if (object instanceof CharObject
            && ((CharObject)object).getCharCode() == (int)'*')
      {
         isStar = true;
         stack.popStack(parser);
      }

      String glg = null;
      String gls = null;
      String glo = null;

      TeXObject optArg = null;

      if (!isStar && !ignored)
      {
         gls = popOptLabelString(parser, stack);
      }

      String label = popLabelString(parser, stack);

      String counter = null;

      TeXObject title = null;

      if (!ignored)
      {
         if (!isStar)
         {
            gls = popLabelString(parser, stack);
            glo = popLabelString(parser, stack);
         }

         title = popArg(parser, stack);

         counter = popOptLabelString(parser, stack);
      }

      sty.createGlossary(label, title, counter, glg, gls, glo, ignored,
        ignored && !isStar, overwrite);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public GlossariesSty getSty()
   {
      return sty;
   }

   private GlossariesSty sty;
   private Overwrite overwrite;
   protected boolean ignored=false;
}
