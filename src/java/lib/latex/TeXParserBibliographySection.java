/*
    Copyright (C) 2026 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class TeXParserBibliographySection extends ControlSequence
{
   public TeXParserBibliographySection()
   {
      this("texparser@bibsection");
   }

   public TeXParserBibliographySection(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new TeXParserBibliographySection(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      TeXObjectList substack = listener.createStack();

      String sectionCsname = "section";
      String titleCsname = "refname";

      LaTeXCls docCls = listener.getDocumentClass();

      if (docCls != null && docCls.supportsChapter())
      {
         sectionCsname = "chapter";
         titleCsname = "bibname";
      }

      substack.add(listener.getControlSequence(sectionCsname));
      substack.add(listener.getOther('*'));
      substack.add(TeXParserUtils.createGroup(listener,
        listener.getControlSequence(titleCsname)));

      TeXParserUtils.process(substack, parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

}
