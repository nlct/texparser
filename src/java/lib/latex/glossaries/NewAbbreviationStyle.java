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

public class NewAbbreviationStyle extends AbstractGlsCommand
{
   public NewAbbreviationStyle(GlossariesSty sty)
   {
      this("newabbreviationstyle", Overwrite.FORBID, sty);
   }

   public NewAbbreviationStyle(String name, Overwrite overwrite, GlossariesSty sty)
   {
      super(name, sty);
      this.overwrite = overwrite;
   }

   @Override
   public Object clone()
   {
      return new NewAbbreviationStyle(getName(), getOverwrite(), getSty());
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

      TeXObject setupArg = popArg(parser, stack);
      TeXObject fmtsArg = popArg(parser, stack);

      ControlSequence cs = parser.getControlSequence(
        "@glsabbrv@dispstyle@setup@"+styleName);

      if (cs != null)
      {
         switch (overwrite)
         {
            case SKIP: return;
            case FORBID:
              throw new LaTeXSyntaxException(parser,
                 GlossariesSty.ABBREVIATION_STYLE_DEFINED, styleName);
         }
      }

      TeXObjectList list = parser.getListener().createStack();

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("GlsXtrPostNewAbbreviation"));
      list.add(parser.getListener().createGroup());

      list.add(setupArg, true);

      parser.putControlSequence(true, new GenericCommand(true,
        "@glsabbrv@dispstyle@setup@"+styleName, null, list));

      list = parser.getListener().createStack();

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("glsxtrinlinefullformat"));
      list.add(new TeXCsRef("glsxtrfullformat"));

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("Glsxtrinlinefullformat"));
      list.add(new TeXCsRef("Glsxtrfullformat"));

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("GLSxtrinlinefullformat"));
      list.add(new TeXCsRef("GLSxtrfullformat"));

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("glsxtrinlinefullplformat"));
      list.add(new TeXCsRef("glsxtrfullplformat"));

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("Glsxtrinlinefullplformat"));
      list.add(new TeXCsRef("Glsxtrfullplformat"));

      list.add(new TeXCsRef("renewcommand"));
      list.add(new TeXCsRef("GLSxtrinlinefullplformat"));
      list.add(new TeXCsRef("GLSxtrfullplformat"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("glsxtrsubsequentfmt"));
      list.add(new TeXCsRef("glsxtrdefaultsubsequentfmt"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("Glsxtrsubsequentfmt"));
      list.add(new TeXCsRef("Glsxtrdefaultsubsequentfmt"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("GLSxtrsubsequentfmt"));
      list.add(new TeXCsRef("GLSxtrdefaultsubsequentfmt"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("glsxtrsubsequentplfmt"));
      list.add(new TeXCsRef("glsxtrdefaultsubsequentplfmt"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("Glsxtrsubsequentplfmt"));
      list.add(new TeXCsRef("Glsxtrdefaultsubsequentplfmt"));

      list.add(new TeXCsRef("let"));
      list.add(new TeXCsRef("GLSxtrsubsequentplfmt"));
      list.add(new TeXCsRef("GLSxtrdefaultsubsequentplfmt"));

      list.add(fmtsArg, true);

      parser.putControlSequence(true, new GenericCommand(true,
        "@glsabbrv@dispstyle@fmts@"+styleName, null, list));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public Overwrite getOverwrite()
   {
      return overwrite;
   }

   protected Overwrite overwrite;
}
