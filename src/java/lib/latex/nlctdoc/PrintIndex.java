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

public class PrintIndex extends AbstractGlsCommand
{
   public PrintIndex(GlossariesSty sty)
   {
      this("printuserguideindex", sty);
   }

   public PrintIndex(String name, GlossariesSty sty)
   {
      this(name, "index", "index", sty);
   }

   public PrintIndex(String name, String type, String label, GlossariesSty sty)
   {
      super(name, sty);
      this.glosType = type;
      this.glosLabel = label;
   }

   @Override
   public Object clone()
   {
      return new PrintIndex(getName(), glosType, glosLabel, getSty());
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

   protected void addStatus(TeXObjectList content, GlsLabel glslabel, TeXParser parser)
   {
      TeXObject statusVal = glslabel.getField("status");

      if (statusVal != null)
      {
         String status = statusVal.toString(parser);

         if (!status.equals("default") && !status.isEmpty())
         {
            content.add(parser.getListener().getControlSequence("icon"));
            content.add(parser.getListener().createGroup(status));
            content.add(parser.getListener().getSpace());
         }
      }
   }

   protected void addTarget(TeXObjectList content, GlsLabel glslabel, TeXParser parser)
   {
      TeXParserListener listener = parser.getListener();

      Vector<String> targets = sty.getTargets(glslabel);

      if (targets != null)
      {
         String targetName = targets.firstElement();

         ControlSequence hyperlinkCs = parser.getControlSequence("hyperlink");

         if (hyperlinkCs != null)
         {
            content.add(hyperlinkCs);
            content.add(listener.createGroup(targetName));
         }
      }
      else
      {
         content.add(listener.getControlSequence("glstarget"));
         content.add(glslabel);
      }

   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      KeyValList options = popOptKeyValList(stack);

      String type = glosType;
      String sectionLabel = glosLabel;

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
         title = listener.getControlSequence("indexname");
      }

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXObjectList list = listener.createStack();

         ControlSequence sectionCs = parser.getControlSequence("chapter");
         ControlSequence subSectionCs = parser.getControlSequence("texparser@section");

         if (sectionCs == null)
         {
            sectionCs = listener.getControlSequence("section");
            subSectionCs = parser.getControlSequence("texparser@subsection");
         }

         list.add(sectionCs);
         list.add(listener.getOther('*'));
         list.add(TeXParserUtils.createGroup(parser, title));
         list.add(new TeXCsRef("label"));
         list.add(listener.createGroup(sectionLabel));

         list.add(listener.getControlSequence("nlctguideindexinitpostnamehooks"));

         TeXParserUtils.process(list, parser, stack);

         ControlSequence nameCs = listener.getControlSequence("glossentryname");

         ControlSequence item0 = listener.getControlSequence("nlctuserguideidx0");
         ControlSequence item1 = listener.getControlSequence("nlctuserguideidx1");
         ControlSequence item2 = listener.getControlSequence("nlctuserguideidx2");
         ControlSequence item3 = listener.getControlSequence("nlctuserguideidx3");

         String currentGrpLabel = "";

         ControlSequence navCs = parser.getControlSequence(
           "@gls@hypergrouplist@"+type);

         if (navCs != null)
         {
            String[] grpLabels = parser.expandToString(navCs, stack).split(",");

            list.add(listener.getControlSequence("nlctusernavbox"));
            Group grp = listener.createGroup();
            list.add(grp);

            for (int i = 0; i < grpLabels.length; i++)
            {
               if (i > 0)
               {
                  grp.add(listener.getSpace());
               }

               ControlSequence cs = parser.getControlSequence(
                  "glsxtr@grouptitle@"+grpLabels[i]);
               TeXObject grpTitle;

               if (cs == null)
               {
                  grpTitle = listener.createGroup(grpLabels[i]);
               }
               else
               {
                  grpTitle = cs;
               }

               grp.add(listener.getControlSequence("hyperlink"));
               grp.add(listener.createGroup(grpLabels[i]));
               grp.add(grpTitle);
            }
         }

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

            TeXObject grpObj = entry.get("group");

            if (grpObj != null)
            {
               String grpLabel = parser.expandToString(grpObj, stack);

               if (!grpLabel.equals(currentGrpLabel))
               {
                  list.add(subSectionCs);

                  ControlSequence cs = parser.getControlSequence(
                     "glsxtr@grouptitle@"+grpLabel);
                  TeXObject grpTitle;

                  if (cs == null)
                  {
                     list.add(listener.createGroup(grpLabel));
                     grpTitle = listener.createString(grpLabel);
                  }
                  else
                  {
                     list.add(cs);
                     grpTitle = cs;
                  }

                  list.add(listener.createGroup(grpLabel));

                  currentGrpLabel = grpLabel;
               }
            }

            GlsLabel glslabel = new GlsLabel("glscurrententrylabel",
              label, entry);

            parser.putControlSequence(true, glslabel);

            int level = entry.getLevel();

            ControlSequence item;

            switch (level)
            {
               case 0: item = item0; break;
               case 1: item = item1; break;
               case 2: item = item2; break;
               default: item = item3;
            }

            list.add(item);

            Group content = listener.createGroup();
            list.add(content);

            addTarget(content, glslabel, parser);

            Group grp = listener.createGroup();
            content.add(grp);

            grp.add(nameCs);
            grp.add(glslabel);

            addStatus(content, glslabel, parser);

            addLocationList(glslabel, content, listener);

            TeXParserUtils.process(list, parser, stack);
         }
      }
   }

   protected void addLocationList(GlsLabel glslabel, TeXObjectList content,
     TeXParserListener listener)
   {
      TeXObject loc = glslabel.getField("location");

      if (loc != null)
      {
         content.add(listener.getControlSequence("qquad"));
         content.add(loc);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected String glosType = "index";
   protected String glosLabel = "index";
}
