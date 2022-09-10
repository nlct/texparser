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
import com.dickimawbooks.texparserlib.latex.*;

public class DefListDec extends DescriptionDec
{
   public DefListDec()
   {
      this("deflist");
   }

   public DefListDec(String name)
   {
      super(name, DescriptionStyle.BLOCK_TITLE);
   }

   @Override
   public Object clone()
   {
      return new DefListDec(getName());
   }

   @Override
   public void setup(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      super.setup(parser, stack);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList listsettings = new TeXObjectList();

      listsettings.add(listener.getControlSequence("let"));
      listsettings.add(new TeXCsRef("itemtitle"));
      listsettings.add(listener.getControlSequence("texparser@listitem"));

      setup(parser, stack, new TeXObjectList(), listsettings);
   }

}
