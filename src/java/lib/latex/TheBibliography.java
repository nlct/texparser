/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class TheBibliography extends DocumentBlockDec
{
   public TheBibliography()
   {
      this("thebibliography", "bibliography");
   }

   public TheBibliography(String name)
   {
      super(name);
   }

   public TheBibliography(String name, String type)
   {
      super(name, type);
   }

   public Object clone()
   {
      return new TheBibliography(getName(), getType());
   }

   public DocumentBlock createBlock(TeXParser parser)
   {
      ControlSequence cs = parser.getControlSequence("chapter");

      return new HierarchicalBlock(cs == null ? 1 : 0, getType());
   }

   public void setBlockAttributes(DocumentBlock block,
      TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getControlSequence("chapter");
      ControlSequence bibname = null;

      if (cs != null)
      {
         bibname = parser.getControlSequence("bibname");
      }

      if (bibname == null)
      {
         block.setAttribute("title", new TeXCsRef("refname"));
      }
      else
      {
         block.setAttribute("title", bibname);
      }
   }


   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      parser.putControlSequence(true, 
       new GenericCommand(true, "@listctr", null, 
          listener.createString("enumiv")));

      listener.resetcounter("enumiv");

      super.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      parser.putControlSequence(true, 
       new GenericCommand(true, "@listctr", null, 
          listener.createString("enumiv")));

      listener.resetcounter("enumiv");

      super.process(parser, stack);
   }
}
