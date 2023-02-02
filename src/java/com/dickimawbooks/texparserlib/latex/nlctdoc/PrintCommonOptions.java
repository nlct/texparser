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

public class PrintCommonOptions extends PrintSummary
{
   public PrintCommonOptions(GlossariesSty sty)
   {
      this("printcommonoptions", sty);
   }

   public PrintCommonOptions(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PrintCommonOptions(getName(), getSty());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      KeyValList options = popOptKeyValList(stack);
      GlsLabel parentLabel = popEntryLabel(parser, stack);

      String type = "index";
      String sectionLabel = parentLabel.getLabel()+"-optionsummary";

      TeXObject title = null;

      if (options != null)
      {
         TeXObject obj = options.get("type");

         if (obj != null)
         {
            type = parser.expandToString(obj, stack);
         }

         obj = options.get("label");

         if (obj != null)
         {
            sectionLabel = parser.expandToString(obj, stack);
         }

         title = options.get("title");
      }

      if (title == null)
      {
         TeXObjectList titleList = listener.createStack();
         titleList.add(listener.getControlSequence("glsxtrfieldtitlecasecs"));

         TeXObject name = parentLabel.getField("name");

         if (name != null)
         {
            titleList.add(TeXParserUtils.createGroup(listener,
               name));
         }

         titleList.add(listener.getSpace());
         titleList.addAll(listener.createString("Summary"));

         title = titleList;
      }

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXObjectList substack = listener.createStack();

         ControlSequence sectionCs = parser.getControlSequence("chapter");

         if (sectionCs == null)
         {
            sectionCs = listener.getControlSequence("section");
         }

         Vector<GlsLabel> labels = new Vector<GlsLabel>();

         TeXObjectList preamble = listener.createStack();
         preamble.add(listener.getControlSequence("glsadd"));
         preamble.add(parentLabel);

         TeXObject desc = parentLabel.getField("description");

         if (desc != null)
         {
            preamble.add(desc);
            preamble.add(listener.getControlSequence("glspostdescription"));
            preamble.add(listener.getPar());
         }

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

            GlsLabel glslabel = new GlsLabel("glscurrententrylabel@"+label,
              label, entry);

            if (!glslabel.getLabel().equals(parentLabel.getLabel()))
            {
               TeXObject rootVal = entry.get("rootancestor");

               if (rootVal != null)
               {
                  String rootLabel = rootVal.toString(parser);
                  GlossaryEntry rootEntry = sty.getEntry(rootLabel);

                  if (rootEntry != null 
                     && rootEntry.getLabel().equals(parentLabel.getLabel()))
                  {
                     labels.add(glslabel);
                  }
               }
            }
         }

         if (!labels.isEmpty())
         {
            processSummary(substack, labels, title, 
              sectionLabel, sectionCs, null, false, preamble, parser, stack);
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
