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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Bibliography extends ControlSequence
{
   public Bibliography()
   {
      this("bibliography");
   }

   public Bibliography(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Bibliography(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      doBibliography(parser, arg);
    }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      doBibliography(parser, arg);
   }

   protected boolean doBibliography(TeXParser parser, TeXObject arg)
       throws IOException
   {

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      String[] bibList = arg.toString(parser).split(",");

      TeXPath[] bibPaths = new TeXPath[bibList.length];

      for (int i = 0; i < bibList.length; i++)
      {
         bibPaths[i] = new TeXPath(parser, bibList[i].trim(), "bib");
         listener.addFileReference(bibPaths[i]);
      }

      TeXPath texPath = new TeXPath(parser, listener.getBblFile());

      listener.addFileReference(texPath);

      return listener.bibliography(bibPaths, texPath);
  }

}
