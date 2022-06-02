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

public class LetAbbreviationStyle extends AbstractGlsCommand
{
   public LetAbbreviationStyle(GlossariesSty sty)
   {
      this("letabbreviationstyle", sty);
   }

   public LetAbbreviationStyle(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new LetAbbreviationStyle(getName(), getSty());
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
      String style1 = popLabelString(parser, stack);
      String style2 = popLabelString(parser, stack);

      ControlSequence setupCs = parser.getControlSequence(
        "@glsabbrv@dispstyle@setup@"+style2);

      if (setupCs == null)
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.ABBREVIATION_STYLE_NOT_DEFINED, style2);
      }

      ControlSequence fmtsCs = parser.getControlSequence(
        "@glsabbrv@dispstyle@fmts@"+style2);

      if (fmtsCs == null)
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.ABBREVIATION_STYLE_NOT_DEFINED, style2);
      }

      parser.putControlSequence(true, new GenericCommand(
          "@glsabbrv@dispstyle@setup@"+style1, null, 
           setupCs));

      parser.putControlSequence(true, new GenericCommand(
          "@glsabbrv@dispstyle@fmts@"+style1, null, 
           fmtsCs));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
