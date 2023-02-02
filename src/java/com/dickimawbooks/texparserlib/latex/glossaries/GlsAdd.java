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

public class GlsAdd extends AbstractGlsCommand
{
   public GlsAdd(GlossariesSty sty)
   {
      this("glsadd", sty);
   }

   public GlsAdd(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsAdd(getName(), getSty());
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
      TeXObject optArg = popOptArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      ControlSequence cs = parser.getControlSequence("@@do@wrglossary");

      if (cs != null)
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList list = listener.createStack();
         KeyValList options = null;

         parser.putControlSequence(true, new TextualContentCommand(
           "@gls@counter", parser.expandToString(
               listener.getControlSequence("glscounter"), stack)));

         GlossaryEntry entry = glslabel.getEntry();

         if (entry != null)
         {
            Glossary glossary = sty.getGlossary(entry);

            if (glossary != null)
            {
               String counter = glossary.getCounter();

               if (counter != null)
               {
                  parser.putControlSequence(true, new TextualContentCommand(
                     "@gls@counter", counter));
               }
            }
         }

         if (optArg != null)
         {
            options = KeyValList.getList(parser, optArg);
         }

         if (options != null && !options.isEmpty())
         {
            list.add(listener.getControlSequence("setkeys"));
            list.add(listener.createGroup("glslink"));
            list.add(options);
         }

         list.add(cs);
         list.add(glslabel);

         if (parser == stack || stack == null)
         {
            list.process(parser);
         }
         else
         {
            list.process(parser, stack);
         }
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
