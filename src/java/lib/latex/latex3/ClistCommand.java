/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.latex.CsvList;

public class ClistCommand extends Command implements L3StorageCommand 
{
   public ClistCommand(String name)
   {
      super(name);
      content = new TeXObjectList();
   }

   public ClistCommand(String name, int capacity)
   {
      super(name);
      content = new TeXObjectList(capacity);
   }

   public ClistCommand(String name, ClistCommand other)
   {
      super(name);
      content = (TeXObjectList)other.content.clone();
   }

   public static ClistCommand createFromClist(TeXParser parser,
     String name, CsvList csvList)
   {
      ClistCommand clist = new ClistCommand(name, csvList.capacity());

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.get(i);

         if (!(parser.isStack(obj) && obj.isEmpty()))
         {
            TeXObject item = csvList.getValue(i, true);

            clist.append(item);
         }
      }

      return clist;
   }

   @Override
   public Object clone()
   {
      ClistCommand clist = new ClistCommand(getName(), content.capacity());

      for (TeXObject obj : content)
      {
         clist.append((TeXObject)obj.clone());
      }

      return clist;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      CsvList list = new CsvList();

      list.addAll(content);

      return list;
   }

   /**
    * Sets the content.
    */ 
   public void setContent(TeXObjectList newContent)
   {
      content = newContent;
   }

   /**
    * Gets the underlying vector.
    */ 
   public TeXObjectList getContent()
   {
      return content;
   }

   @Override
   public void setQuantity(TeXParser parser, TeXObject obj)
    throws TeXSyntaxException
   {
      if (obj instanceof TeXObjectList)
      {
         content = (TeXObjectList)obj;
      }
      else
      {
         content = new TeXObjectList();
         content.add(obj);
      }
   }

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
    throws TeXSyntaxException
   {
      return (TeXObject)content.clone();
   }

   /**
    * Appends an element to the sequence (<code>\clist_put_right:Nn</code>).
    */ 
   public void append(TeXObject obj)
   {
      content.add(obj);
   }

   /**
    * Prepends an element to the sequence (<code>\clist_put_left:Nn</code>).
    */ 
   public void prepend(TeXObject obj)
   {
      content.add(0, obj);
   }

   /**
    * Sets an element in the underlying list (0-indexing), replacing the
    * existing element.
    */ 
   public TeXObject set(int index, TeXObject item)
   throws ArrayIndexOutOfBoundsException
   {
      return content.set(index, item);
   }

   /**
    * Gets an element in the list (0-indexing).
    */ 
   public TeXObject get(int index)
   {
     return content.get(index);
   }

   /**
    * Gets an element in the sequence (<code>\clist_item:Nn</code>).
    * Note that this doesn't include <code>\exp_not:n</code> to
    * prevent further expansion. Note that this returns an empty
    * list if the index is larger than the total number of items.
    * Index starts from 1.
    */ 
   public TeXObject item(int index)
   {
      if (content.size() > index)
      {
         return new TeXObjectList();
      }
      else
      {
         return content.get(index-1);
      }
   }

   public int size()
   {
      return content.size();
   }

   @Override
   public boolean isEmpty()
   {
      return content.isEmpty();
   }

   /**
    * Clears the list (<code>\clist_clear:N</code>).
    */ 
   @Override
   public void clear()
   {
      content.clear();
   }

   /**
    * Gets the first element in the list.
    */ 
   public TeXObject firstElement()
   {
      if (content.isEmpty())
      {
         return new TeXCsRef("q_no_value");
      }
      else
      {
         return content.firstElement();
      }
   }

   /**
    * Gets the last element in the list.
    */ 
   public TeXObject lastElement()
   {
      if (content.isEmpty())
      {
         return new TeXCsRef("q_no_value");
      }
      else
      {
         return content.lastElement();
      }
   }

   /**
    * Pops the first element in the list (<code>\clist_pop:NN</code>).
    */ 
   public TeXObject popFirst()
   {
      if (content.isEmpty())
      {
         return new TeXCsRef("q_no_value");
      }
      else
      {
         return content.remove(0);
      }
   }

   /**
    * Pops the last element in the list.
    */ 
   public TeXObject popLast()
   {
      if (content.isEmpty())
      {
         return new TeXCsRef("q_no_value");
      }
      else
      {
         return content.remove(content.size()-1);
      }
   }

   protected TeXObjectList content;
}
