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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXParserListener;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class ExampleEnv extends Declaration
{
   public ExampleEnv()
   {
      this("example");
   }

   public ExampleEnv(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ExampleEnv(getName());
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
      TeXObject title = popArg(parser, stack);
      String label = popLabelString(parser, stack);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList content = listener.createStack();

      if (listener instanceof L2HConverter)
      {
         listener.stepcounter("example");

         ((L2HConverter)listener).writeliteral(
           String.format("<div class=\"example\" id=\"%s\">", label));

         ((L2HConverter)listener).writeliteral("<div class=\"title\">");

         content.add(listener.getControlSequence("nlctexampletag"));

         if (!title.isEmpty())
         {
            content.add(listener.getOther(':'));
            content.add(listener.getSpace());
            content.add(title);
         }

         TeXParserUtils.process(content, parser, stack);

         ((L2HConverter)listener).writeliteral("</div>");
      }
      else
      {
         content.add(listener.getPar());
         content.add(listener.getControlSequence("refstepcounter"));
         content.add(listener.createGroup("example"));
         content.add(listener.getControlSequence("nlctexampletag"));

         TeXParserUtils.process(content, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      if (listener instanceof L2HConverter)
      {
         ((L2HConverter)listener).writeliteral("</div>");
      }
      else
      {
         TeXParserUtils.process(listener.getPar(), parser, stack);
      }
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

}
