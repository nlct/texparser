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
package com.dickimawbooks.texparserlib.latex2latex;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;

import com.dickimawbooks.texparserlib.latex.Bibliography;

public class L2LBibliography extends Bibliography
{
   public L2LBibliography()
   {
      this("bibliography");
   }

   public L2LBibliography(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2LBibliography(getName());
   }

   @Override
   protected boolean doBibliography(TeXParser parser, TeXObject arg)
       throws IOException
   {
      LaTeX2LaTeX listener = (LaTeX2LaTeX)parser.getListener();

      String bibStr = arg.toString(parser);

      Writeable writeable = listener.getWriteable();

      writeable.write(toString(parser));
      writeable.writeCodePoint(parser.getBgChar());
      writeable.write(bibStr);
      writeable.writeCodePoint(parser.getEgChar());

      return super.doBibliography(parser, arg);
   }
}
