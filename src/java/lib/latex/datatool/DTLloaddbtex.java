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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLloaddbtex extends ControlSequence
{
   public DTLloaddbtex()
   {
      this("DTLloaddbtex");
   }

   public DTLloaddbtex(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLloaddbtex(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.popRequiredControlSequence(stack);
      String filename = parser.popRequiredString(stack);

      TeXParserListener listener = parser.getListener();

      TeXPath texPath = new TeXPath(parser, filename);

      if (parser.getControlSequence(cs.getName()) != null)
      {
         throw new LaTeXSyntaxException(parser, 
           LaTeXSyntaxException.ERROR_DEFINED, cs.toString(parser));
      }

      stack.push(new TeXCsRef("dtllastloadeddb"));
      stack.push(cs);
      stack.push(new TeXCsRef("let"));

      if (!listener.input(texPath))
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_FILE_NOT_FOUND, texPath);
      }

      listener.addFileReference(texPath);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
