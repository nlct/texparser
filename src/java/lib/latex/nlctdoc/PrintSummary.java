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

   protected void processSummary(TeXObjectList substack, Vector<GlsLabel> glslabels, 
     String title, String label, ControlSequence sectionCs, 
     TeXParser parser, TeXObjectList stack)
   {
      TeXParserListener listener = parser.getListener();

      substack.add(sectionCs);
      substack.add(listener.getOther('*'));
      substack.add(listener.createGroup(title));
      substack.add(new TeXCsRef("label"));
      substack.add(listener.createGroup(label));

      processSummary(substack, glslabels, parser, stack);
   }

   protected void processSummary(TeXObjectList substack, Vector<GlsLabel> glslabels, 
     TeXParser parser, TeXObjectList stack)
   {
      TeXParserListener listener = parser.getListener();

      ControlSequence defCs = listener.getControlSequence("def");
      ControlSequence targetCs = listener.getControlSequence("glstarget");

      ControlSequence descCs = listener.getControlSequence("glossentrydesc");
      ControlSequence postDescCs = listener.getControlSequence("glspostdescription");

      for (GlsLabel glslabel : glslabels)
      {
/*
         String category = glslabel.getCategory();

         substack.add(defCs);
         substack.add(new TeXCsRef("glscurrententrylabel"));
         substack.add(listener.createGroup(glslabel.getLabel()));

         ControlSequence nameCs = parser.getControlSequence(
           "summaryglossentry"+category);

         if (nameCs == null)
         {
            nameCs = listener.getControlSequence("summaryglossentry");
         }

*/
ControlSequence nameCs = listener.getControlSequence("summaryglossentry");
         substack.add(nameCs);
         substack.add(glslabel);
/*

         substack.add(listener.getPar());
         substack.add(descCs);
         substack.add(glslabel);
         substack.add(postDescCs);
*/
         substack.add(listener.getPar());

      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String type = "index";

      Glossary glossary = sty.getGlossary(type);

      parser.debugMessage(1, "Writing SUMMARIES");

      if (glossary != null && !glossary.isEmpty())
      {
         TeXParserListener listener = parser.getListener();

         TeXObjectList substack = listener.createStack();

         ControlSequence sectionCs = parser.getControlSequence("chapter");

         if (sectionCs == null)
         {
            sectionCs = listener.getControlSequence("section");
         }

         Vector<GlsLabel> cmds = new Vector<GlsLabel>();
         Vector<GlsLabel> envs = new Vector<GlsLabel>();

         Vector<GlsLabel> pkgList = new Vector<GlsLabel>();
         Vector<GlsLabel> clsList = new Vector<GlsLabel>();

         HashMap<String,Vector<GlsLabel>> pkgMap = new HashMap<String,Vector<GlsLabel>>();

         Vector<GlsLabel> pkgopts = new Vector<GlsLabel>();
         Vector<GlsLabel> clsopts = new Vector<GlsLabel>();

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

            if (entry.isFieldEmpty("description"))
            {
               continue;
            }

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
            else if (cat.equals("package"))
            {
               pkgList.add(glslabel);

               Vector<GlsLabel> pl = new Vector<GlsLabel>();
               pl.add(glslabel);
               pkgMap.put(glslabel.getLabel(), pl);
            }
            else if (cat.equals("class"))
            {
               clsList.add(glslabel);

               Vector<GlsLabel> cl = new Vector<GlsLabel>();
               cl.add(glslabel);
               pkgMap.put(glslabel.getLabel(), cl);
            }
            else if (cat.equals("packageoption"))
            {
               if (entry.hasParent())
               {
                  GlossaryEntry parentEntry = entry.getParent(stack);
                  String parentLabel = parentEntry.getLabel();

                  Vector<GlsLabel> pl = pkgMap.get(parentLabel);

                  if (pl == null)
                  {
                     pkgopts.add(glslabel);
                  }
                  else
                  {
                     pl.add(glslabel);
                  }
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
                  GlossaryEntry parentEntry = entry.getParent(stack);
                  String parentLabel = parentEntry.getLabel();

                  Vector<GlsLabel> cl = pkgMap.get(parentLabel);

                  if (cl == null)
                  {
                     clsopts.add(glslabel);
                  }
                  else
                  {
                     cl.add(glslabel);
                  }
               }
               else
               {
                  clsopts.add(glslabel);
               }
            }
            else if (cat.startsWith("option"))
            {
               TeXObject rootVal = entry.get("rootancestor");

               if (rootVal != null)
               {
                  String rootLabel = rootVal.toString(parser);
                  GlossaryEntry rootEntry = sty.getEntry(rootLabel);

                  if (rootEntry != null)
                  {
                     String rootCat = rootEntry.getCategory();

                     if (rootCat.equals("command"))
                     {
                        cmds.add(glslabel);
                     }
                     else if (rootCat.equals("environment"))
                     {
                        envs.add(glslabel);
                     }
                     else if (rootCat.equals("package"))
                     {
                        Vector<GlsLabel> pl = pkgMap.get(rootLabel);

                        if (pl == null)
                        {
                           pkgopts.add(glslabel);
                        }
                        else
                        {
                           pl.add(glslabel);
                        }
                     }
                     else if (rootCat.equals("class"))
                     {
                        Vector<GlsLabel> cl = pkgMap.get(rootLabel);

                        if (cl == null)
                        {
                           clsopts.add(glslabel);
                        }
                        else
                        {
                           cl.add(glslabel);
                        }
                     }
                  }
               }
            }

         }

         if (!cmds.isEmpty())
         {
            processSummary(substack, cmds, "Command Summary", "cmdsummary", sectionCs, 
              parser, stack);
         }

         if (!envs.isEmpty())
         {
            processSummary(substack, envs, "Environment Summary", "envsummary", sectionCs, 
              parser, stack);
         }

         if (!clsopts.isEmpty())
         {
            processSummary(substack, clsopts, "Class Option Summary",
              "clsoptsummary", sectionCs, parser, stack);
         }
         else if (!clsList.isEmpty())
         {
            substack.add(sectionCs);
            substack.add(listener.getOther('*'));
            substack.add(listener.createGroup("Class Summary"));
            substack.add(new TeXCsRef("label"));
            substack.add(listener.createGroup("clsoptsummary"));
         }

         if (!clsList.isEmpty())
         {
            for (GlsLabel gl : clsList)
            {
               processSummary(substack, pkgMap.get(gl.getLabel()), parser, stack);
            }
         }

         if (!pkgopts.isEmpty())
         {
            processSummary(substack, pkgopts, "Package Option Summary",
              "styoptsummary", sectionCs, parser, stack);
         }
         else if (!pkgList.isEmpty())
         {
            substack.add(sectionCs);
            substack.add(listener.getOther('*'));
            substack.add(listener.createGroup("Package Summary"));
            substack.add(new TeXCsRef("label"));
            substack.add(listener.createGroup("styoptsummary"));
         }

         if (!pkgList.isEmpty())
         {
            for (GlsLabel gl : pkgList)
            {
               processSummary(substack, pkgMap.get(gl.getLabel()), parser, stack);
            }
         }

         TeXParserUtils.process(substack, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
