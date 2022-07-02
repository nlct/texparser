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

public class CreateExample extends ControlSequence
{
   public CreateExample()
   {
      this("createexample");
   }

   public CreateExample(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new CreateExample(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      popModifier(parser, stack, '*');

      KeyValList options = TeXParserUtils.popOptKeyValList(parser, stack);
      popArg(parser, stack);
      popArg(parser, stack);

      TeXObject title = null;
      TeXObject description = null;
      String label = null;

      if (options != null)
      {
         title = options.get("title");
         description = options.get("description");

         if (description != null && parser.isStack(description))
         {
            ((TeXObjectList)description).add(listener.getOther('.'));
         }

         TeXObject obj = options.get("label");

         if (obj != null)
         {
            label = parser.expandToString(obj, stack);
         }
      }

      TeXObjectList substack = listener.createStack();

      substack.add(listener.getControlSequence("refstepcounter"));
      substack.add(listener.createGroup("example"));

      if (label != null)
      {
         substack.add(listener.getControlSequence("label"));
         substack.add(listener.createGroup(label));
      }

      TeXParserUtils.process(substack, parser, stack);

      substack.add(listener.getControlSequence("nlctexampletag"));

      if (title != null)
      {
         substack.add(listener.getOther(':'));
         substack.add(listener.getSpace());
         substack.add(title);
         substack.add(listener.getSpace());
      }

      String dir = parser.expandToString(listener.getControlSequence("examplesdir"),
         stack);
      String base = parser.expandToString(
         listener.getControlSequence("nlctexamplefilebasename"), stack);

      substack.add(listener.getControlSequence("href"));
      substack.add(listener.createGroup(dir+"/"+base+".tex"));
      substack.add(listener.getControlSequence("exampledownloadtexicon"));

      substack.add(listener.getSpace());

      String pdfPath = dir+"/"+base+".pdf";

      substack.add(listener.getControlSequence("href"));
      substack.add(listener.createGroup(pdfPath));
      substack.add(listener.getControlSequence("exampledownloadpdficon"));

      substack.add(listener.getPar());

      TeXParserUtils.process(substack, parser, stack);

      KeyValList imgOptions = new KeyValList();

      if (description != null)
      {
         imgOptions.put("alt", description);
      }

      imgOptions.put("scale", listener.createString("0.75"));

      listener.includegraphics(stack, imgOptions, pdfPath);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
