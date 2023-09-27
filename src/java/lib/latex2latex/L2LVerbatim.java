/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2LVerbatim extends Verbatim
{
   public L2LVerbatim()
   {
      super();
   }

   public L2LVerbatim(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2LVerbatim(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      process(parser);

      listener.beginVerbatim();

      Writeable writeable = listener.getWriteable();

      for (TeXObject obj : stack)
      {
         writeable.write(obj.toString(parser));
      }

      end(parser, stack);

      listener.endVerbatim();
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.writeCodePoint(parser.getEscChar());
      writeable.write("begin");
      writeable.writeCodePoint(parser.getBgChar());
      writeable.write(getName());
      writeable.writeCodePoint(parser.getEgChar());
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.writeCodePoint(parser.getEscChar());
      writeable.write("end");
      writeable.writeCodePoint(parser.getBgChar());
      writeable.write(getName());
      writeable.writeCodePoint(parser.getEgChar());
   }

}
