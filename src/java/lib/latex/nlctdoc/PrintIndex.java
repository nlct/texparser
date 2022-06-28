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

         ControlSequence targetCs = listener.getControlSequence("glstarget");
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

            content.add(targetCs);
            content.add(glslabel);

            Group grp = listener.createGroup();
            content.add(grp);

            grp.add(nameCs);
            grp.add(glslabel);

            TeXObject loc = entry.get("location");

            if (loc != null)
            {
               content.add(listener.getSpace());
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
