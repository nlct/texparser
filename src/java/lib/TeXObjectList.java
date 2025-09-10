/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
import java.util.Vector;
import java.util.ArrayDeque;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;

public class TeXObjectList extends Vector<TeXObject>
  implements TeXObject,Expandable,CaseChangeable
{
   public TeXObjectList()
   {
      super();
      assignStackID();
   }

   public TeXObjectList(int capacity)
   {
      super(capacity);
      assignStackID();
   }

   private void assignStackID()
   {
      if (isStack())
      {
         stackID = currentStackID;
         currentStackID++;
      }
   }

   public TeXObjectList(TeXParserListener listener, String text)
   {
      this(text != null && text.length() > 0 ? text.length() : 10);

      if (text != null)
      {
         for (int i = 0, n = text.length(); i < n; )
         {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);

            add(listener.getOther(cp));
         }
      }
   }

   public TeXObject expandedPopStack(TeXParser parser)
     throws IOException
   {
      return expandedPopStack(parser, (byte)0);
   }

   public TeXObject expandedPopStack(TeXParser parser, byte popStyle)
     throws IOException
   {
      if (size() == 0)
      {
         return null;
      }

      flatten();

      TeXObject object = popStack(parser, popStyle);

      if (object == null) return null;

      if (isIgnoreLeadingSpace(popStyle))
      {
         popStyle = (byte)(popStyle^POP_IGNORE_LEADING_SPACE);
      }

      object = TeXParserUtils.resolve(object, parser);

      if (object instanceof EndCs || object instanceof InternalQuantity
           || !object.canExpand())
      {
         return object;
      }

      if (object instanceof Group)
      {
         Group group = (Group)object;

         TeXObjectList expanded = group.expandfully(parser, this);
         BgChar bgChar = parser.isBeginGroup(expanded.get(0));

         if (bgChar != null)
         {
            expanded.pop();
            group = bgChar.createGroup(parser);
            expanded.popRemainingGroup(parser, group, popStyle, bgChar);

            if (!expanded.isEmpty())
            {
               push(expanded, true);
            }

            return group;
         }

         push(expanded, true);
         object = popStack(parser, popStyle);
      }

      BgChar bgChar = parser.isBeginGroup(object);

      if (bgChar != null)
      {
         Group group = bgChar.createGroup(parser);
         popRemainingGroup(parser, group, popStyle, bgChar);

         return group;
      }

      if (object instanceof NumericExpansion)
      {
         return ((NumericExpansion)object).expandToNumber(parser, this);
      }

      if (!(object instanceof Expandable))
      {
         return object;
      }

      TeXObjectList expanded = ((Expandable)object).expandfully(parser, this);

      if (expanded == null)
      {
         return object;
      }

      push(expanded, true);

      object = popStack(parser, popStyle);

      object = TeXParserUtils.resolve(object, parser);

      return object;
   }

   public TeXObject popStack(TeXParser parser)
     throws IOException
   {
      return popStack(parser, (byte)0);
   }

   public TeXObject popStack(TeXParser parser, byte popStyle)
     throws IOException
   {
      boolean skipIgnoreables = !isRetainIgnoreables(popStyle);
      boolean skipLeadingWhiteSpace = isIgnoreLeadingSpace(popStyle);

      if (skipIgnoreables && skipLeadingWhiteSpace)
      {
         while (size() > 0)
         {
            TeXObject obj = get(0);

            if (!((obj instanceof Ignoreable) || (obj instanceof WhiteSpace)))
            {
               break;
            }

            pop();
         }
      }
      else if (skipIgnoreables)
      {
         while (size() > 0 && (get(0) instanceof Ignoreable))
         {
            pop();
         }
      }
      else if (skipLeadingWhiteSpace)
      {
         while (size() > 0 && (get(0) instanceof WhiteSpace))
         {
            pop();
         }
      }

      if (size() == 0)
      {
         return null;
      }

      if (isIgnoreLeadingSpace(popStyle))
      {
         popStyle = (byte)(popStyle^POP_IGNORE_LEADING_SPACE);
      }

      TeXObject obj = pop();

      if (isShort(popStyle) && obj.isPar())
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_PAR_BEFORE_EG);
      }

      BgChar bgChar = null;

      if (parser != null)
      {
         bgChar = parser.isBeginGroup(obj);
      }

      if (bgChar != null)
      {
         Group group = bgChar.createGroup(parser);
         popRemainingGroup(parser, group, popStyle, bgChar);

         return group;
      }

      return obj;
   }

   public TeXObject popToken()
     throws IOException
   {
      return popToken((byte)0);
   }

   public TeXObject popToken(byte popStyle)
     throws IOException
   {
      boolean retainIgnoreables = isRetainIgnoreables(popStyle);
      boolean skipWhiteSpace = isIgnoreLeadingSpace(popStyle);

      if (!retainIgnoreables && skipWhiteSpace)
      {
         while (size() > 0)
         {
            TeXObject obj = get(0);

            if (!((obj instanceof Ignoreable) || (obj instanceof WhiteSpace)))
            {
               break;
            }

            pop();
         }
      }
      else if (skipWhiteSpace)
      {
         while (size() > 0)
         {
            TeXObject obj = get(0);

            if (!(obj instanceof WhiteSpace
               || obj instanceof SkippedSpaces
               || obj instanceof SkippedEols))
            {
               break;
            }

            pop();
         }
      }
      else if (!retainIgnoreables)
      {
         while (size() > 0 && (get(0) instanceof Ignoreable))
         {
            pop();
         }
      }

      if (size() == 0)
      {
         return null;
      }

      return pop();
   }

   public TeXObject pop()
     throws IOException
   {
      if (isEmpty())
      {
         return null;
      }

      return remove(0);
   }

   public void popLeadingWhiteSpace()
   {
      if (isEmpty())
      {
         return;
      }

      TeXObject obj = get(0);

      if (obj instanceof Ignoreable || obj instanceof WhiteSpace)
      {
         remove(0);
         popLeadingWhiteSpace();
      }
   }

   public TeXObjectList popToGroup(TeXParser parser, byte popStyle)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      while (true)
      {
         if (size() == 0)
         {
            throw new TeXSyntaxException(parser,
              TeXSyntaxException.ERROR_SYNTAX,
              toString(parser));
         }

         TeXObject obj = firstElement();

         BgChar bgChar = parser.isBeginGroup(obj);

         if (obj instanceof Group || bgChar != null)
         {
            break;
         }

         obj = pop();

         if (!isRetainIgnoreables(popStyle) && obj instanceof Ignoreable)
         {
            parser.getListener().skipping((Ignoreable)obj);
         }
         else
         {
            list.add(obj);
         }
      }

      return list;
   }

   public boolean popCsMarker(TeXParser parser, String name)
    throws IOException
   {
      return popCsMarker(parser, name, (byte)0);
   }

   public boolean popCsMarker(TeXParser parser, String name, byte popStyle)
    throws IOException
   {
      TeXObject token = popToken(popStyle);

      if (token == null)
      {
         return false;
      }

      if (!(token instanceof ControlSequence && 
             ((ControlSequence)token).getName().equals(name)))
      {
         throw new TeXSyntaxException(parser, 
            TeXSyntaxException.ERROR_NOT_FOUND, 
            String.format("%s%s", 
              new String(Character.toChars(parser.getEscChar())), name));
      }

      return true;
   }

   public TeXObjectList popToCsMarker(TeXParser parser, String name)
    throws IOException
   {
      return popToCsMarker(parser, name, (byte)0);
   }

   public TeXObjectList popToCsMarker(TeXParser parser,
       String name, byte popStyle)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObject token = popToken(popStyle);

      if (isIgnoreLeadingSpace(popStyle))
      {
         popStyle = (byte)(popStyle^POP_IGNORE_LEADING_SPACE);
      }

      while (token != null)
      {
         if ((token instanceof ControlSequence && 
             ((ControlSequence)token).getName().equals(name)))
         {
            return list;
         }

         list.add(token);
         token = popToken(popStyle);
      }

      throw new TeXSyntaxException(parser, 
         TeXSyntaxException.ERROR_NOT_FOUND, 
         String.format("%s%s", 
          new String(Character.toChars(parser.getEscChar())), name));
   }

   public String popWord(TeXParser parser)
    throws IOException
   {
      StringBuilder builder = new StringBuilder();
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      while (!isEmpty())
      {
         TeXObject obj = peekStack(popStyle);

         if (obj instanceof CharObject)
         {
            int cp = ((CharObject)obj).getCharCode();

            if (Character.isAlphabetic(cp))
            {
               builder.appendCodePoint(cp);
               popStack(parser, popStyle);
            }
            else
            {
               break;
            }
         }
         else
         {
            break;
         }

         popStyle = POP_SHORT;
      }

      return builder.toString();
   }

   public TeXUnit popUnit(TeXParser parser)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, 
       (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE));

      if (object == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      if (object instanceof CharObject)
      {
         int c1 = ((CharObject)object).getCharCode();

         TeXObject nextObj = expandedPopStack(parser);

         if (nextObj == null || !(nextObj instanceof CharObject))
         {
            push(object);

            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_MISSING_UNIT);
         }

         int c2 = ((CharObject)nextObj).getCharCode();

         try
         {
            return parser.getListener().createUnit(String.format("%s%s",
             new String(Character.toChars(c1)), 
             new String(Character.toChars(c2))));
         }
         catch (TeXSyntaxException e)
         {
            if (c1 == 'f' && c2 == 'i')
            {
               TeXObject object3 = expandedPopStack(parser);

               if (object3 == null
               || !(object3 instanceof CharObject)
               || (((CharObject)object3).getCharCode() != 'l'))
               {
                  push(object3);
               }
               else
               {
                  TeXObject object4 = expandedPopStack(parser);

                  if (object4 == null
                  || !(object4 instanceof CharObject)
                  || ((CharObject)object4).getCharCode() != 'l')
                  {
                     push(object4);
                     return TeXUnit.FIL;
                  }

                  TeXObject object5 = expandedPopStack(parser);

                  if (object5 == null
                  || (object5 instanceof CharObject)
                  || ((CharObject)object5).getCharCode() != 'l')
                  {
                     push(object5);
                     return TeXUnit.FILL;
                  }

                  return TeXUnit.FILLL;
               }
            }
         }

         push(nextObj);
         push(object);

         throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      push(object);

      throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_MISSING_UNIT);
   }

   public InternalQuantity popInternalQuantity(TeXParser parser)
     throws IOException
   {
      TeXObject object = popToken(POP_IGNORE_LEADING_SPACE);

      if (object == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_INTERNAL_QUANTITY_EXPECTED);
      }

      object = TeXParserUtils.resolve(object, parser);

      if (object instanceof InternalQuantity)
      {
         return (InternalQuantity)object;
      }

      if (object instanceof Expandable && object.canExpand())
      {
         TeXObjectList expanded;

         if (this == parser)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser,this);
         }

         if (expanded != null)
         {
            addAll(0, expanded);
            object = popToken(POP_IGNORE_LEADING_SPACE);

            if (object instanceof InternalQuantity)
            {
               return (InternalQuantity)object;
            }
         }
      }

      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_INTERNAL_QUANTITY_EXPECTED_BUT_FOUND,
          object.toString(parser));
   }

   public Register popRegister(TeXParser parser)
     throws IOException
   {
      TeXObject object = popToken(POP_IGNORE_LEADING_SPACE);

      if (object == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_EXPECTED);
      }

      object = TeXParserUtils.resolve(object, parser);

      if (object instanceof Register)
      {
         return (Register)object;
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded;

         if (this == parser)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser,this);
         }

         if (expanded != null)
         {
            addAll(0, expanded);
            object = popToken(POP_IGNORE_LEADING_SPACE);

            if (object instanceof Register)
            {
               return (Register)object;
            }
         }
      }

      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_REGISTER_EXPECTED_BUT_FOUND,
          object.toString(parser));
   }

   public Numerical popNumerical(TeXParser parser)
   throws IOException
   {
      TeXObject object = peekStack(POP_IGNORE_LEADING_SPACE);

      if (object instanceof CharObject)
      {
         int codePoint = ((CharObject)object).getCharCode();

         if (codePoint == '"' || codePoint == '\'' || codePoint == '`'
             || Character.isDigit(codePoint)
             || codePoint == '-' || codePoint == '+')
         {
            return popNumber(parser, object);
         }
      }

      if (object instanceof Numerical)
      {
         return (Numerical)popStack(parser, POP_IGNORE_LEADING_SPACE);
      }

      object = expandedPopStack(parser, POP_SHORT);

      if (object instanceof NumericRegister)
      {
         return (NumericRegister)object;
      }

      if (object instanceof TeXDimension)
      {
         return (TeXDimension)object;
      }

      push(object);

      return popNumber(parser);
   }

   public Numerical popNumExpr(TeXParser parser)
    throws IOException
   {
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      TeXObject object = popArg(parser, popStyle, '(', ')', true);
      Numerical num = null;

      if (object != null)
      {
         if (parser.isStack(object))
         {
            object = ((TeXObjectList)object).popNumExpr(parser);
         }

         if (object instanceof Numerical)
         {
            num = (Numerical)object;
         }
         else
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
         }
      }

      if (num == null)
      {
         num = popNumerical(parser);
      }

      object = popStack(parser, popStyle);

      if (object == null || !(object instanceof CharObject))
      {
         if (num == null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_NUMBER_EXPECTED,
                object == null ? "" : object.toString(parser));
         }
         else
         {
            if (!TeXParserUtils.isControlSequence(object, "relax"))
            {
               push(object);
            }

            return num;
         }
      }

      int cp = ((CharObject)object).getCharCode();
      Numerical nextNum = null;
      UserNumber result = new UserNumber(num.number(parser));

      if (cp == '+' || cp == '-')
      {
         object = peekStack(popStyle);

         if ((object instanceof CharObject)
             && ((CharObject)object).getCharCode() == '(')
         {
            nextNum = popNumExpr(parser);
         }
         else
         {
            nextNum = popNumerical(parser);
         }

         object = peekStack(popStyle);

         if (object instanceof CharObject)
         {
            int nextCp = ((CharObject)object).getCharCode();

            if (nextCp == '*' || nextCp == '/')
            {
               popStack(parser, popStyle);
               Numerical factor = popNumericFactor(parser);

               if (nextCp == '*')
               {
                  nextNum = new UserNumber(
                    nextNum.number(parser)
                  * factor.number(parser));
               }
               else
               {
                  nextNum = new UserNumber(
                    nextNum.number(parser)
                  / factor.number(parser));
               }
            }
         }

         if (cp == '+')
         {
            result.advance(parser, nextNum);
         }
         else
         {
            result.setValue(
              result.number(parser)
            - nextNum.number(parser));
         }
      }
      else if (cp == '*')
      {
         Numerical factor = popNumericFactor(parser);

         result.multiply(factor.number(parser));
      }
      else if (cp == '/')
      {
         Numerical factor = popNumericFactor(parser);

         result.divide(factor.number(parser));
      }
      else
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_NUMBER_EXPECTED,
             object == null ? "" : object.toString(parser));
      }

      return result;
   }

   protected Numerical popNumericFactor(TeXParser parser)
    throws IOException
   {
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      TeXObject object = popArg(parser, popStyle, '(', ')', true);
      Numerical num = null;

      if (object != null)
      {
         if (parser.isStack(object))
         {
            object = ((TeXObjectList)object).popNumExpr(parser);
         }

         if (object instanceof Numerical)
         {
            num = (Numerical)object;
         }
         else
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
         }
      }

      if (num == null)
      {
         num = popNumerical(parser);
      }

      object = peekStack(popStyle);

      if (object == null || !(object instanceof CharObject))
      {
         if (num == null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_NUMBER_EXPECTED,
                object == null ? "" : object.toString(parser));
         }
         else
         {
            return num;
         }
      }

      int cp = ((CharObject)object).getCharCode();

      if (cp == '*' || cp == '/')
      {
         popStack(parser, popStyle);

         UserNumber result = new UserNumber(num.number(parser));
         Numerical factor = popNumericFactor(parser);

         if (cp == '*')
         {
            result.multiply(factor.number(parser));
         }
         else
         {
            result.divide(factor.number(parser));
         }

         return result;
      }
      else
      {
         return num;
      }
   }

   public TeXDimension popDimExpr(TeXParser parser)
    throws IOException
   {
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      TeXObject object = popArg(parser, popStyle, '(', ')', true);
      TeXDimension dim = null;

      if (object != null)
      {
         if (parser.isStack(object))
         {
            object = ((TeXObjectList)object).popDimExpr(parser);
         }

         if (object instanceof TeXDimension)
         {
            dim = (TeXDimension)object;
         }
         else
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_DIMEN_EXPECTED, object.toString(parser));
         }
      }

      if (dim == null)
      {
         dim = popDimension(parser);
      }

      object = popStack(parser, popStyle);

      if (object == null || !(object instanceof CharObject))
      {
         if (dim == null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_DIMEN_EXPECTED,
                object == null ? "" : object.toString(parser));
         }
         else
         {
            if (!TeXParserUtils.isControlSequence(object, "relax"))
            {
               push(object);
            }

            return dim;
         }
      }

      int cp = ((CharObject)object).getCharCode();
      TeXDimension nextDim = null;
      UserDimension result = new UserDimension(dim);

      if (cp == '+' || cp == '-')
      {
         object = peekStack(popStyle);

         if ((object instanceof CharObject)
             && ((CharObject)object).getCharCode() == '(')
         {
            nextDim = popDimExpr(parser);
         }
         else
         {
            nextDim = popDimension(parser);
         }

         object = peekStack(popStyle);

         if (object instanceof CharObject)
         {
            int nextCp = ((CharObject)object).getCharCode();

            if (nextCp == '*' || nextCp == '/')
            {
               popStack(parser, popStyle);
               TeXDimension factor = popDimenFactor(parser);

               if (nextCp == '*')
               {
                  nextDim = new UserDimension(
                    nextDim.getValue()
                     * factor.getUnit().toUnit(parser,
                         factor.getValue(), nextDim.getUnit()),
                    nextDim.getUnit());
               }
               else
               {
                  nextDim = new UserDimension(
                    nextDim.getValue()
                     / factor.getUnit().toUnit(parser,
                         factor.getValue(), nextDim.getUnit()),
                    nextDim.getUnit());
               }
            }
         }

         if (cp == '+')
         {
            result.advance(parser, nextDim);
         }
         else
         {
            TeXUnit unit = nextDim.getUnit();

            result.setValue(
              result.getValue()
                - unit.toUnit(parser, nextDim.getValue(), result.getUnit()),
              result.getUnit());
         }
      }
      else if (cp == '*')
      {
         TeXDimension factor = popDimenFactor(parser);

         result.setValue(
           result.getValue()
            * factor.getUnit().toUnit(parser,
                factor.getValue(), result.getUnit()),
           result.getUnit());
      }
      else if (cp == '/')
      {
         TeXDimension factor = popDimenFactor(parser);

         result.setValue(
           result.getValue()
            / factor.getUnit().toUnit(parser,
                factor.getValue(), result.getUnit()),
           result.getUnit());
      }
      else
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_DIMEN_EXPECTED,
             object == null ? "" : object.toString(parser));
      }

      return result;
   }

   protected TeXDimension popDimenFactor(TeXParser parser)
    throws IOException
   {
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      TeXObject object = popArg(parser, popStyle, '(', ')', true);
      TeXDimension dim = null;

      if (object != null)
      {
         if (parser.isStack(object))
         {
            object = ((TeXObjectList)object).popDimExpr(parser);
         }

         if (object instanceof TeXDimension)
         {
            dim = (TeXDimension)object;
         }
         else
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_DIMEN_EXPECTED, object.toString(parser));
         }
      }

      if (dim == null)
      {
         dim = popDimension(parser);
      }

      object = peekStack(popStyle);

      if (object == null || !(object instanceof CharObject))
      {
         if (dim == null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_DIMEN_EXPECTED,
                object == null ? "" : object.toString(parser));
         }
         else
         {
            return dim;
         }
      }

      int cp = ((CharObject)object).getCharCode();

      if (cp == '*' || cp == '/')
      {
         popStack(parser, popStyle);

         UserDimension result = new UserDimension(dim);
         TeXDimension factor = popDimenFactor(parser);

         if (cp == '*')
         {
            result.setValue(
              result.getValue()
               * factor.getUnit().toUnit(parser,
                   factor.getValue(), result.getUnit()),
              result.getUnit());
         }
         else
         {
            result.setValue(
              result.getValue()
               / factor.getUnit().toUnit(parser,
                   factor.getValue(), result.getUnit()),
              result.getUnit());
         }

         return result;
      }
      else
      {
         return dim;
      }
   }

   public TeXDimension popDimension(TeXParser parser)
    throws IOException
   {
      return popDimension(parser, true);
   }

   public TeXDimension popDimension(TeXParser parser, boolean glue)
    throws IOException
   {
      byte popStyle = (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE);

      TeXObject object = popStack(parser, popStyle);

      if (parser.isStack(object) && ((TeXObjectList)object).size() == 1)
      {
         object = ((TeXObjectList)object).firstElement();
      }

      object = TeXParserUtils.resolve(object, parser);

      if (object instanceof TeXDimension)
      {
         return (TeXDimension)object;
      }

      int sign = 1;

      if (object instanceof CharObject)
      {
         int cp = ((CharObject)object).getCharCode();

         if (cp == '-' || cp == '+')
         {
            if (cp == '-')
            {
               sign = -1;
            }

            object = popStack(parser, popStyle);

            if (parser.isStack(object) && ((TeXObjectList)object).size() == 1)
            {
               object = ((TeXObjectList)object).firstElement();
            }

            object = TeXParserUtils.resolve(object, parser);

            if (object instanceof TeXDimension)
            {
               if (sign == 1)
               {
                  return (TeXDimension)object;
               }
               else
               {
                  TeXDimension dim = (TeXDimension)object;
                  return new UserDimension(-dim.getValue(), dim.getUnit());
               }
            }
         }
      }

      push(object);

      Float value = popFloat(parser);

      object = expandedPopStack(parser, popStyle);

      if (object instanceof DimenRegister)
      {
         TeXDimension dimen = new TeXGlue(parser, (DimenRegister)object);
         dimen.multiply(sign * value.floatValue());
         return dimen;
      }

      push(object);

      TeXUnit unit = popUnit(parser);

      TeXDimension dimen = new UserDimension(sign * value.floatValue(), unit);

      if (!glue)
      {
         return dimen;
      }

      TeXDimension stretch = null;

      TeXDimension shrink = null;

      String word = popWord(parser);

      if (word.equals("plus"))
      {
         stretch = popDimension(parser, false);

         word = popWord(parser);

         if (word.equals("minus"))
         {
            shrink = popDimension(parser, false);
         }
         else
         {
            addAll(0, parser.getListener().createString(word));
         }
      }
      else if (word.equals("minus"))
      {
         shrink = popDimension(parser, false);

         word = popWord(parser);

         if (word.equals("plus"))
         {
            stretch = popDimension(parser, false);
         }
         else
         {
            addAll(0, parser.getListener().createString(word));
         }
      }
      else
      {
         addAll(0, parser.getListener().createString(word));
      }

      if (shrink == null && stretch == null)
      {
         return dimen;
      }
      else
      {
         return new TeXGlue(parser, dimen, stretch, shrink);
      }

   }

   public Float popFloat(TeXParser parser)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser);

      StringBuilder builder = new StringBuilder();
      
      popFloat(parser, object, builder);

      String str = builder.toString();

      try
      {
         return Float.valueOf(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser,
                  TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }
   }

   // object should be fully expanded
   protected void popFloat(TeXParser parser, TeXObject object, 
      StringBuilder builder)
   throws IOException
   {
      if (object == null) return;

      String str = object.toString(parser);

      try
      {
         Float.parseFloat(builder.toString()+str+"0");
      }
      catch (NumberFormatException e)
      {
         push(object);
         return;
      }

      builder.append(str);

      popFloat(parser, expandedPopStack(parser), builder);
   }

   public TeXNumber popNumber(TeXParser parser)
    throws IOException
   {
      TeXObject object = peekStack(POP_IGNORE_LEADING_SPACE);

      return popNumber(parser, object);
   }

   private TeXNumber popNumber(TeXParser parser, TeXObject object)
    throws IOException
   {
      int base = 10;

      if (object instanceof CharObject)
      {
         int codePoint = ((CharObject)object).getCharCode();

         if (codePoint == '"')
         {
            popStack(parser, POP_IGNORE_LEADING_SPACE);
            base = 16;
         }
         else if (codePoint == '\'')
         {
            popStack(parser, POP_IGNORE_LEADING_SPACE);
            base = 8;
         }
         else if (codePoint == '`')
         {
            popStack(parser, POP_IGNORE_LEADING_SPACE);

            TeXObject nextObj = peekStack();

            if (nextObj instanceof Macro)
            {
               popStack(parser);

               String name = nextObj.toString(parser);

               codePoint = name.codePointAt(0);
               int charCount = Character.charCount(codePoint);

               if (codePoint == parser.getEscChar())
               {
                  name = name.substring(charCount);

                  codePoint = name.codePointAt(0);
                  charCount = Character.charCount(codePoint);
               }

               int n = name.length();

               if (charCount < n)
               {
                  // push trailing content back as letters

                  TeXObjectList substack = new TeXObjectList(n-1);

                  for (int i = charCount; i < n; )
                  {
                     int cp = name.codePointAt(i);
                     i += Character.charCount(cp);

                     substack.add(parser.getListener().getLetter(cp));
                  }

                  addAll(0, substack);
               }
               else
               {
                  // skip trailing spaces

                  popLeadingWhiteSpace();
               }

               return new UserNumber(codePoint);
            }
            else if (nextObj instanceof CharObject)
            {
               codePoint = ((CharObject)nextObj).getCharCode();

               popStack(parser, POP_IGNORE_LEADING_SPACE);

               // skip trailing spaces

               popLeadingWhiteSpace();

               return new UserNumber(codePoint);
            }
            else
            {
               String str = nextObj.toString(parser);

               codePoint = str.codePointAt(0);

               if (Character.charCount(codePoint) != str.length())
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_IMPROPER_ALPHABETIC_CONSTANT,
                    str);
               }

               return new UserNumber(codePoint);
            }
         }
      }

      return popNumber(parser, base);
   }

   public TeXNumber popNumber(TeXParser parser, int base)
     throws IOException
   {
      popLeadingWhiteSpace();
      TeXObject object = expandedPopStack(parser);

      if (object instanceof TeXNumber)
      {
         return (TeXNumber)object;
      }

      if (object instanceof Group)
      {
         return ((Group)object).toList().popNumber(parser);
      }

      if (object instanceof ControlSequence)
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
      }

      StringBuilder builder = new StringBuilder();

      if (object instanceof CharObject && 
          (((CharObject)object).getCharCode() == '+'
            || ((CharObject)object).getCharCode() == '-'))
      {
         builder.appendCodePoint(((CharObject)object).getCharCode());
         object = expandedPopStack(parser);
      }
      
      popNumber(parser, object, builder, base);

      // skip trailing spaces

      popLeadingWhiteSpace();

      if (builder.length() == 0)
      {
         throw new TeXSyntaxException(parser,
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, 
          object == null ? "" : object.toString(parser));
      }

      return new UserNumber(parser, builder.toString(), base);
   }

   // object should be fully expanded
   protected void popNumber(TeXParser parser, TeXObject object,
     StringBuilder builder, int base)
   throws IOException
   {
      if (object == null) return;

      String str;

      if (object instanceof CharObject)
      {
         // don't allow font encoding etc to convert the character
         str = new String(Character.toChars(((CharObject)object).getCharCode()));
      }
      else
      {
         str = object.toString(parser);
      }

      try
      {
         Integer.parseInt(builder.toString()+str, base);
      }
      catch (NumberFormatException e)
      {
         push(object);
         return;
      }

      builder.append(str);

      popNumber(parser, expandedPopStack(parser), builder, base);
   }

   public ControlSequence popControlSequence(TeXParser parser)
    throws IOException
   {
      TeXObject obj = popArg(parser);

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;

         obj = list.popToken();

         if (obj == null || list.peekStack() != null)
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_CS_EXPECTED,
                obj == null ? list.toString(parser) :
                 String.format("%s%s", obj.toString(parser),
                 list.toString(parser)),
                obj.getClass().getSimpleName());
         }
      }

      if (!(obj instanceof ControlSequence))
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_CS_EXPECTED, obj.toString(parser),
                obj.getClass().getSimpleName());
      }

      return (ControlSequence)obj;
   }

   // TODO separate TeXObjectList and Group into sibling classes
   // (requires some refactoring).
   public boolean isStack()
   {
      return true;
   }

   public boolean equals(String text)
   {
      if (!(this instanceof Group))
      {
         int n = text.length();
         int i = 0;

         for (int j = 0; j < size(); j++)
         {
            TeXObject object = get(j);

            if (object instanceof Ignoreable) continue;

            if (!(object instanceof CharObject)) return false;

            if (i >= n) return false;

            int codepoint = ((CharObject)object).getCharCode();

            if (codepoint != text.codePointAt(i)) return false;

            i += Character.charCount(codepoint);
         }

         if (i == n) return true;
      }

      return false;
   }

   public boolean equalsMatchCatCode(TeXObject other)
   {
      if (other == null || !other.getClass().equals(getClass()))
      {
         return false;
      }

      TeXObjectList otherList = (TeXObjectList)other;

      if (otherList.size() != size())
      {
         return false;
      }

      for (int i = 0; i < size(); i++)
      {
         TeXObject obj1 = get(i);
         TeXObject obj2 = otherList.get(i);

         if (obj1 instanceof TeXObjectList && obj2 instanceof TeXObjectList)
         {
            if (!((TeXObjectList)obj1).equalsMatchCatCode((TeXObjectList)obj2))
            {
               return false;
            }
         }
         else if (
            ! (
               obj1.isSingleToken()
            && obj2.isSingleToken()
            && ((SingleToken)obj1).getCatCode()
                   == ((SingleToken)obj2).getCatCode()
            && obj1.equals(obj2)
              )
            )
         {
            return false;
         }
      }

      return true;
   }

   public void push(TeXObject object)
   {
      push(object, false);
   }

   public void push(TeXObject object, boolean flattenStacks)
   {
      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      if (object != null)
      {
         if (flattenStacks && (object instanceof TeXObjectList)
              && ((TeXObjectList)object).isStack())
         {
            TeXObjectList list = (TeXObjectList)object;

            for (int i = list.size()-1; i >= 0; i--)
            {
               push(list.get(i), flattenStacks);
            }
         }
         else
         {
            add(0, object);
         }
      }
   }

   public void add(int index, TeXObject object)
   {
      if (object == null)
      {
         throw new NullPointerException();
      }

      super.add(index, object);
   }

   public boolean add(TeXObject object)
   {
      return add(object, false);
   }

   public boolean add(TeXObject object, boolean flattenStacks)
   {
      if (object == null)
      {
         throw new NullPointerException();
      }

      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add a list to itself");
      }

      if (flattenStacks && (object instanceof TeXObjectList)
              && ((TeXObjectList)object).isStack())
      {
         TeXObjectList list = (TeXObjectList)object;

         boolean changed = false;

         for (int i = 0; i < list.size(); i++)
         {
            changed = add(list.get(i), flattenStacks) || changed;
         }

         return changed;
      }
      else
      {
         return super.add(object);
      }
   }

   public void addTokens(String str, TeXParserListener listener)
   {
      for (int i = 0; i < str.length(); )
      {
         int cp = str.codePointAt(i);

         i += Character.charCount(cp);

         addToken(cp, listener);
      }
   }

   public void addTokens(StringBuilder str, TeXParserListener listener)
   {
      for (int i = 0; i < str.length(); )
      {
         int cp = str.codePointAt(i);

         i += Character.charCount(cp);

         addToken(cp, listener);
      }
   }

   public void addToken(int cp, TeXParserListener listener)
   {
      if (Character.isAlphabetic(cp))
      {
         add(listener.getLetter(cp));
      }
      else
      {
         add(listener.getOther(cp));
      }
   }


   public TeXObject peek()
   {
      return size() == 0 ? null : firstElement();
   }

   public TeXObject peekLast()
   {
      return size() == 0 ? null : lastElement();
   }

   public TeXObject peekStack()
    throws IOException
   {
      return peekStack((byte)0);
   }

   public TeXObject peekStack(byte popStyle)
    throws IOException
   {
      if (size() == 0)
      {
         return null;
      }

      if (isIgnoreLeadingSpace(popStyle))
      {
         for (int i = 0, n = size(); i < n; i++)
         {
            TeXObject obj = get(i);

            if (!((obj instanceof Ignoreable) || (obj instanceof WhiteSpace)))
            {
               return obj;
            }
         }
      }
      else
      {
         for (int i = 0, n = size(); i < n; i++)
         {
            TeXObject obj = get(i);

            if (!(obj instanceof Ignoreable))
            {
               return obj;
            }
         }
      }

      return null;
   }

   public static boolean isShort(byte popStyle)
   {
      return (popStyle & POP_SHORT) == POP_SHORT;
   }

   public static boolean isRetainIgnoreables(byte popStyle)
   {
      return (popStyle & POP_RETAIN_IGNOREABLES) == POP_RETAIN_IGNOREABLES;
   }

   public static boolean isIgnoreLeadingSpace(byte popStyle)
   {
      return (popStyle & POP_IGNORE_LEADING_SPACE) == POP_IGNORE_LEADING_SPACE;
   }

   public static byte getArgPopStyle(boolean isShort)
   {
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      if (isShort)
      {
         popStyle = (byte)(TeXObjectList.POP_SHORT | popStyle);
      }

      return popStyle;
   }

   // Pops an argument off the stack. Removes any top level
   // grouping. Ignore leading white space.
   public TeXObject popArg(TeXParser parser)
    throws IOException
   {
      return popArg(parser, POP_IGNORE_LEADING_SPACE);
   }

   public TeXObject popArg(TeXParser parser, byte popStyle)
    throws IOException
   {
      TeXObject object = popStack(parser, popStyle);

      if (object == null && !(this instanceof TeXParser))
      {
         object = parser.popNextArg(popStyle);
      }

      if (object instanceof Group
       && !(object instanceof MathGroup))
      {
         return ((Group)object).toList();
      }

      return object;
   }

   // Pops delimited argument off the stack. If the stack doesn't
   // start with the specified open delimiter, returns null, so can
   // be used to get an optional argument
   public TeXObject popArg(TeXParser parser, int openDelim, int closeDelim)
     throws IOException
   {
      return popArg(parser, POP_IGNORE_LEADING_SPACE, openDelim, closeDelim);
   }

   public TeXObject popArg(TeXParser parser, byte popStyle, 
     int openDelim, int closeDelim)
   throws IOException
   {
      return popArg(parser, popStyle, openDelim, closeDelim, false);
   }

   public TeXObject popArg(TeXParser parser, byte popStyle, 
     int openDelim, int closeDelim, boolean balanced)
   throws IOException
   {
      boolean skipIgnoreables = !isRetainIgnoreables(popStyle);
      boolean skipLeadingWhiteSpace = isIgnoreLeadingSpace(popStyle);

      TeXObjectList skipped = null;
      TeXObject object = null;

      if (skipIgnoreables && skipLeadingWhiteSpace)
      {
         while (size() > 0)
         {
            object = get(0);

            if (!((object instanceof Ignoreable) || (object instanceof WhiteSpace)))
            {
               break;
            }

            pop();

            if (skipped == null)
            {
               skipped = new TeXObjectList();
            }

            skipped.add(object);
            object = null;
         }
      }
      else if (skipIgnoreables)
      {
         while (size() > 0)
         {
            object = get(0);

            if (!(object instanceof Ignoreable))
            {
               break;
            }

            pop();

            if (skipped == null)
            {
               skipped = new TeXObjectList();
            }

            skipped.add(object);
            object = null;
         }
      }
      else if (skipLeadingWhiteSpace)
      {
         while (size() > 0)
         {
            object = get(0);

            if (!(object instanceof WhiteSpace))
            {
               break;
            }

            pop();

            if (skipped == null)
            {
               skipped = new TeXObjectList();
            }

            skipped.add(object);
            object = null;
         }
      }

      if (size() == 0 || object == null)
      {
         if (skipped != null)
         {
            addAll(skipped);
         }

         return null;
      }

      if (!(object instanceof CharObject)
         || (((CharObject)object).getCharCode() != openDelim))
      {
         if (skipped != null)
         {
            addAll(0, skipped);
         }

         return null;
      }

      int nested = 1;

      if (isIgnoreLeadingSpace(popStyle))
      {
         popStyle = (byte)(popStyle^POP_IGNORE_LEADING_SPACE);
      }

      pop();

      int lineNum = parser.getLineNumber();

      TeXObjectList list = new TeXObjectList();
      boolean isShort = isShort(popStyle);

      while (!isEmpty())
      {
         object = pop();

         if (object == null) break;

         BgChar bgChar = parser.isBeginGroup(object);

         if (object instanceof CharObject)
         {
            if (((CharObject)object).getCharCode() == closeDelim)
            {
               nested--;

               if (!balanced || nested < 1)
               {
                  return list;
               }
            }
            else if (((CharObject)object).getCharCode() == openDelim)
            {
               nested++;
            }
         }
         else if (bgChar != null)
         {
            Group group = parser.getListener().createGroup();
            popRemainingGroup(parser, group, popStyle, bgChar);
            object = group;
         }
         else if (isShort && object.isPar())
         {
            break;
         }

         list.add(object);
      }

      if (lineNum > 0)
      {
         throw new TeXSyntaxException(parser,
                  TeXSyntaxException.ERROR_MISSING_CLOSING_FROM_OPEN,
                  new String(Character.toChars(closeDelim)),
                  new String(Character.toChars(openDelim)),
                  lineNum);
      }
      else
      {
         throw new TeXSyntaxException(parser,
                  TeXSyntaxException.ERROR_MISSING_CLOSING,
                  new String(Character.toChars(closeDelim)));
      }
   }

   public Numerical popNumericalArg(TeXParser parser)
     throws IOException
   {
      return popNumericalArg(parser, false);
   }

   public Numerical popNumericalArg(TeXParser parser, boolean calculate)
     throws IOException
   {
      TeXObject obj = popArg(parser, POP_SHORT);

      if (obj == null) return null;

      if (parser.isStack(obj) && ((TeXObjectList)obj).size() == 1)
      {
         obj = ((TeXObjectList)obj).firstElement();
      }

      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)obj).expandfully(parser, this);

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof TeXObjectList)
      {
         if (calculate)
         {
            return ((TeXObjectList)obj).popNumExpr(parser);
         }
         else
         {
            return ((TeXObjectList)obj).popNumerical(parser);
         }
      }

      return new UserNumber(parser, obj.toString(parser));
   }

   public Numerical popNumericalArg(TeXParser parser, int openDelim, int closeDelim)
     throws IOException
   {
      return popNumericalArg(parser, openDelim, closeDelim, false);
   }

   public Numerical popNumericalArg(TeXParser parser, int openDelim, int closeDelim,
     boolean calculate)
     throws IOException
   {
      TeXObject obj = popArg(parser, POP_SHORT, openDelim, closeDelim);

      if (obj == null) return null;

      if (parser.isStack(obj) && ((TeXObjectList)obj).size() == 1)
      {
         obj = ((TeXObjectList)obj).firstElement();
      }

      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)obj).expandfully(parser, this);

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof TeXObjectList)
      {
         if (calculate)
         {
            return ((TeXObjectList)obj).popNumExpr(parser);
         }
         else
         {
            return ((TeXObjectList)obj).popNumerical(parser);
         }
      }

      return new UserNumber(parser, obj.toString(parser));
   }

   public TeXObjectList toLowerCase(TeXParser parser)
   {
      TeXObjectList list = createList();

      for (TeXObject object : this)
      {
         object = TeXParserUtils.resolve(object, parser);

         if (object instanceof CaseChangeable)
         {
            list.add(((CaseChangeable)object).toLowerCase(parser));
         }
         else if (object instanceof ControlSequence)
         {
            if (object instanceof Primitive
             || object instanceof MathSymbol)
            {
               list.add(object);
            }
            else
            {
               ControlSequence cs = parser.getControlSequence(
                 ((ControlSequence)object).getName().toLowerCase());

               if (cs == null)
               {
                  list.add(object);
               }
               else
               {
                  list.add(cs);
               }
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList toUpperCase(TeXParser parser)
   {
      TeXObjectList list = createList();

      for (TeXObject object : this)
      {
         object = TeXParserUtils.resolve(object, parser);

         if (object instanceof CaseChangeable)
         {
            list.add(((CaseChangeable)object).toUpperCase(parser));
         }
         else if (object instanceof ControlSequence)
         {
            if (object instanceof Primitive
             || object instanceof MathSymbol)
            {
               list.add(object);
            }
            else
            {
               ControlSequence cs = parser.getControlSequence(
                 ((ControlSequence)object).getName().toUpperCase());

               if (cs == null)
               {
                  list.add(object);
               }
               else
               {
                  list.add(cs);
               }
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList createList()
   {
      return new TeXObjectList(capacity());
   }

   public Object clone()
   {
      TeXObjectList list = createList();

      for (TeXObject object : this)
      {
         list.add((TeXObject)object.clone());
      }

      for (Declaration dec : declarations)
      {
         list.declarations.add((Declaration)dec.clone());
      }

      return list;
   }

   protected void flatten()
   {
      for (int i = size()-1; i >= 0; i--)
      {
         TeXObject obj = get(i);

         if (obj instanceof TeXObjectList)
         {
            ((TeXObjectList)obj).flatten();

            if (((TeXObjectList)obj).isStack())
            {
               remove(i);
               addAll(i, (TeXObjectList)obj);
            }
         }
      }
   }

   public void stripIgnoreables()
   {
      for (int i = size()-1; i >= 0; i--)
      {
         TeXObject obj = get(i);

         if (obj instanceof Ignoreable)
         {
            remove(i);
         }
         else if (obj instanceof TeXObjectList)
         {
            ((TeXObjectList)obj).stripIgnoreables();

            if (((TeXObjectList)obj).isStack())
            {
               remove(i);

               if (!obj.isEmpty())
               {
                  addAll(i, (TeXObjectList)obj);
               }
            }
         }
      }
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      if (isEmpty())
      {
         return this;
      }

      flatten();

      TeXObjectList list = new TeXObjectList(size());
      TeXObjectList remaining = (TeXObjectList)clone();

      boolean blocked = false;

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.pop();

         object = TeXParserUtils.resolve(object, parser);

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         TeXObjectList expanded = null;

         if (object instanceof Unexpanded)
         {
            object = remaining.popArg(parser);
         }
         else if (!blocked && object.canExpand() && object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandonce(parser, remaining);
         }

         if (expanded == null)
         {
            list.add(object);
         }
         else
         {
            for (int i = 0; i < expanded.size(); i++)
            {
               object = expanded.get(i);

               object = TeXParserUtils.resolve(object, parser);

               if (object.isExpansionBlocker())
               {
                  list.add(object);

                  for (int j = i+1; j < expanded.size(); j++)
                  {
                     list.add(expanded.get(j));
                  }

                  blocked = true;

                  break;
               }
               else
               {
                  list.add(object);
               }
            }
         }
      }

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      if (isEmpty())
      {
         return this;
      }

      flatten();

      TeXObjectList list = new TeXObjectList(size());
      TeXObjectList remaining = (TeXObjectList)clone();

      boolean blocked = false;

      StackMarker marker = null;
      boolean markerFound = false;

      if (stack != null && stack != parser)
      {
         marker = new StackMarker();
         remaining.add(marker);
         remaining.addAll(stack);
         stack.clear();
      }

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.pop();

         if (object.equals(marker))
         {
            markerFound = true;
            break;
         }

         object = TeXParserUtils.resolve(object, parser);

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         TeXObjectList expanded = null;

         if (object instanceof Unexpanded)
         {
            object = remaining.popArg(parser);
         }
         else if (!blocked && object.canExpand() && object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandonce(parser, remaining);
         }

         if (expanded == null)
         {
            list.add(object);
         }
         else
         {
            for (int i = 0; i < expanded.size(); i++)
            {
               object = expanded.get(i);

               object = TeXParserUtils.resolve(object, parser);

               if (object.equals(marker))
               {
                  for (int j = i+1; j < expanded.size(); j++)
                  {
                     stack.add(expanded.get(j));
                  }

                  markerFound = true;

                  break;
               }
               else if (object.isExpansionBlocker())
               {
                  list.add(object);

                  for (int j = i+1; j < expanded.size(); j++)
                  {
                     list.add(expanded.get(j));
                  }

                  blocked = true;
                  break;
               }
               else
               {
                  list.add(object);
               }
            }
         }
      }

      if (!remaining.isEmpty())
      {
         if (marker != null && !markerFound)
         {
            int idx = remaining.indexOf(marker);

            for (int i = 0; i < idx; i++)
            {
               list.add(remaining.get(i));
            }

            for (int i = idx+1; i < remaining.size(); i++)
            {
               stack.add(remaining.get(i));
            }
         }
         else
         {
            stack.addAll(remaining);
         }
      }

      return list;
   }

   @Override
   public boolean isDataObject()
   {
      return false;
   }

   @Override
   public boolean isSingleToken()
   {
      return false;
   }

   @Override
   public boolean isExpansionBlocker()
   {
      return false;
   }

   @Override
   public boolean canExpand()
   {
      return !isExpanded();
   }

   public boolean isExpanded()
   {
      for (int i = 0; i < size(); i++)
      {
         TeXObject obj = get(i);

         if (obj.canExpand())
         {
            return false;
         }
      }

      return true;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
      {
         parser.logMessage("FULLY EXPANDING: "+toString(parser));
      }

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
      {
         parser.logMessage("FULLY EXPANDING: "+toString());
      }

      TeXObjectList list = new TeXObjectList(size());

      if (isExpanded())
      {
         if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
           || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
         {
            parser.logMessage("ALREADY EXPANDED");
         }

         list.add(this, true);
         clear();
         return list;
      }

      boolean blocked = false;

      TeXObject prevObj = null;

      while (!isEmpty())
      {
         TeXObject object = pop();

         if (object == prevObj)
         {
            list.add(object);
            continue;
         }

         prevObj = object;

         object = TeXParserUtils.resolve(object, parser);

         if (object.isExpansionBlocker())
         {
            blocked = true;

            if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
              || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
            {
               parser.logMessage("EXPANSION BLOCKED AT: "+object);
            }
         }

         if (object instanceof StackMarker || blocked)
         {
            list.add(object);
         }
         else if (object instanceof Ignoreable)
         {// discard
         }
         else if (object instanceof Unexpanded)
         {
            list.add(popArg(parser));
         }
         else if (parser.isStack(object))
         {
            push(object, true);
         }
         else if (object.canExpand() && object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser, this);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (!expanded.isEmpty())
            {
               list.add(expanded, true);
            }
         }
         else
         {
            list.add(object);

            if (object instanceof Macro
              && (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
                || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST)))
            {
               parser.logMessage("CAN'T EXPAND: "+object);
            }
         }
      }

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      TeXObjectList list = new TeXObjectList(size());

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION))
      {
         parser.logMessage("FULLY EXPANDING: "+toString(parser)
            + " SUBSTACK: "+stack.toString(parser));
      }

      if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
      {
         parser.logMessage("FULLY EXPANDING: "+toString()
            + " SUBSTACK: "+stack);
      }

      if (isExpanded())
      {
         if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
           || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
         {
            parser.logMessage("ALREADY EXPANDED");
         }

         list.add(this, true);
         clear();
         return list;
      }

      boolean blocked = false;

      flatten();

      TeXObjectList remaining = (TeXObjectList)clone();

      StackMarker marker = null;

      if (stack != null && stack != parser)
      {
         marker = new StackMarker();
         remaining.add(marker);
         remaining.addAll(stack);
         stack.clear();
      }

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.pop();

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof Ignoreable)
         {
            continue;
         }

         object = TeXParserUtils.resolve(object, parser);

         if (object.isExpansionBlocker())
         {
            blocked = true;

            if (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
              || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST))
            {
               parser.logMessage("EXPANSION BLOCKED AT: "+object);
            }
         }

         TeXObjectList expanded = null;

         if (object instanceof Unexpanded)
         {
            object = remaining.popArg(parser);
         }
         else if (blocked)
         {
            // do nothing
         }
         else if (object.canExpand() && object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandfully(parser, remaining);
         }
         else if (object instanceof Macro
            && (parser.isDebugMode(TeXParser.DEBUG_EXPANSION)
              || parser.isDebugMode(TeXParser.DEBUG_EXPANSION_LIST)))
         {
            parser.logMessage("CAN'T EXPAND: "+object);
         }

         if (expanded == null)
         {
            list.add(object);
         }
         else
         {
            list.add(expanded, true);
         }
      }

      if (!remaining.isEmpty())
      {
         stack.addAll(remaining);
      }

      return list;
   }

   public void process(TeXParser parser)
      throws IOException
   {
      flatten();

      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK))
      {
         parser.logMessage("PROCESSING STACK: "+toString(parser));
      }

      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK_LIST))
      {
         parser.logMessage("PROCESSING STACK: "+toString());
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (parser.isDebugMode(TeXParser.DEBUG_POPPED))
         {
            parser.logMessage("POPPED "+object 
             + " FROM " + getClass().getSimpleName()+"#"+stackID);
         }

         object = TeXParserUtils.resolve(object, parser);

         if (object instanceof Declaration)
         {
            if (parser.isDebugMode(TeXParser.DEBUG_DECL))
            {
               parser.logMessage("PUSHING DECLARATION "+object);
            }

            pushDeclaration((Declaration)object);
         }
         else if (object instanceof EndDeclaration)
         {
            Declaration decl = ((EndDeclaration)object).getDeclaration(parser);

            if (parser.isDebugMode(TeXParser.DEBUG_DECL))
            {
               parser.logMessage("POPPING DECLARATION "+decl);
            }

            popDeclaration(decl);
         }

         if (!(object instanceof Ignoreable))
         {
            if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
            {
               parser.logMessage("PROCESSING "+object);
            }

            object.process(parser, this);
         }
      }
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      flatten();

      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK))
      {
         parser.logMessage("PROCESSING STACK: "+toString(parser)
            + " SUBSTACK: "+stack.toString(parser));

         if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK_LIST))
         {
            parser.logMessage("PROCESSING STACK: "+toString()+" SUBSTACK: "+stack);
         }
      }

      StackMarker marker = null;

      if (stack != parser && stack != null && !stack.isEmpty())
      {
         marker = new StackMarker();
         add(marker);

         addAll(stack);
         stack.clear();
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (parser.isDebugMode(TeXParser.DEBUG_POPPED))
         {
            parser.logMessage("POPPED "+object 
             + " FROM " + getClass().getSimpleName()+"#"+stackID);
         }

         if (object.equals(marker))
         {
            break;
         }

         object = TeXParserUtils.resolve(object, parser);

         if (object instanceof Declaration)
         {
            if (parser.isDebugMode(TeXParser.DEBUG_DECL))
            {
               parser.logMessage("PUSHING DECLARATION "+object);
            }

            pushDeclaration((Declaration)object);
         }
         else if (object instanceof EndDeclaration)
         {
            Declaration decl = ((EndDeclaration)object).getDeclaration(parser);

            if (parser.isDebugMode(TeXParser.DEBUG_DECL))
            {
               parser.logMessage("POPPING DECLARATION "+decl);
            }

            popDeclaration(decl);
         }

         if (!(object instanceof Ignoreable))
         {
            if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
            {
               parser.logMessage("PROCESSING "+object+" STACK: "+toString());
            }

            object.process(parser, this);
         }
      }

      if (!isEmpty())
      {
         stack.addAll(0, this);
         clear();

         if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK))
         {
            parser.logMessage("REMAINING STACK "+stack);
         }
      }
   }

   /**
    * Process up to the given marker and pick up any mid control sequences.
    * For use where the list is the interior of a group.
    * @param parser the parser
    * @param marker the marker
    */
   protected boolean processList(TeXParser parser, StackMarker marker)
    throws IOException
   {
      boolean markerFound = false;

      TeXObjectList before = new TeXObjectList();
      TeXObjectList after = new TeXObjectList();

      MidControlSequence midcs = null;

      for (int i = 0; i < size(); i++)
      {
         TeXObject object = get(i);

         markerFound = object.equals(marker);

         if (markerFound)
         {
            break;
         }

         object = TeXParserUtils.resolve(object, parser);

         if (object == null)
         {
            break;
         }

         if (object instanceof MidControlSequence)
         {
            midcs = (MidControlSequence)object;
            continue;
         }

         if (midcs == null)
         {
            before.add(object);
         }
         else
         {
            after.add(object);
         }
      }

      if (midcs == null)
      {
         before = null;
         after = null;
         markerFound = false;

         while (size() != 0)
         {
            TeXObject object = remove(0);

            if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
            {
               parser.logMessage("PROCESS LIST OBJ: "+object
                + " TERMINATING MARKER: "+marker);
            }

            if (object == null)
            {
               break;
            }

            markerFound = object.equals(marker);

            if (markerFound)
            {
               break;
            }

            object = TeXParserUtils.resolve(object, parser);

            if (object instanceof Declaration)
            {
               if (parser.isDebugMode(TeXParser.DEBUG_DECL))
               {
                  parser.logMessage("PUSHING DECLARATION "+object);
               }

               pushDeclaration((Declaration)object);
            }
            else if (object instanceof EndDeclaration)
            {
               Declaration decl = ((EndDeclaration)object).getDeclaration(parser);

               if (parser.isDebugMode(TeXParser.DEBUG_DECL))
               {
                  parser.logMessage("POPPING DECLARATION "+decl);
               }

               popDeclaration(decl);
            }

            object.process(parser, this);
         }
      }
      else
      {
         clear();
         midcs.process(parser, before, after);
      }

      if (markerFound && parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
      {
         parser.logMessage("FOUND MARKER: "+marker);
      }

      if (isEmpty())
      {
         processEndDeclarations(parser);
      }

      return markerFound;
   }

   protected String toStringExtraIdentifier()
   {
      return "";
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(getClass().getSimpleName());

      if (stackID >= 0)
      {
         builder.append("#"+stackID);
      }

      builder.append(toStringExtraIdentifier());

      builder.append('[');

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject object = get(i);

         if (i > 0)
         {
            builder.append(", ");
         }

         if (object instanceof CharObject)
         {
            builder.append('\'');
            builder.append(object.toString());
            builder.append('\'');
         }
         else
         {
            builder.append(object.toString());
         }
      }

      builder.append(']');

      return builder.toString();
   }

   public String format()
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject object = get(i);

         builder.append(object.format());
      }

      return builder.toString();
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject object = get(i);

         builder.append(object.toString(parser));

         if (object instanceof ControlSequence
         && ((ControlSequence)object).isControlWord(parser)
         && i < n-1)
         {
            object = get(i+1);
           
            if (object instanceof Letter)
            {
               builder.append(" ");
            }
            else if (object instanceof TeXObjectList
                && !(object instanceof Group))
            {
               i++;
               String str = ((TeXObjectList)object).toString(parser);
               if (str.isEmpty())
               {
                  continue;
               }

               if (parser.isLetter(str.charAt(0)))
               {
                  builder.append(" ");
               }

               builder.append(str);
            }
         }
      }

      return builder.toString();
   }

   /**
    * Gets the string version of this list up to the designated
    * maximum index. Note that this list may contain sub-lists which
    * may themselves be long and won't be truncated if their index
    * in this list is less than or equal to the given maximum.
    * If you want a maximum number of characters in the output
    * instead, use toString(parser).substring(0,max)
    * @param parser the parser
    * @param maxIdx the maximum index
    * @param etc prefix to append if the list is truncated
    */ 
   public String toTruncatedString(TeXParser parser, int maxIdx, String etc)
   {
      if (maxIdx < size())
      {
         return substring(parser, 0, maxIdx) + etc;
      }
      else
      {
         return toString(parser);
      }
   }

   public String substring(TeXParser parser, int startIdx, int endIdx)
   {
      StringBuilder builder = new StringBuilder();

      for (int i = startIdx; i < endIdx; i++)
      {
         TeXObject object = get(i);

         builder.append(object.toString(parser));

         if (object instanceof ControlSequence
         && ((ControlSequence)object).isControlWord(parser)
         && i < endIdx-1)
         {
            object = get(i+1);
           
            if (object instanceof Letter)
            {
               builder.append(" ");
            }
            else if (object instanceof TeXObjectList
                && !(object instanceof Group))
            {
               i++;
               String str = ((TeXObjectList)object).toString(parser);
               if (str.isEmpty())
               {
                  continue;
               }

               if (parser.isLetter(str.charAt(0)))
               {
                  builder.append(" ");
               }

               builder.append(str);
            }
         }
      }

      return builder.toString();
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      if (size() == 0)
      {
         return this;
      }

      TeXObjectList list = pop().string(parser);

      addAll(0, list);

      return this;
   }

   public void pushDeclaration(Declaration decl)
   {
      declarations.add(decl);
   }

   public Declaration popDeclaration(Declaration decl)
   {
      for (int i = declarations.size()-1; i >= 0; i--)
      {
         if (declarations.get(i).equals(decl))
         {
            return declarations.remove(i);
         }
      }

      return null;
   }

   public void processEndDeclarations(TeXParser parser)
     throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING_STACK_LIST))
      {
         parser.logMessage("PROCESSING END DECLARATIONS: "+declarations);
      }

      while (declarations.size() > 0)
      {
         declarations.remove(declarations.size()-1).end(parser, this);
      }
   }

   @Override
   public boolean isPar()
   {
      return size() == 1 && firstElement().isPar();
   }

   public boolean popRemainingGroup(TeXParser parser, 
      Group group, byte popStyle, BgChar bgChar)
     throws IOException
   {
      while (size() > 0)
      {
         TeXObject obj = pop();

         EgChar egChar = parser.isEndGroup(obj);

         if (egChar != null)
         {
            if (!egChar.matches(bgChar))
            {
               throw new TeXSyntaxException(parser,
                 TeXSyntaxException.ERROR_EXTRA_OR_FORGOTTEN,
                 egChar.toString(parser), bgChar.toString(parser));
            }

            return true;
         }

         BgChar bgChar2 = parser.isBeginGroup(obj);

         if (isShort(popStyle) && obj.isPar())
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
         }
         else if (bgChar2 != null)
         {
            Group subGrp = bgChar2.createGroup(parser);

            if (!popRemainingGroup(parser, subGrp, popStyle, bgChar2))
            {
               group.add(subGrp);

               return false;
            }

            group.add(subGrp);
         }
         else
         {
            group.add(obj);
         }
      }

      return false;
   }

   public boolean containsVerbatimCommand(TeXParser parser)
   {
      for (int i = 0; i < size(); i++)
      {
         TeXObject obj = get(i);

         if (obj instanceof ControlSequence)
         {
            ControlSequence cs = (ControlSequence)obj;

            if (parser.isVerbCommand(cs.getName()))
            {
               return true;
            }
         }
         else if (obj instanceof TeXObjectList)
         {
            if (((TeXObjectList)obj).containsVerbatimCommand(parser))
            {
               return true;
            }
         }
      }

      return false;
   }

   public boolean isBlank()
   {
      for (TeXObject obj : this)
      {
         if ( ! (obj instanceof Ignoreable || obj instanceof WhiteSpace) )
         {
            return false;
         }
      }

      return true;
   }

   public TeXObjectList trim()
   {
      // strip redundant white space and grouping

      while (size() > 0
             && (firstElement() instanceof WhiteSpace
                  || firstElement() instanceof Ignoreable))
      {
         remove(0);
      }

      while (size() > 0
             && (lastElement() instanceof WhiteSpace
                  || lastElement() instanceof Ignoreable))
      {
         remove(size()-1);
      }

      if (size() == 1 && (get(0) instanceof Group))
      {
         return ((Group)get(0)).toList();
      }

      return this;
   }

   public void trimTrailing()
   {
      for (int i = size() - 1; i >= 0; i--)
      {
         TeXObject obj = get(i);

         if (obj instanceof WhiteSpace || obj instanceof Ignoreable)
         {
            remove(i);
         }
         else
         {
            break;
         }
      }
   }

   public void trimEmptyIgnoreablesAndEol()
   {
      while (size() > 0)
      {
         TeXObject object = firstElement();

         if (object instanceof Comment)
         {
            String commentText = ((Comment)object).getText().trim();

            if (commentText.isEmpty())
            {
               remove(0);
            }
            else
            {
               break;
            }
         }
         else if (object instanceof Eol
           || object instanceof SkippedEols)
         {
            remove(0);
         }
         else
         {
            break;
         }
      }

      while (size() > 0)
      {
         TeXObject object = lastElement();

         if (object instanceof Comment)
         {
            String commentText = ((Comment)object).getText().trim();

            if (commentText.isEmpty())
            {
               remove(size()-1);
            }
            else
            {
               break;
            }
         }
         else if (object instanceof Eol
           || object instanceof SkippedEols)
         {
            remove(size()-1);
         }
         else
         {
            break;
         }
      }
   }

   @Override
   public String purified()
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < size(); i++)
      {
         builder.append(get(i).purified());
      }

      return builder.toString();
   }

   private Vector<Declaration> declarations
     = new Vector<Declaration>();

   private long stackID = -1;
   private static long currentStackID=0;

   public static byte POP_SHORT=1;
   public static byte POP_RETAIN_IGNOREABLES=2;
   public static byte POP_IGNORE_LEADING_SPACE=4;
}
