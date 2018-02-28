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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public abstract class Macro implements TeXObject
{
   public boolean getAllowsPrefix()
   {
      return allowsPrefix;
   }

   protected void setAllowsPrefix(boolean allow)
   {
      allowsPrefix = allow;
   }

   public boolean isShort()
   {
      return ((prefix & (int)PREFIX_LONG) == (int)PREFIX_LONG) ? false : isShort;
   }

   protected void setShort(boolean isShort)
   {
      this.isShort = isShort;
   }

   // prefix should be cleared after use and doesn't change isShort

   public void setPrefix(byte prefix)
   {
      if (allowsPrefix)
      {
         this.prefix = prefix;
      }
   }

   public byte getPrefix()
   {
      return allowsPrefix ? prefix : PREFIX_NONE;
   }

   public void clearPrefix()
   {
      prefix = PREFIX_NONE;
   }

   public TeXObjectList getSyntax()
   {
      return syntax;
   }

   protected void setSyntax(TeXObjectList syntax)
   {
      this.syntax = syntax;
      this.isDelimited = false;

      if (syntax != null)
      {
         numArgs = 0;

         for (TeXObject obj : syntax)
         {
            if (obj instanceof Param)
            {
               if (((Param)obj).getDigit() == -1)
               {
                  isDelimited = true;
               }
               else
               {
                  numArgs++;
               }
            }
         }
      }

   }

   protected void setSyntax(TeXParserListener listener, int numberOfArguments)
   {
      this.isDelimited = false;
      this.numArgs = numberOfArguments;

      if (numArgs == 0)
      {
         syntax = null;
      }
      else
      {
         syntax = new TeXObjectList(numArgs);

         for (int i = 1; i <= numArgs; i++)
         {
            syntax.add(listener.getParam(i));
         }
      }
   }

   protected void setSyntax(TeXObject[] syntaxArray)
   {
      this.isDelimited = false;

      numArgs = 0;

      if (syntaxArray == null || syntaxArray.length == 0)
      {
         syntax = null;
      }
      else
      {
         syntax = new TeXObjectList(syntaxArray.length);

         for (int i = 0; i < syntaxArray.length; i++)
         {
            if (syntaxArray[i] instanceof Param)
            {
               if (((Param)syntaxArray[i]).getDigit() == -1)
               {
                  isDelimited = true;
               }
               else
               {
                  numArgs++;
               }
            }

            syntax.add(syntaxArray[i]);
         }
      }
   }

   public boolean hasSyntax(Macro macro)
   {
      if (numArgs != macro.numArgs) return false;

      TeXObjectList list = macro.syntax;

      if (syntax == null && list == null) return true;

      if (syntax == null || list == null) return false;

      return syntax.equals(list);
   }

   public boolean isPar()
   {
      return false;
   }

   public abstract Object clone();

   // Is this a short macro?

   protected boolean isShort = true;

   // Is this macro allowed a prefix?

   protected boolean allowsPrefix = false;

   public String toString()
   {
      return String.format("%s[prefix=%d,syntax=%s]",
       getClass().getSimpleName(), getPrefix(), syntax);
   }

   public static final byte PREFIX_NONE = (byte)0;
   public static final byte PREFIX_LONG = (byte)1;
   public static final byte PREFIX_GLOBAL = (byte)2;

   protected byte prefix = PREFIX_NONE;

   protected TeXObjectList syntax=null;

   protected int numArgs=0;
   protected boolean isDelimited=false;
}

