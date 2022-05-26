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
import com.dickimawbooks.texparserlib.primitives.Relax;

public class AtGlsAtFieldAtLink extends AbstractGlsCommand
{
   public AtGlsAtFieldAtLink(GlossariesSty sty)
   {
      this("@gls@field@link", sty);
   }

   public AtGlsAtFieldAtLink(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new AtGlsAtFieldAtLink(getName(), getSty());
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
      TeXObject arg = popArg(parser, stack);
      KeyValList keyValList;

      if (arg instanceof KeyValList)
      {
         keyValList = (KeyValList)arg;
      }
      else
      {
         keyValList = KeyValList.getList(parser, arg);
      }

      GlsLabel glslabel = popEntryLabel(parser, stack);

      TeXObject text = popArg(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      stack.push(listener.getControlSequence("glspostlinkhook"));

      if (entry == null)
      {
         sty.undefWarnOrError(stack, 
           GlossariesSty.ENTRY_NOT_DEFINED, glslabel.getLabel());
      }
      else
      {
         // \let\do@gls@link@checkfirsthyper\relax
         parser.putControlSequence(true, 
            new AssignedControlSequence("do@gls@link@checkfirsthyper", new Relax()));

         Group grp = listener.createGroup();
         grp.add(text, true);
         stack.push(grp);

         stack.push(glslabel);
         stack.push(keyValList);
         stack.push(listener.getControlSequence("@gls@link"));
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
