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
import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsAtAtLink extends AbstractGlsCommand
{
   public AtGlsAtAtLink(GlossariesSty sty)
   {
      this("@gls@@link", sty, false);
   }

   public AtGlsAtAtLink(String name, GlossariesSty sty, boolean checkModifier)
   {
      super(name, sty);
      this.checkModifier = checkModifier;
   }

   public Object clone()
   {
      return new AtGlsAtAtLink(getName(), getSty(), checkModifier);
   }

   // leave indexing/recording to TeX
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      KeyValList options = popOptKeyValList(parser, stack, checkModifier);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject linkText = popArg(parser, stack);

      if (glslabel.getEntry() == null)
      {
         sty.undefWarnOrError(parser, stack, GlossariesSty.ENTRY_NOT_DEFINED, 
           glslabel.getLabel());
      }
      else
      {
         // \let\do@gls@link@checkfirsthyper\relax
         parser.putControlSequence(true, 
            new AssignedControlSequence("do@gls@link@checkfirsthyper", new Relax()));

         if (sty.isExtra())
         {
            parser.putControlSequence(true,
              new GenericCommand(true, "glscustomtext", null, 
                    (TeXObject)linkText.clone()));

            stack.push(listener.getControlSequence("@glsxtr@field@linkdefs"));
         }

         Group grp = listener.createGroup();
         grp.add(linkText);

         stack.push(glslabel);
         stack.push(options);

         stack.push(listener.getControlSequence("@gls@link"));
      }

      stack.push(new TeXCsRef("glspostlinkhook"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean checkModifier;
}
