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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;

public class L2LBibliography extends L2LControlSequence
{
   public L2LBibliography()
   {
      this("bibliography");
   }

   public L2LBibliography(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2LBibliography(getName());
   }

   private void processBib(TeXParser parser, TeXObject arg)
      throws IOException
   {
      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      String bibStr = arg.toString(parser);

      Writeable writeable = listener.getWriteable();

      writeable.write(toString(parser));
      writeable.writeCodePoint(parser.getBgChar());
      writeable.write(bibStr);
      writeable.writeCodePoint(parser.getEgChar());

      String[] bibList = bibStr.split(" *, *");

      TeXPath[] bibPaths = new TeXPath[bibList.length];

      for (int i = 0; i < bibList.length; i++)
      {
         bibPaths[i] = new TeXPath(parser, bibList[i].trim(), "bib");
      }

      listener.bibliography(bibPaths);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      processBib(parser, stack.popArg(parser));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      processBib(parser, parser.popNextArg());
   }

}
