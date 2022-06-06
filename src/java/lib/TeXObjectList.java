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
      this(text.length() > 0 ? text.length() : 10);

      for (int i = 0, n = text.length(); i < n; )
      {
         int cp = text.codePointAt(i);
         i += Character.charCount(cp);

         add(listener.getOther(cp));
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

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
            ((TeXCsRef)object).getName());
      }

      if (object instanceof AssignedMacro)
      {
         object = ((AssignedMacro)object).getBaseUnderlying();
      }

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

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
            ((TeXCsRef)object).getName());
      }

      if (object instanceof AssignedMacro)
      {
         object = ((AssignedMacro)object).getBaseUnderlying();
      }

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

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
          ((TeXCsRef)object).getName());
      }

      if (object instanceof InternalQuantity)
      {
         return (InternalQuantity)object;
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

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
          ((TeXCsRef)object).getName());
      }

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
            return popNumber(parser);
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

   public TeXDimension popDimension(TeXParser parser)
    throws IOException
   {
      return popDimension(parser, true);
   }

   public TeXDimension popDimension(TeXParser parser, boolean glue)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, 
        (byte)(POP_SHORT | POP_IGNORE_LEADING_SPACE));

      if (object instanceof TeXDimension)
      {
         return (TeXDimension)object;
      }

      push(object);

      Float value = popFloat(parser);

      object = expandedPopStack(parser);

      if (object instanceof DimenRegister)
      {
         TeXDimension dimen = new TeXGlue(parser, (DimenRegister)object);
         dimen.multiply(value.floatValue());
         return dimen;
      }

      push(object);

      TeXUnit unit = popUnit(parser);

      TeXDimension dimen = new UserDimension(value, unit);

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

            TeXObject nextObj = peek();

            if (nextObj instanceof ControlSequence)
            {
               popStack(parser);

               String name = ((ControlSequence)nextObj).getName();

               codePoint = name.codePointAt(0);

               if (Character.charCount(codePoint) != name.length())
               {
                  throw new TeXSyntaxException(parser,
                    TeXSyntaxException.ERROR_IMPROPER_ALPHABETIC_CONSTANT,
                    nextObj.toString(parser));
               }

               // skip trailing spaces

               popLeadingWhiteSpace();

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
          object.toString(parser));
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
      TeXObject object = popStack(parser, popStyle);

      if (object == null && !(this instanceof TeXParser))
      {
         object = parser.popStack(popStyle);
      }

      if (!(object instanceof CharObject))
      {
         push(object);
         return null;
      }

      CharObject charObj = (CharObject)object;

      if (charObj.getCharCode() != openDelim)
      {
         push(object);
         return null;
      }

      TeXObjectList list = new TeXObjectList();
      boolean isShort = isShort(popStyle);

      while (true)
      {
         object = pop();

         if (object == null) break;

         BgChar bgChar = parser.isBeginGroup(object);

         if (object instanceof CharObject)
         {
            charObj = (CharObject)object;

            if (charObj.getCharCode() == closeDelim)
            {
               return list;
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

      throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_MISSING_CLOSING,
               closeDelim);
   }

   public Numerical popNumericalArg(TeXParser parser)
     throws IOException
   {
      TeXObject obj = popArg(parser, POP_SHORT);

      if (obj == null) return null;

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
         return ((TeXObjectList)obj).popNumerical(parser);
      }

      return new UserNumber(parser, obj.toString(parser));
   }

   public Numerical popNumericalArg(TeXParser parser, int openDelim, int closeDelim)
     throws IOException
   {
      TeXObject obj = popArg(parser, POP_SHORT, openDelim, closeDelim);

      if (obj == null) return null;

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
         return ((TeXObjectList)obj).popNumerical(parser);
      }

      return new UserNumber(parser, obj.toString(parser));
   }

   public TeXObjectList toLowerCase(TeXParser parser)
   {
      TeXObjectList list = createList();

      for (TeXObject object : this)
      {
         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
              ((TeXCsRef)object).getName());
         }

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
               list.add(new TeXCsRef(
                 ((ControlSequence)object).getName().toLowerCase()));
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
         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
              ((TeXCsRef)object).getName());
         }

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
               list.add(new TeXCsRef(
                ((ControlSequence)object).getName().toUpperCase()));
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
      flatten();

      TeXObjectList list = new TeXObjectList(size());
      TeXObjectList remaining = (TeXObjectList)clone();

      boolean blocked = false;

      while (!remaining.isEmpty())
      {
         TeXObject object = remaining.pop();

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         TeXObjectList expanded = null;

         if (!blocked && object.canExpand() && object instanceof Expandable)
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

               if (object instanceof TeXCsRef)
               {
                  object = parser.getListener().getControlSequence(
                     ((TeXCsRef)object).getName());
               }

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

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         TeXObjectList expanded = null;

         if (!blocked && object.canExpand() && object instanceof Expandable)
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

               if (object instanceof TeXCsRef)
               {
                  object = parser.getListener().getControlSequence(
                     ((TeXCsRef)object).getName());
               }

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

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList(size());

      if (isExpanded())
      {
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

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object instanceof AssignedMacro)
         {
            object = ((AssignedMacro)object).getBaseUnderlying();
         }

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         if (object instanceof StackMarker || blocked)
         {
            list.add(object);
         }
         else if (object instanceof Ignoreable)
         {// discard
         }
         else if (!object.canExpand())
         {
            list.add(object, true);
         }
         else if (object instanceof TeXObjectList
                   && ((TeXObjectList)object).isStack())
         {
            push(object, true);
         }
         else if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandonce(parser, this);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (!expanded.isEmpty())
            {
               push(expanded, true);
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      TeXObjectList list = new TeXObjectList(size());

      if (isExpanded())
      {
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

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object.isExpansionBlocker())
         {
            blocked = true;
         }

         TeXObjectList expanded = null;

         if (!blocked && object.canExpand() && object instanceof Expandable)
         {
            expanded = ((Expandable)object).expandonce(parser, remaining);
         }

         if (expanded == null)
         {
            list.add(object);
         }
         else
         {
            remaining.push(expanded, true);
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
      if (parser.getDebugLevel() > 0)
      {
         parser.logMessage("PROCESSING STACK "+toString());
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (parser.getDebugLevel() > 0)
         {
            parser.logMessage("POPPED "+object);
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object instanceof Declaration)
         {
            pushDeclaration((Declaration)object);
         }

         if (!(object instanceof Ignoreable))
         {
            if (parser.getDebugLevel() > 0)
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
      if (parser.getDebugLevel() > 0)
      {
         parser.logMessage("PROCESSING STACK "+toString()+" SUBSTACK: "+stack);
      }

      StackMarker marker = null;

      if (stack != parser && stack != null)
      {
         marker = new StackMarker();
         add(marker);

         addAll(stack);
         stack.clear();
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (parser.getDebugLevel() > 0)
         {
            parser.logMessage("POPPED "+object);
         }

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object instanceof Declaration)
         {
            pushDeclaration((Declaration)object);
         }

         if (!(object instanceof Ignoreable))
         {
            if (parser.getDebugLevel() > 0)
            {
               parser.logMessage("PROCESSING "+object);
            }

            object.process(parser, this);
         }
      }

      if (!isEmpty())
      {
         stack.addAll(this);
         clear();
      }
   }

   /**
    * Process up to the given marker and pick up any mid control sequences.
    * For use where the list is the interior of a group.
    * @param parser the parser
    * @param marker the marker
    */
   protected void processList(TeXParser parser, StackMarker marker)
    throws IOException
   {
      TeXObjectList before = new TeXObjectList();
      TeXObjectList after = new TeXObjectList();

      MidControlSequence midcs = null;

      for (int i = 0; i < size(); i++)
      {
         TeXObject object = get(i);

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

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

         while (size() != 0)
         {
            TeXObject object = remove(0);

            if (parser.getDebugLevel() > 0)
            {
               parser.logMessage("PROCESS LIST OBJ: "+object);
            }

            if (object.equals(marker) || object == null)
            {
               break;
            }

            if (object instanceof TeXCsRef)
            {
               object = parser.getListener().getControlSequence(
                  ((TeXCsRef)object).getName());
            }

            if (object instanceof Declaration)
            {
               pushDeclaration((Declaration)object);
            }

            object.process(parser, this);
         }
      }
      else
      {
         clear();
         midcs.process(parser, before, after);
      }

      processEndDeclarations(parser);
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

   public void processEndDeclarations(TeXParser parser)
     throws IOException
   {
      while (declarations.size() > 0)
      {
         declarations.pollLast().end(parser, this);
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

   private ArrayDeque<Declaration> declarations
     = new ArrayDeque<Declaration>();

   private long stackID = -1;
   private static long currentStackID=0;

   public static byte POP_SHORT=1;
   public static byte POP_RETAIN_IGNOREABLES=2;
   public static byte POP_IGNORE_LEADING_SPACE=4;
}
