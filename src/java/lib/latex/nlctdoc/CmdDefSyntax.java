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

public class CmdDefSyntax extends AbstractGlsCommand
{
   public CmdDefSyntax(GlossariesSty sty)
   {
      this("cmddefsyntax", sty);
   }

   public CmdDefSyntax(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new CmdDefSyntax(getName(), getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject options = popArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObjectList content = listener.createStack();

      content.add(listener.getControlSequence("mainglsadd"));

      if (!options.isEmpty())
      {
         content.add(listener.getOther('['));
         content.add(options, true);
         content.add(listener.getOther(']'));
      }

      content.add(glslabel);
      content.add(listener.createGroup("cmddef"));

      content.add(listener.getControlSequence("glsxtrglossentry"));
      content.add(glslabel);

      TeXObject syntax = glslabel.getField("syntax");

      if (syntax != null)
      {
         content.add(syntax, true);
      }

      TeXParserUtils.process(content, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
