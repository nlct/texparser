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

import com.dickimawbooks.texparserlib.*;

public class L2LSpace extends Space
{
   public L2LSpace()
   {
      super();
   }

   public L2LSpace(char spaceChar)
   {
      super(spaceChar);
   }

   public L2LSpace(int spaceCodePoint)
   {
      super(spaceCodePoint);
   }

   public Object clone()
   {
      return new L2LSpace(getSpace());
   }

   public void process(TeXParser parser)
      throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();
      writeable.write(toString(parser));

      TeXObject nextObj = parser.pop();

      if (nextObj instanceof ControlSequence
       && ((ControlSequence)nextObj).getName().equals(" "))
      {
          // skip

         nextObj = parser.pop();
      }

      while (nextObj instanceof Ignoreable
           ||nextObj instanceof WhiteSpace)
      {
         writeable.write(nextObj.toString(parser));
         nextObj = parser.pop();
      }

      nextObj.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) 
      throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();
      writeable.write(toString(parser));

      while (stack.size() > 0)
      {
         TeXObject nextObj = stack.pop();

         writeable.write(nextObj.toString(parser));
      }
   }
}

