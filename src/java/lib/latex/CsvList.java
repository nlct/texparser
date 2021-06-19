/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class CsvList extends AbstractTeXObjectList
{
   public CsvList(TeXObject listSep)
   {
      super();
      this.listSep = listSep;
   }

   public CsvList(TeXObject listSep, int capacity)
   {
      super(capacity);
      this.listSep = listSep;
   }

   private CsvList()
   {
      super();
   }

   private CsvList(int capacity)
   {
      super(capacity);
   }

   @Override
   public AbstractTeXObjectList createList()
   {
      return new CsvList(listSep, capacity());
   }

   @Override
   public StackMarker createStackMarker()
   {
      return new InvisibleMarker();
   }

   @Override
   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList(capacity());

      for (int i = 0; i < size(); i++)
      {
         if (i > 0)
         {
            list.add((TeXObject)listSep.clone());
         }

         list.add((TeXObject)get(i).clone());
      }

      return list;
   }

   @Override
   public TeXObjectList deconstruct(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public static CsvList popCsvListFromStack(TeXParser parser, 
      TeXObjectList stack, boolean expandOnce)
     throws IOException
   {
      TeXObject obj = parser.popRequired(stack);

      if (obj instanceof CsvList) return (CsvList)obj;

      if (obj instanceof TeXCsRef)
      {
         obj = parser.getListener().getControlSequence(((TeXCsRef)obj).getName());
      }

      if (obj instanceof AssignedMacro)
      {
         obj = ((AssignedMacro)obj).getBaseUnderlying();
      }

      if (expandOnce && (obj instanceof Expandable))
      {
         TeXObjectList expanded; 

         if (parser == stack)
         {
            expanded = ((Expandable)obj).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)obj).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      CsvList csvList = null;

      if (obj instanceof TeXObjectList)
      {
         csvList = (CsvList)((TeXObjectList)obj).toObject(CsvList.class);
      }

      if (csvList == null)
      {
         csvList = getList(parser, obj);
      }

      return csvList;
   }

   public static CsvList getList(TeXParser parser, TeXObject object)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      CsvList csvList = listener.createCsvList();

      if (object instanceof TeXObjectList)
      {
         if (((TeXObjectList)object).isEmpty()) return csvList;

         TeXObjectList list = new TeXObjectList();

         for (TeXObject obj : (TeXObjectList)object)
         {
            if (csvList.isListSeparator(obj))
            {
               csvList.add(list);
               list = new TeXObjectList();
            }
            else
            {
               list.add(obj);
            }
         }

         csvList.add(list);

      }
      else
      {
         if (!csvList.isListSeparator(object))
         {
            csvList.add(object);
         }
      }

      return csvList;
   }

   public boolean isListSeparator(TeXObject object)
   {
      return listSep.equals(object);
   }

   public TeXObject getListSeparator()
   {
      return listSep;
   }

   /* Use parser.isCharacter() or CsvList.isListSeparator() instead */
   @Deprecated
   public static boolean isComma(TeXObject obj)
   {
      if (obj instanceof CharObject)
      {
         return ((CharObject)obj).getCharCode() == (int)',';
      }

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;

         if (list.size() == 1)
         {
            return isComma(list.firstElement());
         }
      }

      return false;
   }

   public TeXObject getValue(int index)
   {
      TeXObject value = get(index);

      if (value instanceof Group)
      {
         return ((Group)value).toList();
      }

      if (!(value instanceof TeXObjectList))
      {
         return value;
      }

      TeXObjectList list = (TeXObjectList)value;

      int n = list.size();

      if (n == 0) return value;

      if (n == 1)
      {
         value = list.get(0);

         if (value instanceof Group)
         {
            return ((Group)value).toList();
         }
         else
         {
            return value;
         }
      }

      int start = 0;
      int end = n-1;

      for (int i = 0; i < n; i++)
      {
         TeXObject obj = list.get(i);

         if (!(obj instanceof Ignoreable))
         {
            start = i;
            break;
         }
      }

      for (int i = end; i >= 0; i--)
      {
         TeXObject obj = list.get(i);

         if (!(obj instanceof Ignoreable))
         {
            end = i;
            break;
         }
      }

      if (start == end)
      {
         value = list.get(start);

         if (value instanceof Group)
         {
            return ((Group)value).toList();
         }
         else
         {
            return value;
         }
      }

      TeXObjectList valList = new TeXObjectList();

      for (int i = start; i <= end; i++)
      {
         valList.add(list.get(i));
      }

      return valList;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return toList();
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }

   @Override
   public boolean process(TeXParser parser, TeXObjectList stack, StackMarker marker)
      throws IOException
   {
      TeXObjectList list = toList();
      boolean foundMarker = list.process(parser, stack, marker);
      clear();
      return foundMarker;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      toList().process(parser, stack);
      clear();
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      toList().process(parser);
      clear();
   }

   @Override
   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;

      for (TeXObject obj : this)
      {
         if (!(obj instanceof Ignoreable))
         {
            if (isFirst)
            {
               isFirst = false;
            }
            else
            {
               builder.append(listSep.toString(parser));
            }
         }

         builder.append(obj.toString(parser));
      }

      return builder.toString();
   }

   @Override
   public boolean isPar()
   {
      return false;
   }

   @Override
   public boolean isEmptyObject()
   {
      return isEmpty();
   }

   private TeXObject listSep;
}
