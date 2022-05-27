/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

public class TeXParserUtils
{
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
   public static int popModifier(TeXParser parser, TeXObjectList stack, int... charCodes)
   throws IOException
   {
      TeXObject object;

      if (parser == stack || stack == null)
      {
         object = parser.peekStack();
      }
      else
      {
         object = stack.peekStack();
      }

      int found = -1;

      if (object instanceof CharObject)
      {
         int cp = ((CharObject)object).getCharCode();

         for (int mod : charCodes)
         {
            if (cp == mod)
            {
               found = mod;
               break;
            }
         }

         if (found != -1)
         {
            if (parser == stack || stack == null)
            {
               parser.popStack();
            }
            else
            {
               stack.popStack(parser);
            }
         }
      }

      return found;
   }

   /**
    * Pops an argument that should be a label that needs to be fully
    * expanded.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the label
    */
   public static String popLabelString(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return parser.expandToString(popArg(parser, stack), stack);
   }

   /**
    * Pops an optional argument that should be a label that needs to be fully
    * expanded.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the label
    */
   public static String popOptLabelString(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popOptArg(parser, stack);

      if (arg == null)
      {
         return null;
      }

      return parser.expandToString(arg, stack);
   }

   /**
     * Pops a mandatory argument.
     * @param parser the TeX parser
     * @param stack the stack or the parser or null
     * @return the argument
     */
   public static TeXObject popArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNextArg();
      }
      else
      {
         return stack.popArg(parser);
      }
   }

   /**
    * Pops an optional argument (delimited with <code>[</code> and <code>]</code>).
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the argument or null if not present
    */ 
   public static TeXObject popOptArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNextArg('[', ']');
      }
      else
      {
         return stack.popArg(parser, '[', ']');
      }
   }

   /**
    * Pops an argument and then fully expands it.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the fully expanded argument
    */ 
   public static TeXObject popArgExpandFully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg.canExpand() && arg instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser || stack == null)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      return arg;
   }

   /**
    * Pops an integer.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the integer value of the argument
    * @throws TeXSyntaxException if the argument isn't numerical
    */ 
   public static int popInt(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      Numerical num = popNumericalArg(parser, stack);

      return num.number(parser);
   }

   /**
    * Pops an argument that should be a numerical value.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the numerical argument
    * @throws TeXSyntaxException if the argument isn't numerical
    */ 
   public static Numerical popNumericalArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNumericalArg();
      }
      else
      {
         return stack.popNumericalArg(parser);
      }
   }

   /**
    * Pops an argument that should be a numeric register.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the numerical argument
    * @throws TeXSyntaxException if the argument isn't a numeric
    * register
    */ 
   public static NumericRegister popNumericRegister(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = popArg(parser, stack);
      NumericRegister reg = null;

      if (obj instanceof ControlSequence)
      {
         reg = parser.getSettings().getNumericRegister(((ControlSequence)obj).getName());
      }

      if (reg == null)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_REGISTER_NOT_NUMERIC, obj.toString(parser));
      }

      return reg;
   }

   /**
    * Pops an argument that should be a dimension.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the dimension
    * @throws TeXSyntaxException if the argument isn't a dimension
    */ 
   public static TeXDimension popDimensionArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = popArgExpandFully(parser, stack);

      if (obj instanceof InternalQuantity)
      {
         obj = ((InternalQuantity)obj).getQuantity(parser, stack);
      }

      if (obj instanceof TeXDimension)
      {
         return (TeXDimension)obj;
      }

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;

         return list.popDimension(parser);
      }

      throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_DIMEN_EXPECTED);
   }

}

