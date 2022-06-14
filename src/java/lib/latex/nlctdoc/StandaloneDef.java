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
   public StandaloneDef(String name, TaggedColourBox taggedBox, 
      FrameBox rightBox, FrameBox noteBox, GlossariesSty sty)
   {
      super(name, sty);
      this.taggedBox = taggedBox;
      this.rightBox = rightBox;
      this.noteBox = noteBox;
   }

   @Override
   public Object clone()
   {
      return new StandaloneDef(getName(), taggedBox, rightBox, noteBox, getSty());
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

   protected void addEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
      list.add(parser.getListener().getControlSequence("glossentryname"));
      list.add(glslabel);
   }

   protected void addPostEntryName(TeXObjectList list, GlsLabel glslabel, TeXParser parser)
   {
   }

   protected TeXObject getRightBoxContent(GlsLabel glslabel, TeXParser parser)
   {
      return null;
   }

   protected TeXObject getNote(GlsLabel glslabel, TeXParser parser)
   {
      return glslabel.getEntry().get("note");
   }

   protected void addRow(TeXObjectList list, GlsLabel glslabel, 
      TeXParser parser, Vector<GlsLabel> modList)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      list.add(listener.getControlSequence("glstarget"));
      list.add(glslabel);

      Group grp = listener.createGroup();
      list.add(grp);

      addEntryName(grp, glslabel, parser);
      addPostEntryName(list, glslabel, parser);

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

      list.add(listener.getPar());
   }

   protected void postArgHook(GlsLabel glslabel, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popModifier(parser, stack, '*');

      GlsLabel glslabel = popEntryLabel("glscurrententrylabel", parser, stack);

      postArgHook(glslabel, parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      GlossaryEntry entry = glslabel.getEntry();

      list.add(listener.getControlSequence("glsadd"));
      list.add(glslabel);

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
                     modList.add(new GlsLabel("modlabel", modTokenEntry));
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

         addRow(list, glslabel, parser, modList);

         if (modEntries != null)
         {
            for (GlsLabel lb : modEntries)
            {
               addRow(list, lb, parser, null);
            }
         }

         TeXObject note = getNote(glslabel, parser);

         list.add(new StartFrameBox(noteBox));

         if (note != null)
         {
            list.add(note, true);
         }

         list.add(new EndFrameBox(noteBox));
      }

      list.add(taggedBox.getEndDeclaration());

      stack.push(list, true);

      if (parser == stack)
      {
         taggedBox.process(parser);
      }
      else 
      {
         taggedBox.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   protected TaggedColourBox taggedBox;
   protected FrameBox rightBox, noteBox;
}
