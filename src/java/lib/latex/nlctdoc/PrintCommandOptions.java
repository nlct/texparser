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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class PrintCommandOptions extends PrintSummary
{
   public PrintCommandOptions(GlossariesSty sty)
   {
      this("printcommandoptions", sty);
   }

   public PrintCommandOptions(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PrintCommandOptions(getName(), getSty());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popOptArg(parser, stack);
      GlsLabel cmdLabel = popEntryLabel(parser, stack);

      String type = "index";

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList substack = listener.createStack();

         ControlSequence sectionCs = parser.getControlSequence("chapter");

         if (sectionCs == null)
         {
            sectionCs = listener.getControlSequence("section");
         }

         Vector<GlsLabel> labels = new Vector<GlsLabel>();

         labels.add(cmdLabel);

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

            GlsLabel glslabel = new GlsLabel("glscurrententrylabel@"+label,
              label, entry);

            TeXObject rootVal = entry.get("rootancestor");

            if (rootVal != null)
            {
               String rootLabel = rootVal.toString(parser);
               GlossaryEntry rootEntry = sty.getEntry(rootLabel);

               if (rootEntry != null 
                  && rootEntry.getLabel().equals(cmdLabel.getLabel()))
               {
                  labels.add(glslabel);
               }
            }
         }

         if (!labels.isEmpty())
         {
            TeXObjectList title = listener.createStack();
            title.add(cmdLabel.getField("name"));
            title.add(listener.getSpace());
            title.addAll(listener.createString("options"));

            processSummary(substack, labels, title, 
              cmdLabel.getLabel()+"-options", sectionCs, 
              parser, stack);
         }

         // substack should be empty, but if not process anything
         // remaining
         if (!substack.isEmpty())
         {
            TeXParserUtils.process(substack, parser, stack);
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
