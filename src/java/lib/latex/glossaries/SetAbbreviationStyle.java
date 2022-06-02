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

public class SetAbbreviationStyle extends AbstractGlsCommand
{
   public SetAbbreviationStyle(GlossariesSty sty)
   {
      this("setabbreviationstyle", sty);
   }

   public SetAbbreviationStyle(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new SetAbbreviationStyle(getName(), getSty());
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
      String catLabel = popOptLabelString(parser, stack);

      if (catLabel == null)
      {
         catLabel = "abbreviation";
      }

      String style = popLabelString(parser, stack);

      ControlSequence setupCs = parser.getControlSequence(
         "@glsabbrv@dispstyle@setup@"+style);

      ControlSequence fmtsCs = parser.getControlSequence(
         "@glsabbrv@dispstyle@fmts@"+style);

      if (setupCs == null || fmtsCs == null)
      {
         throw new LaTeXSyntaxException(parser,
           GlossariesSty.ABBREVIATION_STYLE_NOT_DEFINED, style);
      }

      parser.putControlSequence(true, 
        new TextualContentCommand("@glsabbrv@current@"+catLabel, style));

      parser.putControlSequence(true, 
        new TextualContentCommand("glscategorylabel", catLabel));

      if (parser == stack || stack == null)
      {
         setupCs.process(parser);
         fmtsCs.process(parser);
      }
      else
      {
         setupCs.process(parser, stack);
         fmtsCs.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
