/*
    Copyright (C) 2022-2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class LoadGlsEntries extends Input
{
   public LoadGlsEntries()
   {
      this("loadglsentries");
   }

   public LoadGlsEntries(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new LoadGlsEntries(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String type = popOptLabelString(parser, stack);

      if (type != null)
      {
         ControlSequence orgTypeCs = listener.getControlSequence("glsdefaulttype");
         String orgType = parser.expandToString(orgTypeCs, stack);

         String filename = popLabelString(parser, stack);

         listener.putControlSequence(true, 
           new TextualContentCommand("glsdefaulttype", type));

         stack.push(new AddControlSequenceObject(orgTypeCs));

         TeXPath path = new TeXPath(parser, filename, getDefaultExtension());

         stack.push(new TeXPathObject(path));
      }

      super.process(parser, stack);

   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String type = popOptLabelString(parser, parser);

      if (type != null)
      {
         ControlSequence orgTypeCs = listener.getControlSequence("glsdefaulttype");
         String orgType = parser.expandToString(orgTypeCs, parser);

         String filename = popLabelString(parser, parser);

         listener.putControlSequence(true, 
           new TextualContentCommand("glsdefaulttype", type));

         parser.push(listener.createGroup(orgType));
         parser.push(new TeXCsRef("glsdefaulttype"));
         parser.push(new TeXCsRef("def"));

         TeXPath path = new TeXPath(parser, filename, getDefaultExtension());

         parser.push(new TeXPathObject(path));
      }

      super.process(parser);
   }
}
