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

public class PrintGlossary extends ControlSequence
{
   public PrintGlossary(GlossariesSty sty)
   {
      this("printglossary", sty);
   }

   public PrintGlossary(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new PrintGlossary(getName(), sty);
   }

   protected void initOptions(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      KeyValList options = sty.popOptKeyValList(parser, stack);

      Glossary glossary = sty.initPrintGloss(options, stack);

      ext = glossary.getGls();

      if (ext == null)
      {
         throw new NullPointerException();
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      parser.startGroup();

      initOptions(parser, stack);

      ControlSequence cs = listener.getControlSequence("jobname");
      String jobname = parser.expandToString(cs, stack);

      TeXPath texPath = new TeXPath(parser, jobname, ext, false);

      stack.push(listener.getControlSequence("endgroup"));

      if (texPath.exists())
      {
         listener.addFileReference(texPath);
         listener.input(texPath, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private String ext = "gls";

   private GlossariesSty sty;
}
