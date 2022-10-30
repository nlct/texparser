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

public class IfGlsFieldEq extends AbstractGlsCommand
{
   public IfGlsFieldEq(GlossariesSty sty)
   {
      this("ifglsfieldeq", false, false, sty);
   }

   public IfGlsFieldEq(String name, boolean expandArg, GlossariesSty sty)
   {
      this(name, expandArg, false, sty);
   }

   public IfGlsFieldEq(String name, boolean expandArg, boolean csname, 
     GlossariesSty sty)
   {
      super(name, sty);
      this.expandArg = expandArg;
      this.csname = csname;
   }

   public Object clone()
   {
      return new IfGlsFieldEq(getName(), expandArg, csname, getSty());
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

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      GlsLabel glslabel = popEntryLabel(parser, stack);

      String field = sty.getFieldName(popLabelString(parser, stack));

      TeXObject arg = null;

      if (csname)
      {
         String name = popLabelString(parser, stack);
         arg = parser.getListener().getControlSequence(name);
      }
      else
      {
         arg = popArg(parser, stack);
      }

      TeXObject trueArg = popArg(parser, stack);
      TeXObject falseArg = popArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      if (entry == null)
      {
         sty.undefWarnOrError(stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         TeXObject val = entry.get(field);

         if (val == null)
         {
            throw new LaTeXSyntaxException(parser, 
              GlossariesSty.FIELD_NOT_DEFINED, field);
         }

         if (expandArg)
         {
            arg = TeXParserUtils.expandOnce(arg, parser, stack);
         }

         val = TeXParserUtils.expandOnce(val, parser, stack);

         if (arg.toString(parser).equals(val.toString(parser)))
         {
            TeXParserUtils.process(trueArg, parser, stack);
         }
         else
         {
            TeXParserUtils.process(falseArg, parser, stack);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean expandArg, csname;
}
