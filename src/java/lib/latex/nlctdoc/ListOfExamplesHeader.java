/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.latex.*;

public class ListOfExamplesHeader extends ControlSequence
{
   public ListOfExamplesHeader()
   {
      this("listofexamplesheader");
   }

   public ListOfExamplesHeader(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ListOfExamplesHeader(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList substack = listener.createStack();
      substack.add(listener.getPar());

      substack.add(listener.createString("If an example shows the icon "));
      substack.add(listener.getControlSequence("exampledownloadtexicon"));
      substack.add(listener.createString(" then you can click on that icon to try downloading the example source code from a location relative to this document. You can also try using: "));

      substack.add(listener.getControlSequence("texdocref"));

      Group grp = listener.createGroup();
      substack.add(grp);

      grp.add(listener.createString("-l "));
      grp.add(listener.getControlSequence("jobname"));
      grp.add(listener.createString("-example"));
      grp.add(listener.getControlSequence("meta"));
      grp.add(listener.createGroup("nnn"));

      substack.add(listener.createString(
       "where "));
      substack.add(listener.getControlSequence("meta"));
      substack.add(listener.createGroup("nnn"));
      substack.add(listener.createString(
       " is the example number zero-padded to three digits to find out if the example files are installed on your device. "));

      substack.add(listener.getPar());

      TeXParserUtils.process(substack, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }

}
