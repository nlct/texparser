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

public class GlsAddStorageKey extends AbstractGlsCommand
{
   public GlsAddStorageKey(GlossariesSty sty)
   {
      this("glsaddstoragekey", sty);
   }

   public GlsAddStorageKey(String name, GlossariesSty sty)
   {
      this(name, Overwrite.FORBID, sty);
   }

   public GlsAddStorageKey(String name, Overwrite overwrite, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsAddStorageKey(getName(), getOverwrite(), getSty());
   }

   public Overwrite getOverwrite()
   {
      return overwrite;
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
      boolean isStar = (popModifier(parser, stack, '*') == '*');
      String field = popLabelString(parser, stack);
      TeXObject defValue = popArg(parser, stack);
      TeXObject csArg = popArg(parser, stack);

      if (!csArg.isEmpty())
      {
         if (!(csArg instanceof ControlSequence))
         {
            throw new TeXSyntaxException(
               parser,
               TeXSyntaxException.ERROR_CS_EXPECTED,
               csArg.format(), csArg.getClass().getSimpleName());
         }

         String csname = ((ControlSequence)csArg).getName();

         switch (overwrite)
         {
            case FORBID:

              if (parser.getControlSequence(csname) != null)
              {
                 throw new LaTeXSyntaxException(parser,
                  LaTeXSyntaxException.ERROR_DEFINED,
                  csname);
              }
            break;
            case SKIP:

              if (parser.getControlSequence(csname) != null)
              {
                 return;
              }

            break;
         }

         parser.putControlSequence(new GlsEntryField(csname, field, getSty()));
      }

      sty.addField(field, defValue);

      if (isStar)
      {
         sty.setFieldExpansionOn(field, true);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected Overwrite overwrite;
}
