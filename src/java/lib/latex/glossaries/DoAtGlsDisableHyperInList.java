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

public class DoAtGlsDisableHyperInList extends AbstractGlsCommand
{
   public DoAtGlsDisableHyperInList(GlossariesSty sty)
   {
      this("do@glsdisablehyperinlist", sty);
   }

   public DoAtGlsDisableHyperInList(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new DoAtGlsDisableHyperInList(getName(), getSty());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      GlsType type = (GlsType)listener.getControlSequence("glstype");

      boolean nohyper = sty.isNoHyperGlossary(type);

      if (!nohyper && sty.isExtra())
      {
         GlsLabel glslabel = (GlsLabel)listener.getControlSequence("glslabel");

         if (sty.isAttributeTrue(glslabel, "nohyper"))
         {
            nohyper = true;
         }
      }

      if (nohyper)
      {
         TeXObjectList substack = listener.createStack();
         substack.add(parser.getControlSequence("let"));
         substack.add(parser.getControlSequence("ifKV@glslink@hyper"));
         substack.add(parser.getControlSequence("iffalse"));

         if (parser == stack || stack == null)
         {
            substack.process(parser);
         }
         else
         {
            substack.process(parser, stack);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
