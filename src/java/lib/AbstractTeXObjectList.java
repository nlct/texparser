/*
    Copyright (C) 2013-2020 Nicola L.C. Talbot
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
import java.util.Collection;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;

public abstract class AbstractTeXObjectList extends Vector<TeXObject>
  implements TeXObject,Expandable,CaseChangeable
{
   public AbstractTeXObjectList()
   {
      super();
   }

   public AbstractTeXObjectList(int capacity)
   {
      super(capacity);
   }

   public AbstractTeXObjectList(TeXParserListener listener, String text)
   {
      this(text.length() > 0 ? text.length() : 10);

      for (int i = 0, n = text.length(); i < n; )
      {
         int cp = text.codePointAt(i);
         i += Character.charCount(cp);

         add(listener.getOther(cp));
      }
   }

   public abstract TeXObjectList toList();

   public abstract StackMarker createStackMarker();

   public TeXObject popStack(TeXParser parser)
     throws IOException
   {
      return popStack(parser, PopStyle.DEFAULT);
   }

   public TeXObject popStack(TeXParser parser, PopStyle popStyle)
     throws IOException
   {
      while (size() > 0)
      {
         TeXObject obj = get(0);

         if (!obj.isPopStyleSkip(popStyle))
         {
            break;
         }

         pop();
      }

      if (size() == 0)
      {
         return null;
      }

      popStyle = popStyle.excludeLeadingStyles();

      TeXObject obj = pop();

      if (popStyle.isShort() && obj.isPar())
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_PAR_BEFORE_EG);
      }

      BeginGroupObject bg = null;

      if (parser != null)
      {
         bg = parser.toBeginGroup(obj);
      }

      if (bg != null)
      {
         AbstractGroup group = bg.createGroup(parser);
         popRemainingGroup(parser, group, popStyle, bg);

         return group;
      }

      return obj;
   }

   public TeXObject popToken()
     throws IOException
   {
      return popToken(PopStyle.DEFAULT);
   }

   public TeXObject popToken(PopStyle popStyle)
     throws IOException
   {
      while (size() > 0)
      {
         TeXObject obj = get(0);

         if (!obj.isPopStyleSkip(popStyle))
         {
            break;
         }

         pop();
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

   public TeXObjectList popToGroup(TeXParser parser, PopStyle popStyle)
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

         BeginGroupObject bg = parser.toBeginGroup(obj);

         if (obj instanceof AbstractGroup || bg != null)
         {
            break;
         }

         obj = pop();

         if (!popStyle.isRetainIgnoreables() && obj instanceof Ignoreable)
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
      return popCsMarker(parser, name, PopStyle.DEFAULT);
   }

   public boolean popCsMarker(TeXParser parser, String name, PopStyle popStyle)
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
      return popToCsMarker(parser, name, PopStyle.DEFAULT);
   }

   public TeXObjectList popToCsMarker(TeXParser parser,
       String name, PopStyle popStyle)
    throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObject token = popToken(popStyle);

      popStyle = popStyle.excludeLeadingStyles();

      while (token != null)
      {
         if (parser.isControlSequence(token, name))
         {
            return list;
         }

         token = parser.resolveReference(token);

         if (token instanceof ExpandAfter)
         {
            TeXObject next1 = popToken(popStyle);
            TeXObject next2 = expandedPopStack(parser, popStyle, false);

            push(next2);
            push(next1);
         }
         else
         {
            list.add(token);
         }

         token = popToken(popStyle);
      }

      throw new TeXSyntaxException(parser, 
         TeXSyntaxException.ERROR_NOT_FOUND, 
         String.format("%s%s", 
          new String(Character.toChars(parser.getEscChar())), name));
   }

   public boolean containsExactly(AbstractTeXObjectList list)
   {
      for (int i = 0; i < size(); i++)
      {
         TeXObject obj = get(i);

         if (obj == list) return true;

         if (obj instanceof AbstractTeXObjectList)
         {
            if (((AbstractTeXObjectList)obj).containsExactly(list))
            {
               return true;
            }
         }
      }

      return false;
   }

   public void push(TeXObject object)
   {
      if (object == null)
      {
         throw new NullPointerException();
      }

      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      if (object instanceof TeXObjectList)
      {
         super.addAll(0, (TeXObjectList)object);
      }
      else
      {
         super.add(0, object);
      }
   }

   public void append(TeXObject object)
   {
      if (object instanceof TeXObjectList)
      {
         addAll((TeXObjectList)object);
      }
      else
      {
         add(object);
      }
   }


   @Override
   public void add(int index, TeXObject object)
   {
      if (object == null)
      {
         throw new NullPointerException();
      }

      if (object == this)
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      super.add(index, object);
   }

   @Override
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

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      return super.add(object);
   }

   @Override
   public void addElement(TeXObject object)
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

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      super.addElement(object);
   }

   @Override
   public void setElementAt(TeXObject object, int index)
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

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      super.setElementAt(object, index);
   }

   @Override
   public void insertElementAt(TeXObject object, int index)
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

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      super.insertElementAt(object, index);
   }

   @Override
   public TeXObject set(int index, TeXObject object)
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

      if ((object instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)object).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      return super.set(index, object);
   }

   @Override
   public boolean addAll(int index, Collection<? extends TeXObject> objects)
   {
      if (objects == null)
      {
         throw new NullPointerException();
      }

      if (objects == this)
      {
         throw new IllegalArgumentException(
           "Can't add a list to itself");
      }

      if ((objects instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)objects).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself");
      }

      return super.addAll(index, objects);
   }

   @Override
   public boolean addAll(Collection<? extends TeXObject> objects)
   {
      if (objects == null)
      {
         throw new NullPointerException();
      }

      if (objects == this)
      {
         throw new IllegalArgumentException(
           "Can't add a list to itself");
      }

      if ((objects instanceof AbstractTeXObjectList)
          && ((AbstractTeXObjectList)objects).containsExactly(this))
      {
         throw new IllegalArgumentException(
           "Can't add list to itself. This: "+toString()+"\nOther: "+objects);
      }

      return super.addAll(objects);
   }

   public TeXObject peek()
   {
      return size() == 0 ? null : firstElement();
   }

   public TeXObject peekLast()
   {
      return size() == 0 ? null : lastElement();
   }

   public StackMarker peekMarker()
    throws IOException
   {
      return peekMarker(PopStyle.DEFAULT);
   }

   public StackMarker peekMarker(PopStyle popStyle)
    throws IOException
   {
      if (size() == 0)
      {
         return null;
      }

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject obj = get(i);

         if (obj instanceof StackMarker)
         {
            return (StackMarker)obj;
         }

         if (!obj.isPopStyleSkip(popStyle))
         {
            return null;
         }
      }

      return null;
   }

   public TeXObject peekStack()
    throws IOException
   {
      return peekStack(PopStyle.DEFAULT);
   }

   public TeXObject peekStack(PopStyle popStyle)
    throws IOException
   {
      if (size() == 0)
      {
         return null;
      }

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject obj = get(i);

         if (!obj.isPopStyleSkip(popStyle))
         {
            return obj;
         }
      }

      return null;
   }

   public int getTeXCategory()
   {
      return TYPE_OBJECT;
   }

   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      return false;
   }

   // Pops an argument off the stack. Removes any top level
   // grouping. Ignore leading white space.
   public TeXObject popArg(TeXParser parser)
    throws IOException
   {
      return popArg(parser, PopStyle.IGNORE_LEADING_SPACE);
   }

   public TeXObject popArg(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject object = popStack(parser, popStyle);

      if (object == null && !(this instanceof TeXParser))
      {
         object = parser.popNextArg(popStyle);
      }

      if (object instanceof Group)
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
      return popArg(parser, PopStyle.IGNORE_LEADING_SPACE, openDelim, closeDelim);
   }

   public TeXObject popArg(TeXParser parser, PopStyle popStyle, 
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
      boolean isShort = popStyle.isShort();

      while (true)
      {
         object = pop();

         if (object == null) break;

         BeginGroupObject bg = parser.toBeginGroup(object);

         if (object instanceof CharObject)
         {
            charObj = (CharObject)object;

            if (charObj.getCharCode() == closeDelim)
            {
               return list;
            }
         }
         else if (bg != null)
         {
            AbstractGroup group = parser.getListener().createGroup();
            popRemainingGroup(parser, group, popStyle, bg);
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

   public AbstractTeXObjectList toLowerCase(TeXParser parser)
   {
      AbstractTeXObjectList list = createList();

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

   public AbstractTeXObjectList toUpperCase(TeXParser parser)
   {
      AbstractTeXObjectList list = createList();

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

   public abstract AbstractTeXObjectList createList();

   public Object clone()
   {
      AbstractTeXObjectList list = createList();

      for (TeXObject object : this)
      {
         list.add((TeXObject)object.clone());
      }

      return list;
   }

   protected void flatten()
   {
      for (int i = 0; i < size(); i++)
      {
         TeXObject obj = get(i);

         if (obj instanceof TeXObjectList)
         {
            remove(i);
            addAll(i, ((TeXObjectList)obj));
            i--;
         }
         else if (obj instanceof AbstractTeXObjectList)
         {
            ((AbstractTeXObjectList)obj).flatten();
         }
      }
   }

   public TeXObject expandedPopStack(TeXParser parser)
     throws IOException
   {
      return expandedPopStack(parser, PopStyle.DEFAULT);
   }

   public TeXObject expandedPopStack(TeXParser parser, PopStyle popStyle)
     throws IOException
   {
      return expandedPopStack(parser, popStyle, true);
   }

   public TeXObject expandedPopStack(TeXParser parser, PopStyle popStyle, boolean fully)
     throws IOException
   {
      if (isEmpty())
      {
         return null;
      }

      TeXObjectList stack = new TeXObjectList();

      stack.addAll(this);
      clear();

      TeXObject object = stack.popToken(popStyle);

      if (object instanceof Expandable)
      {
         TeXObjectList expanded;

         if (fully)
         {
            expanded = ((Expandable)object).expandfully(parser, stack);
         }
         else
         {
            expanded = ((Expandable)object).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            object = expanded;
         }
      }

      addAll(stack);

      return object;
   }

   /* Gets the index of the given marker or of the inner list that
    * contains the marker. 
    * Returns -1 if not in this list or any sub-list
    */
   public int indexOfMarker(StackMarker marker)
   {
      for (int i = 0; i < size(); i++)
      {
         TeXObject object = get(i);

         if (object.equals(marker))
         {
            return i;
         }

         if (object instanceof AbstractTeXObjectList)
         {
            if (((AbstractTeXObjectList)object).indexOfMarker(marker) != -1)
            {
               return i;
            }
         }
      }

      return -1;
   }

   public abstract TeXObjectList deconstruct(TeXParser parser)
     throws IOException;

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList stack = new TeXObjectList();
      stack.addAll(this);

      StackMarker marker = createStackMarker();
      stack.add(marker);

      AbstractTeXObjectList newList = createList();

      while (!stack.isEmpty())
      {
         TeXObject obj = parser.expandOnce(stack.pop(), stack);

         if (obj == null) break;

         if (obj instanceof TeXObjectList)
         {
            newList.addAll((TeXObjectList)obj);
         }
         else
         {
            newList.add(obj);
         }
      }

      TeXObjectList result = new TeXObjectList();

      int index = newList.indexOfMarker(marker);

      if (index == -1)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_LOST_MARKER, marker);
      }

      TeXObject obj = newList.remove(index);

      for (int i = index-1; i >= 0; i--)
      {
         result.push(newList.remove(i));
      }

      if (!obj.equals(marker))
      {
         if (obj instanceof AbstractTeXObjectList)
         {
            TeXObjectList deconstructed = 
              ((AbstractTeXObjectList)obj).deconstruct(parser);

            while (!deconstructed.isEmpty())
            {
               obj = deconstructed.pop();

               if (obj.equals(marker))
               {
                  break;
               }

               result.add(obj);
            }

            if (!deconstructed.isEmpty())
            {
               newList.push(deconstructed);
            }
         }
         else
         {
            throw new TeXSyntaxException(parser, 
              TeXSyntaxException.ERROR_LOST_MARKER, marker);
         }
      }

      if (!newList.isEmpty())
      {
         parser.push(newList);
      }

      return result;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      StackMarker marker = createStackMarker();
      stack.push(marker);

      stack.addAll(0, this);

      AbstractTeXObjectList newList = createList();

      while (!stack.isEmpty())
      {
         TeXObject obj = stack.pop();

         if (obj == null || obj.equals(marker))
         {
            break;
         }

         obj = parser.expandOnce(obj, stack);

         if (obj instanceof TeXObjectList)
         {
            TeXObjectList subStack = (TeXObjectList)obj;
            newList.addAll(subStack);

            if (subStack.indexOfMarker(marker) > -1)
            {
               break;
            }
         }
         else
         {
            newList.add(obj);
         }
      }

      TeXObjectList result = new TeXObjectList();

      int index = newList.indexOfMarker(marker);

      if (index == -1)
      {
         result.push(newList);
      }
      else
      {
         for (int i = newList.size()-1; i > index; i--)
         {
            result.push(newList.remove(i));
         }

         newList.remove(index);

         stack.push(newList);
      }

      return result;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList stack = new TeXObjectList();
      stack.addAll(this);

      StackMarker marker = createStackMarker();
      stack.add(marker);

      AbstractTeXObjectList newList = createList();

      while (!stack.isEmpty())
      {
         TeXObject obj = stack.pop();

         obj = parser.expandFully(obj, stack);

         if (obj == null) break;

         if (obj instanceof TeXObjectList)
         {
            newList.addAll((TeXObjectList)obj);
         }
         else
         {
            newList.add(obj);
         }
      }

      TeXObjectList result = new TeXObjectList();

      int index = newList.indexOfMarker(marker);

      if (index == -1)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_LOST_MARKER, marker);
      }

      TeXObject obj = newList.remove(index);

      for (int i = index-1; i >= 0; i--)
      {
         result.push(newList.remove(i));
      }

      if (!obj.equals(marker))
      {
         if (obj instanceof AbstractTeXObjectList)
         {
            TeXObjectList deconstructed = 
              ((AbstractTeXObjectList)obj).deconstruct(parser);

            while (!deconstructed.isEmpty())
            {
               obj = deconstructed.pop();

               if (obj.equals(marker))
               {
                  break;
               }

               result.add(obj);
            }

            if (!deconstructed.isEmpty())
            {
               newList.push(deconstructed);
            }
         }
         else
         {
            throw new TeXSyntaxException(parser, 
              TeXSyntaxException.ERROR_LOST_MARKER, marker);
         }
      }

      if (!newList.isEmpty())
      {
         parser.push(newList);
      }

      return result;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      StackMarker marker = createStackMarker();
      stack.push(marker);

      stack.addAll(0, this);

      AbstractTeXObjectList newList = createList();

      while (!stack.isEmpty())
      {
         TeXObject obj = stack.pop();

         if (obj == null || obj.equals(marker))
         {
            break;
         }

         obj = parser.expandFully(obj, stack);

         if (obj instanceof TeXObjectList)
         {
            TeXObjectList subStack = (TeXObjectList)obj;
            newList.addAll(subStack);

            if (subStack.indexOfMarker(marker) > -1)
            {
               break;
            }
         }
         else
         {
            newList.add(obj);
         }
      }

      TeXObjectList result = new TeXObjectList();

      int index = newList.indexOfMarker(marker);

      if (index == -1)
      {
         result.push(newList);
      }
      else
      {
         for (int i = newList.size()-1; i > index; i--)
         {
            result.push(newList.remove(i));
         }

         newList.remove(index);

         stack.push(newList);
      }

      return result;
   }

   public abstract void process(TeXParser parser)
      throws IOException;

   public abstract void process(TeXParser parser, TeXObjectList stack)
      throws IOException;

   @Override
   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return deconstruct(parser).string(parser);
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format("%s[", getClass().getSimpleName()));

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
      return format(false);
   }

   public String format(boolean skipIgnoreables)
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject object = get(i);

         if (object instanceof AbstractTeXObjectList)
         {
            ((AbstractTeXObjectList)object).format(skipIgnoreables);
         }
         else if (!(skipIgnoreables && (object instanceof Ignoreable)))
         {
            builder.append(object.format());
         }
      }

      return builder.toString();
   }

   public String stripToString(TeXParser parser)
    throws IOException
   {
      StringBuilder builder = new StringBuilder();

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject object = get(i);

         builder.append(object.stripToString(parser));
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
            else if (object instanceof TeXObjectList)
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
            else if (object instanceof TeXObjectList)
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

   public boolean popRemainingGroup(TeXParser parser, 
      AbstractGroup group, PopStyle popStyle, BeginGroupObject bg)
     throws IOException
   {
      while (size() > 0)
      {
         TeXObject obj = pop();

         EndGroupObject eg = parser.toEndGroup(obj);

         if (eg != null)
         {
            if (!eg.matches(bg))
            {
               throw new TeXSyntaxException(parser,
                 TeXSyntaxException.ERROR_EXTRA_OR_FORGOTTEN,
                 eg.toString(parser), bg.toString(parser));
            }

            return true;
         }

         BeginGroupObject bg2 = parser.toBeginGroup(obj);

         if (popStyle.isShort() && obj.isPar())
         {
            throw new TeXSyntaxException(parser,
               TeXSyntaxException.ERROR_PAR_BEFORE_EG);
         }
         else if (bg2 != null)
         {
            AbstractGroup subGrp = bg2.createGroup(parser);

            if (!popRemainingGroup(parser, subGrp, popStyle, bg2))
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

   public TeXUnit popUnit(TeXParser parser)
    throws IOException
   {
      return popUnit(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public TeXUnit popUnit(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, popStyle);

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

   public ControlSequence popControlSequence(TeXParser parser)
    throws IOException
   {
      return popControlSequence(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public ControlSequence popControlSequence(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject obj = popArg(parser, popStyle);

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;

         obj = list.popToken(popStyle);

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

   public Numerical popNumerical(TeXParser parser)
   throws IOException
   {
      TeXObject object = peekStack(PopStyle.IGNORE_LEADING_SPACE);

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

      object = expandedPopStack(parser, PopStyle.SHORT);

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
      return popDimension(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public TeXDimension popDimension(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, popStyle);

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
      TeXObject object = expandedPopStack(parser, 
         PopStyle.SHORT_IGNORE_LEADING_SPACE);

      if (!(object instanceof CharObject)
       ||((CharObject)object).getCharCode() != 'p')
      {
         push(object);
         return null;
      }

      TeXObject object2 = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object2 instanceof CharObject)
       ||((CharObject)object2).getCharCode() != 'l')
      {
         push(object2);
         push(object);
         return null;
      }

      TeXObject object3 = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object3 instanceof CharObject)
       ||((CharObject)object3).getCharCode() != 'u')
      {
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object4 = expandedPopStack(parser, PopStyle.SHORT);

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

      object = expandedPopStack(parser, PopStyle.SHORT);

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
      TeXObject object = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object instanceof CharObject)
       ||((CharObject)object).getCharCode() != 'm')
      {
         push(object);
         return null;
      }

      TeXObject object2 = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object2 instanceof CharObject)
       ||((CharObject)object2).getCharCode() != 'i')
      {
         push(object2);
         push(object);
         return null;
      }

      TeXObject object3 = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object3 instanceof CharObject)
       ||((CharObject)object3).getCharCode() != 'n')
      {
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object4 = expandedPopStack(parser, PopStyle.SHORT);

      if (!(object4 instanceof CharObject)
       ||((CharObject)object4).getCharCode() != 'u')
      {
         push(object4);
         push(object3);
         push(object2);
         push(object);
         return null;
      }

      TeXObject object5 = expandedPopStack(parser, PopStyle.SHORT);

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

      object = expandedPopStack(parser, PopStyle.SHORT);

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
      return popFloat(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public Float popFloat(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject object = expandedPopStack(parser, popStyle);

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
      return popNumber(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public TeXNumber popNumber(TeXParser parser, PopStyle popStyle)
    throws IOException
   {
      TeXObject object = peekStack(popStyle);

      int base = 10;

      if (object instanceof CharObject)
      {
         int codePoint = ((CharObject)object).getCharCode();

         if (codePoint == '"')
         {
            popStack(parser, popStyle);
            base = 16;
         }
         else if (codePoint == '\'')
         {
            popStack(parser, popStyle);
            base = 8;
         }
         else if (codePoint == '`')
         {
            popStack(parser, popStyle);

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

               popStack(parser, popStyle);

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

      if (object instanceof ControlSequence)
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_NUMBER_EXPECTED, object.toString(parser));
      }

      StringBuilder builder = new StringBuilder();

      int cp = parser.toCodePoint(object);

      if (cp == '+' || cp == '-')
      {
         builder.appendCodePoint(cp);
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

      if (object instanceof StackMarker)
      {
         push(object);
         return;
      }

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

   // strip redundant white space and grouping

   public AbstractTeXObjectList trim()
   {
      return trim(true);
   }

   public AbstractTeXObjectList trim(boolean stripOuterGroup)
   {
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

      if (stripOuterGroup && size() == 1 && (get(0) instanceof Group))
      {
         return ((Group)get(0)).toList();
      }

      return this;
   }

   // Strip all ignoreable objects
   public void stripIgnoreables()
   {
      for (int i = size()-1; i >= 0; i--)
      {
         if (get(i) instanceof Ignoreable)
         {
            remove(i);
         }
      }
   }

   public int countNonIgnoreables()
   {
      return countNonIgnoreables(false);
   }

   public int countNonIgnoreables(boolean recurse)
   {
      int total = 0;

      for (TeXObject obj : this)
      {
         if (recurse && obj instanceof AbstractTeXObjectList)
         {
            total += ((AbstractTeXObjectList)obj).countNonIgnoreables(recurse);
         }
         else if (!(obj instanceof Ignoreable))
         {
            total++;
         }
      }

      return total;
   }
}
