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

public class TeXObjectList extends Vector<TeXObject> implements TeXObject,Expandable
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

   public TeXObject popStack() throws IOException
   {
      return popStack(false);
   }

   public TeXObject popStack(boolean isShort)
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
            throw new TeXSyntaxException(
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
      }

      return obj;
   }

   public TeXObject pop()
     throws IOException
   {
      return size() == 0 ? null : remove(0);
   }

// TODO check for paragraph breaks if short
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

         if (obj instanceof Group)
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
      TeXObject object = popStack();

      if (object == null)
      {
         parser.push(new TeXUnit());
         throw new TeXSyntaxException(
            parser.getListenerFile(),
            parser.getLineNumber(),
            TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      if (object instanceof CharObject)
      {
         char c1 = (char)((CharObject)object).getCharCode();

         boolean found = false;

         for (int i = 0; i < TeXUnit.UNIT_NAMES.length; i++)
         {
            if (TeXUnit.UNIT_NAMES[i].charAt(0) == c1)
            {
               found = true;
               break;
            }
         }

         if (!found)
         {
            push(object);
            push(new TeXUnit());
            throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
         }

         TeXObject nextObj = popStack();

         if (nextObj == null || !(nextObj instanceof CharObject))
         {
            push(object);
            push(new TeXUnit());
            throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
         }

         char c2 = (char)((CharObject)object).getCharCode();

         for (int i = 0; i < TeXUnit.UNIT_NAMES.length; i++)
         {
            if (TeXUnit.UNIT_NAMES[i].charAt(0) == c1
             && TeXUnit.UNIT_NAMES[i].charAt(1) == c2)
            {
               return new TeXUnit(i);
            }
         }

         push(object);
         push(nextObj);
         push(new TeXUnit());
         throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser, this);

         if (expanded == null || expanded.size() == 0)
         {
            push(object);
            push(new TeXUnit());
            throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
         }

         TeXUnit unit = expanded.popUnit(parser);

         if (expanded.size() > 0)
         {
            addAll(0, expanded);
         }

         return unit;
      }

      push(object);
      push(new TeXUnit());
      throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_UNIT);
   }

   public Numerical popNumerical(TeXParser parser)
   throws IOException
   {
      TeXObject object = peekStack();

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(((TeXCsRef)object).getName());
      }

      if (object instanceof Register)
      {
         popStack();
         return (Register)object;
      }

      if (object instanceof TeXDimension)
      {
         popStack();
         return (TeXDimension)object;
      }

      return popNumber(parser);
   }

   public TeXDimension popDimension(TeXParser parser)
    throws IOException
   {
      TeXObject object = peekStack();

      if (object instanceof TeXDimension)
      {
         popStack();
         return (TeXDimension)object;
      }

      Float value = popFloat(parser);

      TeXUnit unit = popUnit(parser);

      return new UserDimension(value, unit);
   }

   public Float popFloat(TeXParser parser)
    throws IOException
   {
      TeXObject object = popStack();

      StringBuilder builder = new StringBuilder();
      
      popFloat(parser, object, builder);

      try
      {
         return Float.valueOf(builder.toString());
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(
                  parser.getListenerFile(),
                  parser.getLineNumber(),
                  TeXSyntaxException.ERROR_NUMBER_EXPECTED);
      }
   }

   protected void popFloat(TeXParser parser, TeXObject object, StringBuilder builder)
   throws IOException
   {
      if (object == null) return;

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser, this);

         if (expanded != null)
         {
            for (int i = 0, n = expanded.size(); i < n; i++)
            {
               TeXObject obj = expanded.get(i);

               String str = obj.toString(parser);

               try
               {
                  Float.parseFloat(builder.toString()+str+"0");
               }
               catch (NumberFormatException e)
               {
                  addAll(0, expanded.subList(i, n-1));

                  return;
               }

               builder.append(str);
            }

            object = popStack();
            popFloat(parser, object, builder);
            return;
         }
      }

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

      popFloat(parser, popStack(), builder);
   }

   public TeXNumber popNumber(TeXParser parser)
    throws IOException
   {
      TeXObject object = popStack();

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(((TeXCsRef)object).getName());
      }

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

      try
      {
         return new UserNumber(Integer.parseInt(builder.toString()));
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(
                  parser.getListenerFile(),
                  parser.getLineNumber(),
                  TeXSyntaxException.ERROR_NUMBER_EXPECTED);
      }
   }

   protected void popNumber(TeXParser parser, TeXObject object, StringBuilder builder)
   throws IOException
   {
      if (object == null) return;

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser, this);

         if (expanded != null)
         {
            for (int i = 0, n = expanded.size(); i < n; i++)
            {
               TeXObject obj = expanded.get(i);

               String str = obj.toString(parser);

               try
               {
                  Integer.parseInt(builder.toString()+str);
               }
               catch (NumberFormatException e)
               {
                  addAll(0, expanded.subList(i, n-1));

                  return;
               }

               builder.append(str);
            }

            object = popStack();
            popNumber(parser, object, builder);
            return;
         }
      }

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

      popNumber(parser, popStack(), builder);
   }

   public void push(TeXObject object)
   {
      add(0, object);
   }

   public void add(int index, TeXObject object)
   {
      if (object == null)
      {
         throw new NullPointerException();
      }

      super.add(index, object);
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
   public TeXObject popArg()
    throws IOException
   {
      TeXObject object = popStack();

      if (object == null)
      {
         throw new TeXSyntaxException(
            TeXSyntaxException.ERROR_MISSING_PARAM);
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
      TeXObject object = popStack();

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

         list.add(object);
      }

      throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_CLOSING,
        ""+closeDelim);
   }

   public Object clone()
   {
      TeXObjectList list = new TeXObjectList(Math.min(10, size()));

      list.addAll(this);

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
      TeXObjectList list = new TeXObjectList();

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

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.popStack();

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

      for (TeXObject object : this)
      {
         builder.append(object.toString(parser));
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

   private ArrayDeque<Declaration> declarations
     = new ArrayDeque<Declaration>();
}
