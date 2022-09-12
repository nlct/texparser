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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class ListOfFloats extends ControlSequence
{
   public ListOfFloats(String csname, String titlecsname, String ext)
   {
      super(csname);
      this.titlecsname = titlecsname;
      this.ext = ext;
   }

   @Override
   public Object clone()
   {
      return new ListOfFloats(getName(), titlecsname, ext);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList content = parser.getListener().createStack();

      ControlSequence secCs = parser.getControlSequence("chapter");

      if (secCs == null)
      {
         secCs = parser.getListener().getControlSequence("section");
      }

      content.add(secCs);
      content.add(parser.getListener().getOther('*'));

      content.add(parser.getListener().getControlSequence(titlecsname));

      content.add(parser.getListener().getControlSequence("@starttoc"));
      content.add(parser.getListener().createGroup(ext));

      TeXParserUtils.process(content, parser, stack);
   }

   protected String titlecsname, ext;
}
