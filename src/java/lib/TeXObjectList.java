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

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;

public class TeXObjectList extends AbstractTeXObjectList
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
      super(listener, text);
   }

   @Override
   public boolean isPopStyleSkip(PopStyle popStyle)
   {
      for (int i = 0; i < size(); i++)
      {
         if (!get(i).isPopStyleSkip(popStyle))
         {
            return false;
         }
      }

      return true;
   }

   @Override
   public AbstractTeXObjectList createList()
   {
      return new TeXObjectList(capacity());
   }

   @Override
   public TeXObjectList toList()
   {
      return this;
   }

   @Override
   public TeXObject expandedPopStack(TeXParser parser)
     throws IOException
   {
      return expandedPopStack(parser, PopStyle.DEFAULT);
   }

   @Override
   public TeXObject expandedPopStack(TeXParser parser, PopStyle popStyle, 
       boolean fully)
     throws IOException
   {
      if (size() == 0)
      {
         return null;
      }

      flatten();

      TeXObject object = parser.resolveReference(popToken(popStyle));

      popStyle = popStyle.excludeLeadingStyles();

      if (object instanceof EndCs)
      {
         return object;
      }

      if (object instanceof AbstractGroup)
      {
         // most likely object has already been popped and pushed back
         return (AbstractGroup)object;
      }

      BeginGroupObject bg = parser.toBeginGroup(object);

      if (bg != null)
      {
         AbstractGroup group = bg.createGroup(parser);
         parser.startGroup();
         popExpandedRemainingGroup(parser, group, popStyle, bg, fully);
         parser.endGroup();
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

      TeXObjectList expanded;

      if (fully)
      {
         expanded = ((Expandable)object).expandfully(parser, this);
      }
      else
      {
         expanded = ((Expandable)object).expandonce(parser, this);
      }

      if (expanded == null)
      {
         return object;
      }

      return expanded;
   }

   public boolean popExpandedRemainingGroup(TeXParser parser, 
      AbstractGroup group, PopStyle popStyle, BeginGroupObject bg)
     throws IOException
   {
      return popExpandedRemainingGroup(parser, group, popStyle, bg, true);
   }

   public boolean popExpandedRemainingGroup(TeXParser parser, 
      AbstractGroup group, PopStyle popStyle, BeginGroupObject bg, boolean fully)
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

            if (!popExpandedRemainingGroup(parser, subGrp, popStyle, bg2, fully))
            {
               group.add(subGrp);

               return false;
            }

            group.add(subGrp);
         }
         else if (obj instanceof Expandable)
         {
            TeXObjectList expanded;

            if (fully)
            {
               expanded = ((Expandable)obj).expandfully(parser, this);
            }
            else
            {
               expanded = ((Expandable)obj).expandonce(parser, this);
            }

            if (expanded != null)
            {
               group.add(obj);
            }
            else
            {
               group.addAll(expanded);
            }
         }
         else
         {
            group.add(obj);
         }
      }

      return false;
   }

   public Register popRegister(TeXParser parser)
     throws IOException
   {
      return popRegister(parser, PopStyle.DEFAULT);
   }

   public Register popRegister(TeXParser parser, PopStyle popStyle)
     throws IOException
   {
      TeXObject object = parser.popNextTokenResolveReference(this, popStyle);

      if (object == null)
      {
         throw new TeXSyntaxException(parser,
            TeXSyntaxException.ERROR_REGISTER_EXPECTED);
      }

      if (object instanceof Register)
      {
         return (Register)object;
      }

      throw new TeXSyntaxException(parser,
         TeXSyntaxException.ERROR_REGISTER_EXPECTED_BUT_FOUND,
          object.toString(parser));
   }

   public Numerical popNumericalArg(TeXParser parser)
     throws IOException
   {
      return popNumericalArg(parser, PopStyle.SHORT_IGNORE_LEADING_SPACE);
   }

   public Numerical popNumericalArg(TeXParser parser, PopStyle popStyle)
     throws IOException
   {
      TeXObject obj = popArg(parser, popStyle);

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
         TeXObjectList subList = ((TeXObjectList)obj);
         Numerical num = subList.popNumerical(parser);
         addAll(0, subList);
         return num;
      }

      return new UserNumber(parser, obj.toString(parser));
   }

   public Numerical popNumericalArg(TeXParser parser, int openDelim, int closeDelim)
     throws IOException
   {
      TeXObject obj = popArg(parser, PopStyle.SHORT, openDelim, closeDelim);

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

   @Override
   public TeXObjectList deconstruct(TeXParser parser)
     throws IOException
   {
      flatten();
      return this;
   }

   @Override
   public StackMarker createStackMarker()
   {
      return new InvisibleMarker();
   }

   @Override
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

         object.process(parser, this);
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      StackMarker marker = null;

      if (stack != parser && stack != null)
      {
         marker = createStackMarker();
         add(marker);

         addAll(stack);
         stack.clear();
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object.process(parser, this, marker))
         {
            break;
         }
      }

      if (!isEmpty())
      {
         stack.addAll(this);
         clear();
      }
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      int index = indexOfMarker(marker);

      if (index == -1)
      {
         process(parser, stack);
         return false;
      }

      while (size() > 0)
      {
         TeXObject object = remove(0);

         if (object.equals(marker))
         {
            break;
         }

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object.process(parser, this, marker))
         {
            break;
         }
      }

      if (!isEmpty())
      {
         stack.addAll(this);
         clear();
      }

      return true;
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

   @Override
   public boolean isPar()
   {
      return size() == 1 && firstElement().isPar();
   }

   @Override
   public boolean isEmptyObject()
   {
      int total = 0;

      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject obj = get(i);

         if (!(obj instanceof Ignoreable))
         {
            total++;

            if (total > 1 || !obj.isEmptyObject()) return false;
         }
      }

      return total <= 1;
   }

   /* If this list only contains one non-ignoreable object that has
    * the same type as the given object, return that element otherwise
    * return null.
    */
   public TeXObject toObject(Class classObject)
   {
      TeXObject object = null;

      for (int i = 0; i < size(); i++)
      {
         TeXObject o = get(i);

         if (o instanceof Ignoreable) continue;

         if (object != null)
         {
            return null;
         }

         if (o.getClass().equals(classObject))
         {
            object = o;
         }
      }

      return object;
   }
}
