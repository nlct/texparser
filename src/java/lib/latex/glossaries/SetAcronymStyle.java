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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class SetAcronymStyle extends AbstractGlsCommand
{
   public SetAcronymStyle(GlossariesSty sty)
   {
      this("setacronymstyle", sty);
   }

   public SetAcronymStyle(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new SetAcronymStyle(getName(), getSty());
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
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String styleName = popLabelString(parser, stack);

      ControlSequence cs = parser.getControlSequence("@glsacr@styledefs@"+styleName);

      if (cs == null)
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.ACRONYM_STYLE_NOT_DEFINED, styleName);
      }

      if (parser == stack || stack == null)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }

      Vector<String> types = sty.getAbbreviationGlossaries();

      if (types == null)
      {
         sty.isAbbreviationGlossary(parser.expandToString(
            parser.getListener().getControlSequence("acronymtype"), stack));

         types = sty.getAbbreviationGlossaries();
      }

      for (String type : types)
      {
         parser.putControlSequence(false, new GenericCommand(true,
          "gls@"+type+"@entryfmt", null, 
            new TeXCsRef("@glsacr@dispstyle@"+styleName)));
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
