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

   public TeXObject popStack() throws IOException
   {
      return popStack(false);
   }

// TODO check for paragraph breaks if short
   public TeXObject popStack(boolean isShort)
     throws IOException
   {
      while (size() > 0 && (get(0) instanceof Ignoreable))
      {
         remove(0);
      }

      return size() == 0 ? null : remove(0);
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

   public TeXDimension popDimension(TeXParser parser)
    throws IOException
   {
      TeXObject object = popStack();

      if (object == null)
      {
         push(new UserDimension());
         throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_MISSING_PARAM);

      }

      if (object instanceof TeXDimension)
      {
         return (TeXDimension)object;
      }

      if (object instanceof TeXNumber)
      {
         TeXNumber number = (TeXNumber)object;

         TeXUnit unit = popUnit(parser);

         return new UserDimension(number, unit);
      }

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)object).expandfully(parser,
          this);

         if (expanded == null || expanded.size() == 0)
         {
            push(object);
            push(new UserDimension());
            throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_DIMEN_EXPECTED);

         }

         if (expanded.firstElement() instanceof TeXDimension)
         {
            TeXDimension dimen = (TeXDimension)expanded.pop();
            addAll(0, expanded);

            return dimen;
         }

         if (expanded.firstElement() instanceof TeXNumber)
         {
            TeXNumber number = (TeXNumber)expanded.pop();

            TeXUnit unit = expanded.popUnit(parser);

            return new UserDimension(number, unit);
         }
      }

      push(object);

      push(new UserDimension());
      throw new TeXSyntaxException(
               parser.getListenerFile(),
               parser.getLineNumber(),
               TeXSyntaxException.ERROR_DIMEN_EXPECTED);

   }

   public void push(TeXObject object)
   {
      add(0, object);
   }

   public TeXObject peek()
   {
      return firstElement();
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

   public Object clone()
   {
      TeXObjectList list = new TeXObjectList(Math.min(10, size()));

      list.addAll(this);

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

   private ArrayDeque<Declaration> declarations
     = new ArrayDeque<Declaration>();
}
