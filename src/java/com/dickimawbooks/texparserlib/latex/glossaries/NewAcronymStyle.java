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

public class NewAcronymStyle extends AbstractGlsCommand
{
   public NewAcronymStyle(GlossariesSty sty)
   {
      this("newacronymstyle", Overwrite.FORBID, sty);
   }

   public NewAcronymStyle(String name, Overwrite overwrite, GlossariesSty sty)
   {
      super(name, sty);
      this.overwrite = overwrite;
   }

   @Override
   public Object clone()
   {
      return new NewAcronymStyle(getName(), getOverwrite(), getSty());
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

      TeXObject dispArg = popArg(parser, stack);
      TeXObject defsArg = popArg(parser, stack);

      ControlSequence cs = parser.getControlSequence("@glsacr@dispstyle@"+styleName);

      if (cs != null)
      {
         switch (overwrite)
         {
            case SKIP: return;
            case FORBID:
              throw new LaTeXSyntaxException(parser,
                 GlossariesSty.ACRONYM_STYLE_DEFINED, styleName);
         }
      }

      parser.putControlSequence(true, new GenericCommand(true,
        "@glsacr@dispstyle@"+styleName, null, dispArg));
      parser.putControlSequence(true, new GenericCommand(true,
        "@glsacr@styledefs@"+styleName, null, defsArg));
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
