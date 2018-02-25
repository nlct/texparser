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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HTextSubscript extends ControlSequence
{
   public L2HTextSubscript()
   {
      this("textsubscript");
   }

   public L2HTextSubscript(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HTextSubscript(getName());
   }

   /*
    * If entire argument is support by Unicode characters, use
    * them instead. 
    */ 

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (((L2HConverter)parser.getListener()).supportUnicodeScript()
           && hasUnicodeSupport(arg))
      {
         stack.push(convert(parser, arg));
      }
      else
      {
         // Don't use listener as this should be in text mode
         // (push in reverse order)

         stack.push(new HtmlTag("</sub>"));
         stack.push(arg);
         stack.push(new HtmlTag("<sub>"));
      }
   }

   public void process(TeXParser parser)
   throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (((L2HConverter)parser.getListener()).supportUnicodeScript()
           && hasUnicodeSupport(arg))
      {
         parser.push(convert(parser, arg));
      }
      else
      {
         // Don't use listener as this should be in text mode
         // (push in reverse order)

         parser.push(new HtmlTag("</sub>"));
         parser.push(arg);
         parser.push(new HtmlTag("<sub>"));
      }
   }

   private TeXObject convert(TeXParser parser, TeXObject arg)
   {
      if (arg instanceof CharObject)
      {
         return getUnicode(parser, (CharObject)arg);
      }
      else if (arg instanceof TeXNumber)
      {
         return convert(parser, parser.getListener().createString(
            arg.toString(parser)));
      }
      else
      {
         TeXObjectList argList = (TeXObjectList)arg;

         for (int i = 0; i < argList.size(); i++)
         {
            argList.set(i, convert(parser, argList.get(i)));
         }

         return argList;
      }
   }

   private CharObject getUnicode(TeXParser parser, CharObject obj)
   {
      int code = obj.getCharCode();

      for (int i = 0; i < UNICODE_SUBSCRIPTS.length; i++)
      {
         if (UNICODE_SUBSCRIPTS[i][0] == code)
         {
            return parser.getListener().getOther(UNICODE_SUBSCRIPTS[i][1]);
         }
      }

      // Should already have been checked for this
      throw new IllegalArgumentException("Unknown conversion to subscript: "
        +obj);
   }

   private boolean hasUnicodeSupport(TeXObject arg)
   {
      if (arg instanceof TeXObjectList && !(arg instanceof MathGroup))
      {
         for (TeXObject obj : (TeXObjectList)arg)
         {
            if (!hasUnicodeSupport(obj)) return false;
         }

         return true;
      }
      else if (arg instanceof TeXNumber)
      {
         return true;
      }
      else if (arg instanceof CharObject)
      {
         int code = ((CharObject)arg).getCharCode();

         for (int i = 0; i < UNICODE_SUBSCRIPTS.length; i++)
         {
            if (UNICODE_SUBSCRIPTS[i][0] == code)
            {
               return true;
            }
         }
      }

      return false;
   }

   public static final int[][] UNICODE_SUBSCRIPTS = new int[][]
   {
      new int[] {'0', 0x2080},
      new int[] {'1', 0x2081},
      new int[] {'2', 0x2082},
      new int[] {'3', 0x2083},
      new int[] {'4', 0x2084},
      new int[] {'5', 0x2085},
      new int[] {'6', 0x2086},
      new int[] {'7', 0x2087},
      new int[] {'8', 0x2088},
      new int[] {'9', 0x2089},
      new int[] {'+', 0x208A},
      new int[] {'-', 0x208B},
      new int[] {0x2212, 0x208B},
      new int[] {'=', 0x208C},
      new int[] {'(', 0x208D},
      new int[] {')', 0x208D},
      new int[] {'a', 0x2090},
      new int[] {'e', 0x2091},
      new int[] {'o', 0x2092},
      new int[] {'x', 0x2093},
      new int[] {0x0259, 0x2094},// schwa
      new int[] {'h', 0x2095},
      new int[] {'k', 0x2096},
      new int[] {'l', 0x2097},
      new int[] {'m', 0x2098},
      new int[] {'n', 0x2099},
      new int[] {'p', 0x209A},
      new int[] {'s', 0x209B},
      new int[] {'t', 0x209C},
      new int[] {'j', 0x2C7C}
   };

}
