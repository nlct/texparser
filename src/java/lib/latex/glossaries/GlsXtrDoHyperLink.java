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

public class GlsXtrDoHyperLink extends AbstractGlsCommand
{
   public GlsXtrDoHyperLink(GlossariesSty sty)
   {
      this("glsxtrdohyperlink", sty);
   }

   public GlsXtrDoHyperLink(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsXtrDoHyperLink(getName(), getSty());
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

   // TODO implement attributes and multi-entry alias
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String target = popLabelString(parser, stack);
      TeXObject linkText = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      ControlSequence labelCs = listener.getControlSequence("glslabel");
      GlossaryEntry entry = null;
      String label = null;

      if (labelCs instanceof GlsLabel)
      {
         label = ((GlsLabel)labelCs).getLabel();
         entry = ((GlsLabel)labelCs).getEntry();

         if (entry == null)
         {
            entry = sty.getEntry(label);
         }
      }
      else
      {
         label = parser.expandToString(labelCs, stack);
         entry = sty.getEntry(label);
      }

      if (entry != null)
      {
         TeXObjectList content = listener.createStack();
         content.add(listener.getControlSequence("glsxtrhyperlink"));

         TeXObject aliasObj = entry.get("alias");

         if (aliasObj == null)
         {
            content.add(listener.createGroup(target));
         }
         else
         {
            String prefix = parser.expandToString(
              parser.getControlSequence("glolinkprefix"), stack);
            String alias = parser.expandToString(aliasObj, stack);

            content.add(listener.createGroup(prefix+alias));
         }

         content.add(TeXParserUtils.createGroup(listener, linkText));

         TeXParserUtils.process(content, parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
