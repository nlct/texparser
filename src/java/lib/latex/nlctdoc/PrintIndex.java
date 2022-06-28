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
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PrintIndex(getName(), getSty());
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
      TeXObject statusVal = glslabel.getEntry().get("status");

      if (statusVal != null)
      {
         String status = statusVal.toString(parser);

         if (!status.equals("default") && !status.isEmpty())
         {
            content.add(parser.getListener().getControlSequence("glssymbol"));
            content.add(parser.getListener().createGroup("sym."+status));
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
      String type = "index";

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXParserListener listener = parser.getListener();
         TeXObjectList list = listener.createStack();

         ControlSequence cs = parser.getControlSequence("chapter");

         if (cs == null)
         {
            cs = listener.getControlSequence("section");
         }

         list.add(cs);
         list.add(listener.getOther('*'));
         list.add(listener.createGroup("Index"));
         list.add(new TeXCsRef("label"));
         list.add(listener.createGroup("index"));

         TeXParserUtils.process(list, parser, stack);

         ControlSequence nameCs = listener.getControlSequence("glossentryname");

         ControlSequence item0 = listener.getControlSequence("nlctuserguideidx0");
         ControlSequence item1 = listener.getControlSequence("nlctuserguideidx1");
         ControlSequence item2 = listener.getControlSequence("nlctuserguideidx2");
         ControlSequence item3 = listener.getControlSequence("nlctuserguideidx3");

         for (String label : glossary)
         {
            GlossaryEntry entry = sty.getEntry(label);

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

            TeXObject loc = entry.get("location");

            if (loc != null)
            {
               content.add(listener.getControlSequence("qquad"));
               content.add(loc);
            }

            TeXParserUtils.process(list, parser, stack);
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
