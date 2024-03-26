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

public class StandaloneDef extends AbstractGlsCommand
{
   public StandaloneDef(String name, FrameBoxEnv outerBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, sty);
      this.outerBox = outerBox;
      this.rightBox = rightBox;
      this.noteBox = noteBox;
   }

   @Override
   public Object clone()
   {
      return new StandaloneDef(getName(), outerBox, rightBox, noteBox, getSty());
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

   protected void addPreEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
   }

   protected void addEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      list.add(parser.getListener().getControlSequence("glossentryname"));
      list.add(glslabel);
   }

   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
   }

   protected TeXObject getRightBoxContent(GlsLabel glslabel, TeXParser parser)
   throws IOException
   {
      TeXObjectList list = null;

      TeXObject val = glslabel.getEntry().get("defaultvalue");

      if (val != null)
      {
         list = parser.getListener().createStack();
         list.add(parser.getListener().getControlSequence("summarytagfmt"));
         list.add(parser.getListener().createGroup("default"));
         list.add(val);
      }

      val = glslabel.getEntry().get("initvalue");

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
         list.add(val);
      }

      val = glslabel.getEntry().get("alias");

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
         list.add(parser.getListener().createGroup("alias"));

         list.add(parser.getListener().getControlSequence("aliasref"));
         list.add(parser.getListener().createGroup(val.toString(parser)));
      }

      TeXObject statusVal = glslabel.getEntry().get("status");

      if (statusVal != null)
      {
         String status = parser.expandToString(statusVal, parser);

         if (!status.equals("default"))
         {
            if (list == null)
            {
               list = parser.getListener().createStack();
            }
            else
            {
               list.add(parser.getListener().getSpace());
            }

            list.add(parser.getListener().getControlSequence("icon"));
            list.add(parser.getListener().createGroup(status));
         }
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

      return list;
   }

   protected ControlSequence getNoteFmt(TeXParser parser)
   {
      return null;
   }

   protected TeXObject getNote(GlsLabel glslabel, TeXParser parser)
   {
      TeXObject note = glslabel.getEntry().get("note");

      if (note == null)
      {
         return null;
      }

      ControlSequence cs = getNoteFmt(parser);

      if (cs == null)
      {
         return note;
      }

      TeXObjectList list = parser.getListener().createStack();
      list.add(cs);
      list.add(TeXParserUtils.createGroup(parser, note));

      return list;
   }

   protected void preNote(TeXObjectList content, GlsLabel glslabel,
     TeXParser parser)
    throws IOException
   {
   }

   protected String getDefinitionCsName()
   {
      return "code";
   }

   protected void addRow(TeXObjectList list, GlsLabel glslabel, 
      TeXParser parser, Vector<GlsLabel> modList)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String csname = getDefinitionCsName();
      TeXObjectList defnList = list;

      if (csname != null)
      {
         ControlSequence cs = listener.getControlSequence(csname);
         list.add(cs);
         defnList = listener.createGroup();
         list.add(defnList);
      }

      addPreEntryName(defnList, glslabel, parser);

      defnList.add(listener.getControlSequence("glstarget"));
      defnList.add(glslabel);

      Group grp = listener.createGroup();
      defnList.add(grp);

      addEntryName(grp, glslabel, parser);
      addPostEntryName(defnList, glslabel, parser);

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

      if (rightBoxContent != null)
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

   protected String getMainTag()
   {
      return "";
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popModifier(parser, stack, '*');

      preArgHook(parser, stack);

      GlsLabel glslabel = popEntryLabel("glscurrententrylabel", parser, stack);

      postArgHook(glslabel, parser, stack);

      TeXParserListener listener = parser.getListener();

      parser.startGroup();

      initHook(glslabel, parser, stack);

      TeXObjectList content = listener.createStack();
      content.add(outerBox);

      GlossaryEntry entry = glslabel.getEntry();

      content.add(listener.getControlSequence("mainglsadd"));
      content.add(glslabel);
      content.add(listener.createGroup(getMainTag()));

      if (entry != null)
      {
         TeXObject modVal = entry.get("modifiers");
         String[] modLabels = null;

         Vector<GlsLabel> modList = null;
         Vector<GlsLabel> modEntries = null;

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
               else
               {
                  if (modEntries == null)
                  {
                     modEntries = new Vector<GlsLabel>();
                  }

                  modEntries.add(new GlsLabel("glslabelmod", altEntry));
               }
            }
         }

         addRow(content, glslabel, parser, modList);

         if (modEntries != null)
         {
            for (GlsLabel lb : modEntries)
            {
               content.add(listener.getPar());
               content.add(listener.getControlSequence("glsadd"));
               content.add(lb);

               addRow(content, lb, parser, null);
            }
         }

         preNote(content, glslabel, parser);

         TeXObject note = getNote(glslabel, parser);

         if (note != null)
         {
            content.add(listener.getPar());
            content.add(new StartFrameBox(noteBox));

            content.add(note, true);

            content.add(new EndFrameBox(noteBox));
         }
      }

      content.add(outerBox.getEndDeclaration());

      TeXParserUtils.process(content, parser, stack);

      parser.endGroup();
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected FrameBoxEnv outerBox;
   protected FrameBox rightBox, noteBox;
}
