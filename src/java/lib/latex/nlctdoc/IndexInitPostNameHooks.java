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

public class IndexInitPostNameHooks extends ControlSequence
{
   public IndexInitPostNameHooks()
   {
      this("nlctguideindexinitpostnamehooks");
   }

   public IndexInitPostNameHooks(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new IndexInitPostNameHooks(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      parser.putControlSequence(true, 
        new GenericCommand(true, "glsxtrpostnameenvironment", null,
         TeXParserUtils.createStack(listener,
          listener.getSpace(), new TeXCsRef("idxenvname"))));

      parser.putControlSequence(true, 
        new GenericCommand(true, "glsxtrpostnamepackage", null,
         TeXParserUtils.createStack(listener,
          listener.getSpace(), new TeXCsRef("idxpackagename"))));

      parser.putControlSequence(true, 
        new GenericCommand(true, "glsxtrpostnameclass", null,
         TeXParserUtils.createStack(listener,
          listener.getSpace(), new TeXCsRef("idxclassname"))));

      parser.putControlSequence(true, 
        new GenericCommand(true, "glsxtrpostnamecounter", null,
         TeXParserUtils.createStack(listener,
          listener.getSpace(), new TeXCsRef("idxcountername"))));

      parser.putControlSequence(true,
        new GenericCommand(true, "glsxtrpostnameacronym", null, 
          new TeXCsRef("abbrpostnamehook")));

      parser.putControlSequence(true,
        new GenericCommand(true, "glsxtrpostnameabbreviation", null, 
          new TeXCsRef("abbrpostnamehook")));

      parser.putControlSequence(true,
        new GenericCommand(true, "glsxtrpostnametermabbreviation", null, 
          new TeXCsRef("abbrpostnamehook")));

      parser.putControlSequence(true,
        new GenericCommand(true, "glsxtrpostnamedualindexabbreviation", null, 
          new TeXCsRef("abbrpostnamehook")));
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      process(parser, parser);
   }
}
