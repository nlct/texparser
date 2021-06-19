/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrKeywords extends RobustDeclaration
{
   public JmlrKeywords()
   {
      this("keywords");
   }

   public JmlrKeywords(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new JmlrKeywords(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      block = new DocumentBlock("keywords");
      listener.startBlock(block);

      Group grp = listener.createGroup("Keywords:");

      stack.push(new TeXCsRef("ignorespaces"));
      stack.push(listener.getSpace());
      stack.push(grp);
      stack.push(new TeXCsRef("textbf"));
      stack.push(new TeXCsRef("small"));
   }

   @Override
   public void end(TeXParser parser)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      listener.endBlock(block);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   protected DocumentBlock block;
}
