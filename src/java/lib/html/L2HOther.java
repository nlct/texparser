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
         int c = getCharCode();

         if (parser.isMathMode())
         {
            if (!listener.useMathJax())
            {
               if (c == (int)'\'')
               {
                  TeXObject obj = stack.peekStack();

                  if (obj instanceof CharObject
                   && (((CharObject)obj).getCharCode() == c))
                  {
                     stack.popStack(parser);
                     listener.writeCodePoint(0x2033);
                  }
                  else
                  {
                     listener.writeCodePoint(0x2032);
                  }

                  return;
               }
               else if (c == (int)'`')
               {
                  TeXObject obj = stack.peekStack();

                  if (obj instanceof CharObject
                   && (((CharObject)obj).getCharCode() == c))
                  {
                     stack.popStack(parser);
                     listener.writeCodePoint(0x2036);
                  }
                  else
                  {
                     listener.writeCodePoint(0x2035);
                  }

                  return;
               }
            }
         }
         else if (c == (int)'\'')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack(parser);
               listener.writeCodePoint(0x201D);
            }
            else
            {
               listener.writeCodePoint(0x2019);
            }

            return;
         }
         else if (c == (int)'`')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack(parser);
               listener.writeCodePoint(0x201C);
            }
            else
            {
               listener.writeCodePoint(0x2018);
            }

            return;
         }
         else if (c == (int)'-')
         {
            TeXObject obj = stack.peekStack();

            if (obj instanceof CharObject
             && (((CharObject)obj).getCharCode() == c))
            {
               stack.popStack(parser);

               obj = stack.peekStack();

               if (obj instanceof CharObject
                && (((CharObject)obj).getCharCode() == c))
               {
                  stack.popStack(parser);
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
               stack.popStack(parser);
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
               stack.popStack(parser);
               listener.writeCodePoint(0x00A1);
               return;
            }
         }

         super.process(parser);
      }
   }
}
