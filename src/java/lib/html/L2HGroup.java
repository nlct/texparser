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

import com.dickimawbooks.texparserlib.*;

public class L2HGroup extends Group
{
   public L2HGroup()
   {
      super();
   }

   public L2HGroup(int capacity)
   {
      super(capacity);
   }

   public L2HGroup(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   @Override
   public TeXObjectList createList()
   {
      return new L2HGroup(capacity());
   }

   @Override
   public void startGroup(TeXParser parser)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (parser.isMathMode() && listener.useMathJax())
      {
         listener.writeCodePoint(parser.getBgChar());
      }

      super.startGroup(parser);
   }

   @Override
   public void endGroup(TeXParser parser)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      super.endGroup(parser);

      if (parser.isMathMode() && listener.useMathJax())
      {
         listener.writeCodePoint(parser.getEgChar());
      }
   }
}
