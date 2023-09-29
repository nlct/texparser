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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLaddheaderalign extends ControlSequence
{
   public DTLaddheaderalign()
   {
      this("dtladdheaderalign");
   }

   public DTLaddheaderalign(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLaddheaderalign(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TokenListCommand tl = listener.popTokenListCommand(parser, stack);
      int type = TeXParserUtils.popInt(parser, stack);
      int colNum = TeXParserUtils.popInt(parser, stack);
      int maxCols = TeXParserUtils.popInt(parser, stack);

      if (colNum == 1)
      {
         tl.setContent((TeXObjectList)
           listener.getTokenListCommand("dtlbeforecols", stack).getContent().clone());
      }
      else
      {
         tl.rightConcat((TeXObjectList)
           listener.getTokenListCommand("dtlbetweencols", stack).getContent().clone());
      }

      tl.append(listener.getLetter('c'));

      if (colNum == maxCols)
      {
         tl.rightConcat((TeXObjectList)
           listener.getTokenListCommand("dtlaftercols", stack).getContent().clone());
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
