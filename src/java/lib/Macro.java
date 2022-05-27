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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public abstract class Macro extends AbstractTeXObject
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

   // returns true if this has the same syntax as the other macro
   // (definition may be different)
   public boolean hasSyntax(Macro macro)
   {
      if (numArgs != macro.numArgs) return false;

      TeXObjectList list = macro.syntax;

      if (syntax == null && list == null) return true;

      if (syntax == null || list == null) return false;

      return syntax.equals(list);
   }

   public boolean hasNoSyntax()
   {
      return numArgs == 0 && (syntax == null || syntax.isEmpty());
   }

   /**
   * Pops a matching token if present.
   * This method will pop the next token but only if it's a CharObject
   * and matches one of the given char codes. Returns the char code 
   * if token was popped otherwise -1.
   * @param parser the TeX parser
   * @param stack the local stack (may be null or the parser, if no
   * local stack)
   * @param charCodes list of allowed character codes
   * @return the character code of the popped token or -1 if no
   * match
   * @throws IOException if I/O error
   */
   protected int popModifier(TeXParser parser, TeXObjectList stack, int... charCodes)
   throws IOException
   {
      return TeXParserUtils.popModifier(parser, stack, charCodes);
   }

   // pops an argument that should be a label that needs to be fully
   // expanded
   protected String popLabelString(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popLabelString(parser, stack);
   }

   // pops an optional argument that should be a label that needs to be fully
   // expanded
   protected String popOptLabelString(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popOptLabelString(parser, stack);
   }

   // pops a mandatory argument
   protected TeXObject popArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popArg(parser, stack);
   }

   // pops an optional argument
   // returns null if not present
   protected TeXObject popOptArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popOptArg(parser, stack);
   }

   // pops an argument and then fully expands it
   protected TeXObject popArgExpandFully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return TeXParserUtils.popArgExpandFully(parser, stack);
   }

   // pops an optional argument and then fully expands it
   protected TeXObject popOptArgExpandFully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return TeXParserUtils.popOptArgExpandFully(parser, stack);
   }

   protected int popInt(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popInt(parser, stack);
   }

   // pops an argument that should be a numerical value
   protected Numerical popNumericalArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popNumericalArg(parser, stack);
   }

   protected NumericRegister popNumericRegister(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popNumericRegister(parser, stack);
   }

   protected TeXDimension popDimensionArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return TeXParserUtils.popDimensionArg(parser, stack);
   }

   public abstract Object clone();

   @Override
   public String toString()
   {
      return String.format("%s[prefix=%d,syntax=%s]",
       getClass().getSimpleName(), getPrefix(), syntax);
   }

   // Is this a short macro?

   protected boolean isShort = true;

   // Is this macro allowed a prefix?

   protected boolean allowsPrefix = false;

   public static final byte PREFIX_NONE = (byte)0;
   public static final byte PREFIX_LONG = (byte)1;
   public static final byte PREFIX_GLOBAL = (byte)2;

   protected byte prefix = PREFIX_NONE;

   protected TeXObjectList syntax=null;

   protected int numArgs=0;
   protected boolean isDelimited=false;
}

