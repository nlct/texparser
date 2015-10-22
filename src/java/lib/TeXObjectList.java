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
   }

   public TeXObjectList(int capacity)
   {
      super(capacity);
   }

   public TeXObjectList(TeXParserListener listener, String text)
   {
      this(text.length() > 0 ? text.length() : 10);

      for (int i = 0, n = text.length(); i < n; i++)
      {
         add(listener.getOther(text.codePointAt(i)));
      }
   }

   public TeXObject expandedPopStack(TeXParser parser)
     throws IOException
   {
      return expandedPopStack(parser, false);
   }

   public TeXObject expandedPopStack(TeXParser parser, boolean isShort)
     throws IOException
   {
      TeXObject object = popStack(parser, isShort);

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
           ((TeXCsRef)object).getName());
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded =
            ((Expandable)object).expandfully(parser, this);

         if (expanded != null)
         {
            if (expanded instanceof Group)
            {
               object = expanded;
            }
            else
            {
               addAll(0, expanded);
               object = popStack(parser, isShort);
            }
         }
      }

      return object;
   }

   public TeXObject popStack(TeXParser parser) throws IOException
   {
      return popStack(parser, false);
   }

   public TeXObject popStack(TeXParser parser, boolean isShort)
     throws IOException
   {
      while (size() > 0 && (get(0) instanceof Ignoreable))
      {
         remove(0);
      }

      if (size() == 0)
      {
         return null;
      }

      TeXObject obj = remove(0);

      if (isShort && obj.isPar())
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_PAR_BEFORE_EG);
      }

      if (obj instanceof BgChar)
      {
         Group group = parser.getListener().createGroup();
         popRemainingGroup(parser, group, isShort);
         return group;
      }

      return obj;
   }

   public TeXObject popToken()
     throws IOException
   {
      while (size() > 0 && (get(0) instanceof Ignoreable))
      {
         remove(0);
      }

      if (size() == 0)
      {
         return null;
      }

      return remove(0);
   }

   public TeXObject pop()
     throws IOException
   {
      return size() == 0 ? null : remove(0);
   }

   public TeXObjectList popToGroup(TeXParser parser, boolean isShort)
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

         if (obj instanceof Group || obj instanceof BgChar)
         {
            break;
         }

         obj = remove(0);

         if (obj instanceof Ignoreable)
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

   public TeXUnit popUnit(TeXParser parser)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, true);

      if (object == null)
      {
         throw new TeXSyntaxException(
            parser.getCurrentFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      if (object instanceof CharObject)
      {
         char c1 = (char)((CharObject)object).getCharCode();

         TeXObject nextObj = expandedPopStack(parser);

         if (nextObj == null || !(nextObj instanceof CharObject))
         {
            push(object);

            throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
         }

         char c2 = (char)((CharObject)nextObj).getCharCode();

         try
         {
            return parser.getListener().createUnit(String.format("%c%c", c1, c2));
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

         throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      push(object);

      throw new TeXSyntaxException(
               parser.getCurrentFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
   }

   public Numerical popNumerical(TeXParser parser)
   throws IOException
   {
      TeXObject object = expandedPopStack(parser, true);

      if (object instanceof Register)
      {
         return (Register)object;
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
      TeXObject object = expandedPopStack(parser, true);

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

      TeXDimension stretch = null;

      TeXDimension shrink = null;

      object = expandedPopStack(parser);
      push(object);

      if (!(object instanceof CharObject))
      {
         return dimen;
      }

      if (((CharObject)object).getCharCode() == 'p')
      {
         stretch = popStretch(parser);

         if (stretch == null)
         {
            return dimen;
         }

         object = expandedPopStack(parser);
         push(object);

         if ((object instanceof CharObject)
          && (((CharObject)object).getCharCode() == 'm'))
         {
            shrink = popShrink(parser);
         }
      }
      else if (((CharObject)object).getCharCode() == 'm')
      {
         shrink = popShrink(parser);

         if (shrink == null)
         {
            return dimen;
         }
      }
      else
      {
         return dimen;
      }

      return new TeXGlue(parser, dimen, stretch, shrink);
   }

   private TeXDimension popStretch(TeXParser parser)
     throws IOException
   {
      TeXObject object = expandedPopStack(parser, true);

      if (!(object instanceof CharObject)
       ||((CharObject)object).getCharCode() != 'p')
      {
         push(object);
         return null;
      }

      TeXObject object2 = expandedPopStack(parser, true);

      if (!(object2 instanceof CharObject)
       ||((CharObject)object2).getCharCode() != 'l')
      {
         push(object2);
         push(object);
         return null;
      }

      TeXObject object3 = expandedPopStack(parser, true);

      if (!(object3 instanceof CharObject)
       ||((CharObject)object3).getCharCode() != 'u')
      {
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object4 = expandedPopStack(parser, true);

      if (!(object4 instanceof CharObject)
       ||((CharObject)object4).getCharCode() != 's')
      {
         push(object4);
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      Float value = popFloat(parser);

      object = expandedPopStack(parser, true);

      if (object instanceof DimenRegister)
      {
         TeXDimension dimen = new UserDimension();
         dimen.setDimension(parser, (DimenRegister)object);
         dimen.multiply(value.floatValue());
         return dimen;
      }

      push(object);

      TeXUnit unit = popUnit(parser);
      
      return new UserDimension(value, unit);
   }

   private TeXDimension popShrink(TeXParser parser)
     throws IOException
   {
      TeXObject object = expandedPopStack(parser, true);

      if (!(object instanceof CharObject)
       ||((CharObject)object).getCharCode() != 'm')
      {
         push(object);
         return null;
      }

      TeXObject object2 = expandedPopStack(parser, true);

      if (!(object2 instanceof CharObject)
       ||((CharObject)object2).getCharCode() != 'i')
      {
         push(object2);
         push(object);
         return null;
      }

      TeXObject object3 = expandedPopStack(parser, true);

      if (!(object3 instanceof CharObject)
       ||((CharObject)object3).getCharCode() != 'n')
      {
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object4 = expandedPopStack(parser, true);

      if (!(object4 instanceof CharObject)
       ||((CharObject)object4).getCharCode() != 'u')
      {
         push(object4);
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object5 = expandedPopStack(parser, true);

      if (!(object5 instanceof CharObject)
       ||((CharObject)object5).getCharCode() != 's')
      {
         push(object5);
         push(object4);
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      Float value = popFloat(parser);

      object = expandedPopStack(parser, true);

      if (object instanceof DimenRegister)
      {
         TeXDimension dimen = new UserDimension();
         dimen.setDimension(parser, (DimenRegister)object);
         dimen.multiply(value.floatValue());
         return dimen;
      }

      push(object);

      TeXUnit unit = popUnit(parser);
      
      return new UserDimension(value, unit);
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
      TeXObject object = expandedPopStack(parser);

      if (object instanceof TeXNumber)
      {
         return (TeXNumber)object;
      }

      if (object instanceof Group)
      {
         return ((Group)object).toList().popNumber(parser);
      }

      StringBuilder builder = new StringBuilder();
      
      popNumber(parser, object, builder);

      return new UserNumber(parser, builder.toString());
   }

   // object should be fully expanded
   protected void popNumber(TeXParser parser, TeXObject object,
     StringBuilder builder)
   throws IOException
   {
      if (object == null) return;

      String str = object.toString(parser);

      try
      {
         Integer.parseInt(builder.toString()+str);
      }
      catch (NumberFormatException e)
      {
         push(object);
         return;
      }

      builder.append(str);

      popNumber(parser, expandedPopStack(parser), builder);
   }

   public void push(TeXObject object)
   {
      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      if (object != null)
      {
         add(0, object);
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
      if (object == null)
      {
         throw new NullPointerException();
      }

      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add a list to itself");
      }

      return super.add(object);
   }

   public TeXObject peek()
   {
      return size() == 0 ? null : firstElement();
   }

   public TeXObject peekStack()
    throws IOException
   {
      if (size() == 0) return null;

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject obj = get(i);

         if (!(obj instanceof Ignoreable))
         {
            return obj;
         }
      }

      return null;
   }

   // Pops an argument off the stack. Removes any top level
   // grouping.
   public TeXObject popArg(TeXParser parser)
    throws IOException
   {
      return popArg(parser, false);
   }

   public TeXObject popArg(TeXParser parser, boolean isShort)
    throws IOException
   {
      TeXObject object = popStack(parser, isShort);

      if (object == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_EMPTY_STACK);
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
   public TeXObject popArg(TeXParser parser, char openDelim, char closeDelim)
     throws IOException
   {
      return popArg(parser, false, openDelim, closeDelim);
   }

   public TeXObject popArg(TeXParser parser, boolean isShort, 
     char openDelim, char closeDelim)
   throws IOException
   {
      TeXObject object = popStack(parser, isShort);

      if (!(object instanceof CharObject))
      {
         push(object);
         return null;
      }

      CharObject charObj = (CharObject)object;

      if (charObj.getCharCode() != (int)openDelim)
      {
         push(object);
         return null;
      }

      TeXObjectList list = new TeXObjectList();

      while (true)
      {
         object = pop();

         if (object == null) break;

         if (object instanceof CharObject)
         {
            charObj = (CharObject)object;

            if (charObj.getCharCode() == (int)closeDelim)
            {
               return list;
            }
         }
         else if (isShort && object.isPar())
         {
            break;
         }

         list.add(object);
      }

      throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_MISSING_CLOSING,
        ""+closeDelim);
   }

   public Numerical popNumericalArg(TeXParser parser, char openDelim, char closeDelim)
     throws IOException
   {
      TeXObject obj = popArg(parser, true, openDelim, closeDelim);

      if (obj == null) return null;

      if (obj instanceof Numerical)
      {
         return (Numerical)obj;
      }

      TeXObjectList expanded = null;

      if (obj instanceof Expandable)
      {
         expanded = ((Expandable)obj).expandfully(parser, this);
      }

      if (expanded != null)
      {
         obj = expanded;
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

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, null);
   }

   public TeXObjectList expandonce(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = createList();

      TeXObjectList remaining = (TeXObjectList)clone();

      if (stack != null)
      {
         while (stack.size() > 0)
         {
            remaining.add(stack.remove(0));
         }
      }

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.remove(0);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandonce(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      if (list instanceof Group)
      {
         TeXObjectList expanded = new TeXObjectList();
         expanded.add(list);
         return expanded;
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList list = createList();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.popStack(parser);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      if (list instanceof Group)
      {
         TeXObjectList expanded = new TeXObjectList();
         expanded.add(list);
         return expanded;
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      return expandfully(parser);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      while (size() > 0)
      {
         TeXObject object = remove(0);

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
            object.process(parser, this);
         }
      }
   }

   // Process with local stack.
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (stack != null)
      {
         while (stack.size() > 0)
         {
            TeXObject object = stack.remove(0);

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
               add(object);
            }
         }
      }

      process(parser);
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();
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
         declarations.pollLast().end(parser);
      }
   }

   public boolean isPar()
   {
      return size() == 1 && firstElement().isPar();
   }

   public boolean popRemainingGroup(TeXParser parser, 
      Group group, boolean isShort)
     throws IOException
   {
      while (size() > 0)
      {
         TeXObject obj = remove(0);

         if (obj instanceof EgChar)
         {
            return true;
         }

         if (isShort && obj.isPar())
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
         }
         else if (obj instanceof BgChar)
         {
            Group subGrp = parser.getListener().createGroup();

            if (!popRemainingGroup(parser, subGrp, isShort))
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

   private ArrayDeque<Declaration> declarations
     = new ArrayDeque<Declaration>();
}
