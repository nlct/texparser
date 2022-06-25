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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class PrintSummary extends AbstractGlsCommand
{
   public PrintSummary(GlossariesSty sty)
   {
      this("printsummary", sty);
   }

   public PrintSummary(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PrintSummary(getName(), getSty());
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

   protected void processSummary(TeXObjectList list, Vector<GlsLabel> glslabels, 
     String title, String label, ControlSequence sectionCs, String id,
     TeXParser parser, TeXObjectList stack)
   {
      TeXParserListener listener = parser.getListener();

      list.add(sectionCs);
      list.add(listener.getOther('*'));
      list.add(listener.createGroup(title));
      list.add(new TeXCsRef("label"));
      list.add(listener.createGroup(label));

      ControlSequence defCs = listener.getControlSequence("def");
      ControlSequence targetCs = listener.getControlSequence("glstarget");
      ControlSequence nameCs = parser.getControlSequence("summaryglossentry"+id);

      if (nameCs == null)
      {
         nameCs = listener.getControlSequence("summaryglossentry");
      }

      ControlSequence descCs = listener.getControlSequence("glossentrydesc");
      ControlSequence postDescCs = listener.getControlSequence("glspostdescription");

      for (GlsLabel glslabel : glslabels)
      {
         list.add(defCs);
         list.add(new TeXCsRef("glscurrententrylabel"));
         list.add(listener.createGroup(glslabel.getLabel()));

         Group grp = listener.createGroup();
         list.add(grp);
         grp.add(nameCs);
         grp.add(glslabel);

         list.add(listener.getPar());
         list.add(descCs);
         list.add(glslabel);
         list.add(postDescCs);
         list.add(listener.getPar());
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String type = "index";

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXObjectList list = parser.getListener().createStack();

         ControlSequence cs = parser.getControlSequence("chapter");

         if (cs == null)
         {
            cs = parser.getListener().getControlSequence("section");
         }

         Vector<GlsLabel> cmds = new Vector<GlsLabel>();
         Vector<GlsLabel> envs = new Vector<GlsLabel>();
         Vector<GlsLabel> pkgopts = new Vector<GlsLabel>();
         Vector<GlsLabel> clsopts = new Vector<GlsLabel>();

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

            GlsLabel glslabel = new GlsLabel("glscurrententrylabel@"+label,
              label, entry);

            String cat = glslabel.getEntry().getCategory();

            if (cat.equals("command"))
            {
               cmds.add(glslabel);
            }
            else if (cat.equals("environment"))
            {
               envs.add(glslabel);
            }
            else if (cat.equals("packageoption"))
            {
               if (entry.hasParent())
               {
               }
               else
               {
                  pkgopts.add(glslabel);
               }
            }
            else if (cat.equals("classoption"))
            {
               if (entry.hasParent())
               {
               }
               else
               {
                  clsopts.add(glslabel);
               }
            }
            else if (cat.equals("optionvalue"))
            {
            }

         }

         if (!cmds.isEmpty())
         {
            processSummary(list, cmds, "Command Summary", "cmdsummary", cs, 
              "command", parser, stack);
         }

         if (!envs.isEmpty())
         {
            processSummary(list, envs, "Environmant Summary", "envsummary", cs, 
              "environment", parser, stack);
         }

         if (!clsopts.isEmpty())
         {
            processSummary(list, clsopts, "Class Option Summary", "clsoptsummary", cs, 
              "class", parser, stack);
         }

         if (!pkgopts.isEmpty())
         {
            processSummary(list, pkgopts, "Package Option Summary",
              "styoptsummary", cs, "package", parser, stack);
         }

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
