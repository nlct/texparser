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

public class PrintMain extends AbstractGlsCommand
{
   public PrintMain(GlossariesSty sty)
   {
      this("printmain", sty);
   }

   public PrintMain(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new PrintMain(getName(), getSty());
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
      String type = "main";

      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);

      Glossary glossary = sty.getGlossary(type);

      if (glossary != null && !glossary.isEmpty())
      {
         TeXObject title = null;

         if (options != null)
         {
            title = options.getValue("title");
         }

         TeXParserListener listener = parser.getListener();
         TeXObjectList list = listener.createStack();

         ControlSequence cs = parser.getControlSequence("chapter");

         if (cs == null)
         {
            cs = listener.getControlSequence("section");
         }

         list.add(cs);
         list.add(listener.getOther('*'));

         if (title == null)
         {
            list.add(listener.getControlSequence("glossaryname"));
         }
         else
         {
            list.add(TeXParserUtils.createGroup(listener, title));
         }

         list.add(new TeXCsRef("label"));
         list.add(listener.createGroup("glossary"));
         list.add(listener.getControlSequence("begin"));

         String env = "texparser@block@list";
         list.add(listener.createGroup(env));

         ControlSequence defCs = listener.getControlSequence("csdef");
         ControlSequence itemCs = listener.getControlSequence(
          "texparser@listitem");
         ControlSequence itemDescCs = listener.getControlSequence(
          "texparser@listdesc");
         ControlSequence targetCs = listener.getControlSequence("glstarget");
         ControlSequence nameCs = listener.getControlSequence("Glossentryname");
         ControlSequence descCs = listener.getControlSequence("glossentrydesc");
         ControlSequence postDescCs = listener.getControlSequence("glspostdescription");

         for (String label : glossary)
         {
            GlsLabel glslabel = new GlsLabel("glscurrententrylabel@"+label,
              label, sty.getEntry(label));

            list.add(defCs);
            list.add(listener.createGroup("glscurrententrylabel"));
            list.add(listener.createGroup(label));
            list.add(itemCs);

            Group grp = listener.createGroup();
            list.add(grp);

            grp.add(new TeXCsRef("glsadd"));
            grp.add(TeXParserUtils.createGroup(listener, glslabel));

            grp.add(targetCs);
            grp.add(glslabel);

            Group subgrp = listener.createGroup();
            grp.add(subgrp);
            subgrp.add(nameCs);
            subgrp.add(TeXParserUtils.createGroup(listener, glslabel));

            list.add(itemDescCs);

            grp = listener.createGroup();
            list.add(grp);

            grp.add(descCs);
            grp.add(TeXParserUtils.createGroup(listener, glslabel));
            grp.add(postDescCs);
         }

         list.add(listener.getControlSequence("end"));
         list.add(listener.createGroup(env));

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
