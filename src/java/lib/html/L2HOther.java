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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class L2HOther extends Other
{
   public L2HOther(int charCode)
   {
      super(charCode);
   }

   public L2HOther(char c)
   {
      super(c);
   }

   public Object clone()
   {
      return new L2HOther(getCharCode());
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (listener.isInDocEnv())
      {
         if (parser.isMathMode())
         {
            super.process(parser);
            return;
         }

         int c = getCharCode();

         if (c == (int)'\'')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack();
               listener.writeCodePoint(0x201D);
               return;
            }
         }
         else if (c == (int)'`')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack();
               listener.writeCodePoint(0x201C);
               return;
            }
         }
         else if (c == (int)'-')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack();

               obj = stack.peekStack();

               if (obj instanceof CharObject
                && (((CharObject)obj).getCharCode() == c))
               {
                  stack.popStack();
                  listener.writeCodePoint(0x2014);
                  return;
               }

               listener.writeCodePoint(0x2013);
               return;
            }
         }
         else if (c == (int)'?')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == (int)'`'))
            {
               stack.popStack();
               listener.writeCodePoint(0x00BF);
               return;
            }
         }
         else if (c == (int)'!')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == (int)'`'))
            {
               stack.popStack();
               listener.writeCodePoint(0x00A1);
               return;
            }
         }

         super.process(parser);
      }
   }
}
