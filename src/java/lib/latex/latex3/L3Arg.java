/*
    Copyright (C) 2022-2023 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.latex3;

import com.dickimawbooks.texparserlib.*;

public class L3Arg
{
   public L3Arg(int id)
   {
      this(id, -1, -1, null);
   }

   public L3Arg(int id, boolean isShort)
   {
      this(id, -1, -1, null, isShort);
   }

   public L3Arg(int id, int token1, int token2, boolean isShort)
   {
      this(id, token1, token2, null, isShort);
   }

   public L3Arg(int id, int token1, int token2, TeXObject defValue)
   {
      this(id, token1, token2, defValue, true);
   }

   public L3Arg(int id, int token1, int token2, TeXObject defValue, boolean isShort)
   {
      this(id, token1, token2, defValue, isShort, true);
   }

   public L3Arg(int id, int token1, int token2, TeXObject defValue, boolean isShort,
     boolean ignoreSpace)
   {
      this.id = id;
      this.token1 = token1;
      this.token2 = token2;
      this.defaultValue = defValue;
      this.isShort = isShort;
      this.ignoreSpace = ignoreSpace;
   }

   public byte getPopStyle()
   {
      if (ignoreSpace)
      {
         return TeXObjectList.getArgPopStyle(isShort);
      }
      else if (isShort)
      {
         return TeXObjectList.POP_SHORT;
      }
      else
      {
         return (byte)0;
      }
   }

   public int getId()
   {
      return id;
   }

   public int getToken1()
   {
      return token1;
   }

   public int getToken2()
   {
      return token2;
   }

   public TeXObject getDefaultValue()
   {
      return defaultValue;
   }

   public boolean isIgnoreSpace()
   {
      return ignoreSpace;
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      if (!ignoreSpace)
      {
         builder.append('!');
      }

      if (!isShort)
      {
         builder.append('+');
      }

      builder.appendCodePoint(id);

      if (token1 != -1)
      {
         builder.appendCodePoint(token1);
      }

      if (token2 != -1)
      {
         builder.appendCodePoint(token2);
      }

      if (defaultValue != null)
      { 
         builder.append(String.format("{%s}", defaultValue.format()));
      }

      return builder.toString();
   }

   public static TeXObject createNoValue(TeXParser parser)
   {
      return new DataObjectList(parser.getListener(), "-NoValue-", true);
   }

   public static boolean isNoValue(TeXObject arg, TeXParser parser)
   {
      if (arg instanceof DataObjectList)
      {
         return arg.toString(parser).equals("-NoValue-");
      }

      if (parser.isStack(arg))
      {
         TeXObjectList list = (TeXObjectList)arg;

         if (list.size() == 1)
         {
            TeXObject obj = list.firstElement();

            if (obj instanceof DataObjectList)
            {
               return obj.toString(parser).equals("-NoValue-");
            }
         }
      }

      return false;
   }

   int id;
   int token1=-1, token2=-1;
   TeXObject defaultValue;
   boolean isShort = true;
   boolean ignoreSpace = true;
}
