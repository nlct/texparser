/*
    Copyright (C) 2022-2024 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.CsvList;

public class TeXParserUtils
{
   public static TeXObject peek(TeXParser parser, TeXObjectList stack, byte popStyle)
   throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.peekStack(popStyle);
      }
      else
      {
         return stack.peekStack(popStyle);
      }
   }

   public static TeXObject pop(TeXParser parser, TeXObjectList stack, byte popStyle)
   throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popStack(popStyle);
      }
      else
      {
         return stack.popStack(parser, popStyle);
      }
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
     * Pops a mandatory argument.
     * @param parser the TeX parser
     * @param stack the stack or the parser or null
     * @param popStyle the pop style
     * @return the argument
     */
   public static TeXObject popArg(TeXParser parser, TeXObjectList stack, byte popStyle)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNextArg(popStyle);
      }
      else
      {
         return stack.popArg(parser, popStyle);
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
    * Pops an optional argument (delimited with <code>[</code> and <code>]</code>).
    * @param popStyle pop style (use 0 or TeXObjectList.POP_SHORT to retain leading space)
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the argument or null if not present
    */ 
   public static TeXObject popOptArg(byte popStyle,
      TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser == stack || stack == null)
      {
         return parser.popNextArg(popStyle, '[', ']');
      }
      else
      {
         return stack.popArg(parser, popStyle, '[', ']');
      }
   }

   public static TeXObject expandOnce(TeXObject arg, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return parser.expandonce(arg, stack);
   }

   public static TeXObject expandFully(TeXObject arg, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return parser.expandfully(arg, stack);
   }

   /**
    * Pops an argument and then expands it once.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the expanded argument
    */ 
   public static TeXObject popArgExpandOnce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      return expandOnce(arg, parser, stack);
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

      return expandFully(arg, parser, stack);
   }

   /**
    * Pops an optional argument and then (if present) fully expands it.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the fully expanded argument or null if not present
    */ 
   public static TeXObject popOptArgExpandFully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = popOptArg(parser, stack);

      if (arg == null)
      {
         return null;
      }

      return expandFully(arg, parser, stack);
   }

   public static String getControlSequenceValue(String csname, String defValue,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname);
               
      if (cs == null) return defValue;
            
      if (cs instanceof TextualContentCommand)
      {
         return ((TextualContentCommand)cs).getText();
      }

      return parser.expandToString(cs, stack);
   }

   public static Numerical toNumerical(TeXObject obj, 
    TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      if (parser.isStack(obj) && ((TeXObjectList)obj).size() == 1
           && ((TeXObjectList)obj).firstElement() instanceof Numerical)
      {
         return (Numerical)((TeXObjectList)obj).firstElement();
      }

      String str = parser.expandToString(obj, stack).trim();

      return new UserNumber(parser, str);
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

   /**
    * Pops an optional argument that should be a dimension.
    * @param parser the TeX parser
    * @param stack the stack or the parser or null
    * @return the dimension or null if no optional argument
    * @throws TeXSyntaxException if the argument isn't a dimension
    */ 
   public static TeXDimension popOptDimensionArg(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = popOptArgExpandFully(parser, stack);

      if (obj == null)
      {
         return null;
      }

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

   public static boolean isControlSequence(TeXObject obj, String... csnames)
   {
      if (obj instanceof ControlSequence)
      {
         ControlSequence cs = (ControlSequence)obj;

         for (String name : csnames)
         {
            if (cs.getName().equals(name))
            {
               return true;
            }
         }
      }

      return false;
   }

   public static ControlSequence popControlSequence(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      if (arg instanceof ControlSequence)
      {
         return (ControlSequence)arg;
      }

      if (parser.isStack(arg))
      {
         if (stack == null)
         {
            parser.push(arg, true);
         }
         else
         {
            stack.push(arg, true);
         }

         arg = popArg(parser, stack);

         if (arg instanceof ControlSequence)
         {
            return (ControlSequence)arg;
         }
      }

      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_CS_EXPECTED,
         arg.format(), arg.getClass().getSimpleName());
   }

   public static TeXObjectList toList(TeXObject arg, TeXParser parser)
   {
      if (parser.isStack(arg))
      {
         return (TeXObjectList)arg;
      }

      return createStack(parser, arg);
   }

   public static CsvList toCsvList(TeXObject arg, TeXParser parser)
     throws IOException
   {
      if (arg instanceof CsvList)
      {
         return (CsvList)arg;
      }

      if (parser.isStack(arg))
      {
         TeXObjectList list = (TeXObjectList)arg;

         if (list.size() == 1 && list.firstElement() instanceof CsvList)
         {
            return (CsvList)list.firstElement();
         }
      }

      return CsvList.getList(parser, arg);
   }

   public static CsvList popCsvList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      return toCsvList(arg, parser);
   }

   public static CsvList popOptCsvList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popOptArg(parser, stack);

      if (arg == null)
      {
         return null;
      }

      return toCsvList(arg, parser);
   }

   public static KeyValList toKeyValList(TeXObject arg, TeXParser parser)
     throws IOException
   {
      if (arg instanceof KeyValList)
      {
         return (KeyValList)arg;
      }

      if (arg instanceof CsvList)
      {
         return ((CsvList)arg).toKeyValList(parser);
      }

      if (parser.isStack(arg))
      {
         TeXObjectList list = (TeXObjectList)arg;

         if (list.size() == 1)
         {
            TeXObject firstElem = list.firstElement();

            if (firstElem instanceof KeyValList)
            {
               return (KeyValList)list.firstElement();
            }
            else if (firstElem instanceof CsvList)
            {
               return ((CsvList)firstElem).toKeyValList(parser);
            }
         }
      }

      return KeyValList.getList(parser, arg);
   }

   public static KeyValList popKeyValList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      return toKeyValList(arg, parser);
   }

   public static KeyValList popOptKeyValList(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popOptArg(parser, stack);

      if (arg == null)
      {
         return null;
      }

      return toKeyValList(arg, parser);
   }

   public static boolean isTrue(String csname, TeXParser parser)
   {
      TeXBoolean bool = toBoolean(csname, parser);

      return bool != null && bool.booleanValue();
   }

   public static boolean isFalse(String csname, TeXParser parser)
   {
      TeXBoolean bool = toBoolean(csname, parser);

      return bool != null && !bool.booleanValue();
   }

   public static TeXBoolean toBoolean(String csname, TeXParser parser)
   {
      return toBoolean(parser.getControlSequence(csname), parser);
   }

   public static TeXBoolean toBoolean(TeXObject object, TeXParser parser)
   {
      if (object == null)
      {
         return null;
      }

      if (object instanceof TeXBoolean)
      {
         return (TeXBoolean)object;
      }

      if (object instanceof AssignedControlSequence)
      {
         TeXObject underlying = ((AssignedControlSequence)object).getBaseUnderlying();

         if (underlying instanceof TeXBoolean)
         {
            return (TeXBoolean)underlying;
         }
      }

      return null;
   }

   public static int toInt(TeXObject object, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (object instanceof TeXNumber)
      {
         return ((TeXNumber)object).getValue();
      }

      if (object instanceof AssignedControlSequence)
      {
         TeXObject underlying = ((AssignedControlSequence)object).getBaseUnderlying();

         if (underlying instanceof TeXNumber)
         {
            return ((TeXNumber)underlying).getValue();
         }
      }

      String str = parser.expandToString(object, stack);

      try
      {
         return Integer.parseInt(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public static float toFloat(TeXObject object, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (object instanceof TeXNumber)
      {
         return (float)((TeXNumber)object).doubleValue();
      }

      if (object instanceof AssignedControlSequence)
      {
         TeXObject underlying = ((AssignedControlSequence)object).getBaseUnderlying();

         if (underlying instanceof TeXNumber)
         {
            return (float)((TeXNumber)underlying).getValue();
         }
      }

      String str = parser.expandToString(object, stack);

      try
      {
         return Float.parseFloat(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   public static double toDouble(TeXObject object, TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (object instanceof TeXNumber)
      {
         return ((TeXNumber)object).doubleValue();
      }

      if (object instanceof AssignedControlSequence)
      {
         TeXObject underlying = ((AssignedControlSequence)object).getBaseUnderlying();

         if (underlying instanceof TeXNumber)
         {
            return (double)((TeXNumber)underlying).getValue();
         }
      }

      String str = parser.expandToString(object, stack);

      try
      {
         return Double.parseDouble(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(e, parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   /**
    * Creates a stack containing the given objects. 
    */ 
   public static TeXObjectList createStack(TeXParser parser,
     TeXObject... objects)
   {
      return createStack(parser.getListener(), objects);
   }

   /**
    * Creates a stack containing the given objects. 
    */ 
   public static TeXObjectList createStack(TeXParserListener listener,
     TeXObject... objects)
   {
      TeXObjectList stack = listener.createStack();

      for (TeXObject obj : objects)
      {
         stack.add(obj);
      }

      return stack;
   }

   /**
    * Creates a group containing the given objects. 
    */ 
   public static Group createGroup(TeXParser parser,
     TeXObject... objects)
   {
      return createGroup(parser.getListener(), objects);
   }

   /**
    * Creates a group containing the given objects. 
    */ 
   public static Group createGroup(TeXParserListener listener,
     TeXObject... objects)
   {
      Group grp = listener.createGroup();

      for (TeXObject obj : objects)
      {
         grp.add(obj);
      }

      return grp;
   }

   public static TeXObjectList removeGroup(TeXObjectList list)
   {
      if (list instanceof Group && !(list instanceof MathGroup))
      {
         list = ((Group)list).toList();
      }
      else if (list.size() == 1)
      {
         TeXObject elem = list.firstElement();

         if (elem instanceof Group && !(elem instanceof MathGroup))
         {
            list = ((Group)elem).toList();
         }
      }

      return list;
   }

   public static void process(TeXObject obj, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (parser == stack || stack == null)
      {
         obj.process(parser);
      }
      else
      {
         obj.process(parser, stack);
      }
   }

   public static TeXObject resolve(TeXObject object, TeXParser parser)
   {
      if (object != null && object instanceof Resolvable)
      {
         object = ((Resolvable)object).resolve(parser);
      }

      return object;
   }

   /**
    * Test if the given object is void. That is, the object doesn't
    * perform any significant action so can be skipped.
    */ 
   public static boolean isVoid(TeXObject object, TeXParser parser)
   {
      if (object == null || object.isEmpty() || object instanceof Relax)
      {
         return true;
      }

      if (object instanceof Resolvable)
      {
         object = ((Resolvable)object).resolve(parser);

         if (object.isEmpty() || object instanceof Relax)
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Tests if the given object consists solely of "letter" or
    * "other" or "space".
    */ 
   public static boolean isString(TeXObject object, TeXParser parser)
   {
      if (object == null)
      {
         return false;
      }

      if (parser.isStack(object))
      {
         TeXObjectList list = (TeXObjectList)object;

         for (TeXObject obj : list)
         {
            if (!isString(obj, parser)) return false;
         }

         return true;
      }
      else if (object instanceof WhiteSpace
            || object instanceof Letter
            || object instanceof Other)
      {
         return true;
      }
      else if (object.isSingleToken())
      {
         int catcode = ((SingleToken)object).getCatCode();

         return (catcode == TeXParser.TYPE_LETTER
               || catcode == TeXParser.TYPE_OTHER
               || catcode == TeXParser.TYPE_SPACE);
      }
      else
      {
         return false;
      }
   }

   public static TeXObject purify(TeXObject object, TeXParser parser,
     TeXObjectList stack)
   throws IOException
   {
      object = expandFully(object, parser, stack);

      if (isString(object, parser))
      {
         return object;
      }

      TeXObjectList list = parser.getListener().createStack();

      if (object instanceof TeXObjectList)
      {
         for (TeXObject o : (TeXObjectList)object)
         {
            if (isString(o, parser))
            {
               list.add(o, true);
            }
         }
      }

      return list;
   }

   public static boolean isBlank(int cp)
   {
      return (cp == ' ' || cp == '\r' || cp == '\n' || cp == '\f' || cp == '\t');
   }

   public static boolean isBlank(String str)
   {
      for (int i = 0; i < str.length(); )
      {
         int cp = str.codePointAt(i);
         i += Character.charCount(cp);

         if (!isBlank(cp))
         {
            return false;
         }
      }

      return true;
   }

   public static boolean isBlank(StringBuilder str)
   {
      for (int i = 0; i < str.length(); )
      {
         int cp = str.codePointAt(i);
         i += Character.charCount(cp);

         if (!isBlank(cp))
         {
            return false;
         }
      }

      return true;
   }
}

