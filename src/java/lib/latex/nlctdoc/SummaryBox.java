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
import java.awt.Color;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class SummaryBox extends AbstractGlsCommand
{
   public SummaryBox(FrameBox frameBox,
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      this("summaryglossentry", frameBox, rightBox, noteBox, sty);
   }

   public SummaryBox(String name, FrameBox frameBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, sty);
      this.frameBox = frameBox;
      this.rightBox = rightBox;
      this.noteBox = noteBox;
   }

   @Override
   public Object clone()
   {
      return new SummaryBox(getName(), frameBox, rightBox, noteBox, getSty());
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

   protected void addPreEntryName(TeXObjectList list, GlsLabel glslabel,
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject statusVal = glslabel.getField("status");

      if (statusVal != null)
      {
         String status = statusVal.toString(parser);

         if (!status.equals("default") && !status.isEmpty())
         {
            list.add(parser.getListener().getControlSequence("icon"));
            list.add(parser.getListener().createGroup(status));
            list.add(parser.getListener().getSpace());
         }
      }
   }

   protected void addEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      list.add(parser.getListener().getControlSequence("glossentryname"));
      list.add(glslabel);
   }

   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   protected TeXObject getRightBoxContent(GlsLabel glslabel, TeXParser parser)
   throws IOException
   {
      TeXObjectList list = null;

      TeXObject val = glslabel.getField("defaultvalue");

      if (val != null)
      {
         list = parser.getListener().createStack();
         list.add(parser.getListener().getControlSequence("summarytagfmt"));
         list.add(parser.getListener().createGroup("default"));
         list.add(val, true);
      }

      val = glslabel.getField("initvalue");

      if (val != null)
      {
         if (list == null)
         {
            list = parser.getListener().createStack();
         }
         else
         {
            list.add(parser.getListener().getOther(';'));
            list.add(parser.getListener().getSpace());
         }

         list.add(parser.getListener().getControlSequence("summarytagfmt"));
         list.add(parser.getListener().createGroup("initial"));
         list.add(val, true);
      }

      TeXObject alias = glslabel.getField("alias");

      if (alias != null)
      {
         if (list == null)
         {
            list = parser.getListener().createStack();
         }
         else
         {
            list.add(parser.getListener().getOther(';'));
            list.add(parser.getListener().getSpace());
         }

         list.add(parser.getListener().getControlSequence("summarytagfmt"));
         list.add(parser.getListener().createGroup("alias"));
            list.add(parser.getListener().getSpace());
         list.add(parser.getListener().getControlSequence("glsfmtname"));
         list.add(TeXParserUtils.createGroup(parser, alias));
      }

      val = glslabel.getEntry().get("variants");

      if (val != null)
      {
         if (list == null)
         {
            list = parser.getListener().createStack();
         }
         else
         {
            list.add(parser.getListener().getOther(';'));
            list.add(parser.getListener().getSpace());
         }

         list.add(parser.getListener().getControlSequence("summarytagfmt"));
         list.add(parser.getListener().createGroup("variants"));

         list.add(parser.getListener().getControlSequence("code"));
         list.add(parser.getListener().createGroup(
           parser.expandToString(val, parser).replace(",", " ")));
      }

      int n = (list == null ? 0 : list.size());

      list = addStatus(list, glslabel, parser);

      boolean statusAdded = (n < (list == null ? 0 : list.size()));

      TeXObject providedby = glslabel.getField("providedby");

      if (providedby != null)
      {
         if (list == null)
         {
            list = parser.getListener().createStack();
         }
         else
         {
            if (!statusAdded)
            {
               list.add(parser.getListener().getOther(';'));
            }

            list.add(parser.getListener().getSpace());
         }

         list.add(providedby, true);
      }

      return list;
   }

   protected TeXObjectList addStatus(TeXObjectList contentList, 
     GlsLabel glslabel, TeXParser parser)
   throws IOException
   {
      return contentList;
   }

   protected TeXObject getNote(GlsLabel glslabel, TeXParser parser)
   {
      return glslabel.getField("note");
   }

   protected void preNote(TeXObjectList content, GlsLabel glslabel,
     TeXParser parser)
    throws IOException
   {
   }

   protected void addRow(TeXObjectList list, GlsLabel glslabel, 
      TeXParser parser, TeXObjectList stack, Vector<GlsLabel> modList)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      list.add(new TeXCsRef("glsadd"));
      list.add(glslabel);

      addPreEntryName(list, glslabel, parser, stack);

      Vector<String> targets = sty.getTargets(glslabel);

      if (targets != null)
      {
         String targetName = targets.firstElement();

         ControlSequence hyperlinkCs = parser.getControlSequence("hyperlink");

         if (hyperlinkCs != null)
         {
            list.add(hyperlinkCs);
            list.add(listener.createGroup(targetName));
         }
      }
      else
      {
         list.add(listener.getControlSequence("glstarget"));
         list.add(glslabel);
      }

      Group grp = listener.createGroup();
      list.add(grp);

      addEntryName(grp, glslabel, parser);
      addPostEntryName(list, glslabel, parser, stack);

      TeXObject rightBoxContent = getRightBoxContent(glslabel, parser);

      if (modList != null)
      {
         TeXObjectList sublist = listener.createStack();

         sublist.add(listener.getControlSequence("summarytagfmt"));

         sublist.add(listener.createGroup(
           modList.size() == 1 ? "modifier" : "modifiers"));

         for (GlsLabel lb : modList)
         {
            if (lb.getEntry() != null)
            {
               sublist.add(listener.getControlSequence("gls"));
            }

            sublist.add(lb);

            sublist.add(listener.getSpace());
         }

         if (rightBoxContent != null)
         {
            sublist.add(rightBoxContent, true);
         }

         rightBoxContent = sublist;
      }

      if (rightBoxContent != null && !rightBoxContent.isEmpty())
      {
         list.add(new StartFrameBox(rightBox));
         list.add(rightBoxContent, true);
         list.add(new EndFrameBox(rightBox));
      }
   }

   protected void preArgHook(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   protected void postArgHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   protected void initHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popModifier(parser, stack, '*');

      preArgHook(parser, stack);

      GlsLabel glslabel = popEntryLabel("glscurrententrylabel", parser, stack);

      postArgHook(glslabel, parser, stack);

      parser.startGroup();

      initHook(glslabel, parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList substack = listener.createStack();
      substack.add(frameBox);
 
      Group content = listener.createGroup();
      substack.add(content);

      GlossaryEntry entry = glslabel.getEntry();

      if (entry != null)
      {
         TeXObject modVal = entry.get("modifiers");
         String[] modLabels = null;

         Vector<GlsLabel> modList = null;

         if (modVal != null && !modVal.isEmpty())
         {
            modLabels = parser.expandToString(modVal, stack).split(" *, *");

            for (String lb : modLabels)
            {
               String modTokenLabel = "idx.mod."+lb;
               GlossaryEntry modTokenEntry = sty.getEntry(modTokenLabel);

               String altEntryLabel = glslabel.getLabel()+lb;
               GlossaryEntry altEntry = sty.getEntry(altEntryLabel);

               if (altEntry == null)
               {
                  if (modList == null)
                  {
                     modList = new Vector<GlsLabel>();
                  }

                  if (modTokenEntry == null)
                  {
                     modList.add(new GlsLabel("modlabel", lb, null));
                  }
                  else
                  {
                     modList.add(new GlsLabel("modlabel", modTokenLabel, modTokenEntry));
                  }
               }
            }
         }

         addRow(content, glslabel, parser, stack, modList);

         preNote(content, glslabel, parser);

         TeXObject note = getNote(glslabel, parser);

         if (note != null)
         {
            content.add(listener.getPar());
            content.add(new StartFrameBox(noteBox));

            content.add(listener.getControlSequence("summarynotefmt"));
            content.add(TeXParserUtils.createGroup(listener, note));

            content.add(new EndFrameBox(noteBox));
         }
      }

      TeXParserUtils.process(substack, parser, stack);

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected FrameBox frameBox, rightBox, noteBox;
}
