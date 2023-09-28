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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class CsvList extends DataObjectList
{
   public CsvList()
   {
      super();
   }

   public CsvList(int capacity)
   {
      super(capacity);
   }

   public TeXObjectList createList()
   {
      return new CsvList(capacity());
   }

   public static CsvList getList(TeXParser parser, TeXObject object)
     throws IOException
   {
      CsvList csvList = new CsvList();

      csvList.parseList(parser, object);

      return csvList;
   }

   public void parseList(TeXParser parser, TeXObject object)
     throws IOException
   {
      if (object instanceof TeXObjectList)
      {
         if (((TeXObjectList)object).isEmpty()) return;

         TeXObjectList list = new TeXObjectList();

         for (TeXObject obj : (TeXObjectList)object)
         {
            if (isSeparator(obj))
            {
               add(list);
               list = new TeXObjectList();
            }
            else
            {
               list.add(obj);
            }
         }

         add(list);

      }
      else
      {
         if (!isSeparator(object))
         {
            add(object);
         }
      }
   }

   public TeXObject getSeparator(TeXParser parser)
   {
      return parser.getListener().getOther(',');
   }

   public boolean isSeparator(TeXObject obj)
   {
      return isComma(obj);
   }

   public static boolean isComma(TeXObject obj)
   {
      if (obj instanceof CharObject)
      {
         return ((CharObject)obj).getCharCode() == (int)',';
      }

      if (obj instanceof TeXObjectList
      && !(obj instanceof Group))
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
      return getValue(index, false);
   }

   public TeXObject getValue(int index, boolean trim)
   {
      TeXObject value = get(index);

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

         if (value instanceof Group
           &&!(value instanceof MathGroup))
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

         if (!(obj instanceof Ignoreable || (trim && obj instanceof WhiteSpace)))
         {
            start = i;
            break;
         }
      }

      for (int i = end; i >= 0; i--)
      {
         TeXObject obj = list.get(i);

         if (!(obj instanceof Ignoreable || (trim && obj instanceof WhiteSpace)))
         {
            end = i;
            break;
         }
      }

      if (start == end)
      {
         value = list.get(start);

         if (value instanceof Group
          &&!(value instanceof MathGroup))
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
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public boolean canExpand()
   {
      return true;
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
      TeXObjectList list = new TeXObjectList(capacity());

      for (int i = 0; i < size(); i++)
      {
         if (i > 0)
         {
            list.add(getSeparator(parser));
         }

         list.add((TeXObject)get(i).clone());
      }

      return list;
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
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      boolean isFirst = true;

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

         if (object.equals(marker))
         {
            break;
         }

         object = TeXParserUtils.resolve(object, parser);

         if (!(object instanceof Ignoreable))
         {
            if (isFirst)
            {
               isFirst = false;
            }
            else
            {
               getSeparator(parser).process(parser, stack);
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

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      boolean isFirst = true;

      while (size() > 0)
      {
         TeXObject object = remove(0);

         object = TeXParserUtils.resolve(object, parser);

         if (!(object instanceof Ignoreable))
         {
            if (isFirst)
            {
               isFirst = false;
            }
            else
            {
               getSeparator(parser).process(parser);
            }

            object.process(parser, this);
         }
      }
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
               builder.append(getSeparator(parser).toString(parser));
            }
         }

         builder.append(obj.toString(parser));
      }

      return builder.toString();
   }

   @Override
   public String format()
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
               builder.append(',');
            }
         }

         builder.append(obj.format());
      }

      return builder.toString();
   }

   @Override
   public String purified()
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
               builder.append(',');
            }
         }

         builder.append(obj.purified());
      }

      return builder.toString();
   }
}
