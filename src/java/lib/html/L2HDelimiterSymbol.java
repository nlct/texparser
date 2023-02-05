/*
    Copyright (C) 2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HDelimiterSymbol extends DelimiterSymbol
{
   public L2HDelimiterSymbol(String name, int codePoint)
   {
      super(name, codePoint);
   }

   @Override
   public Object clone()
   {
      return new L2HDelimiterSymbol(getName(), getCharCode());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.useMathJax() ? null : super.expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      return listener.useMathJax() ? null : super.expandonce(parser, stack);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.useMathJax())
      {
         // assume all DelimiterSymbol objects are supported by MathJax
         listener.write(format());
      }
      else
      {
         super.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
   throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.useMathJax())
      {
         // assume all DelimiterSymbol objects are supported by MathJax
         listener.write(format());
      }
      else
      {
         super.process(parser);
      }
   }

}
