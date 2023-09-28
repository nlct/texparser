/*
    Copyright (C) 2023 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

public class TokenListCommand extends Command
{
   public TokenListCommand(String name)
   {
      super(name);
      content = new TeXObjectList();
   }

   public TokenListCommand(String name, int capacity)
   {
      super(name);
      content = new TeXObjectList(capacity);
   }

   public TokenListCommand(String name, TeXObjectList content)
   {
      super(name);
      this.content = content;
   }

   public TokenListCommand(String name, TokenListCommand other)
   {
      super(name);
      this.content = (TeXObjectList)other.getContent().clone();
   }

   @Override
   public Object clone()
   {
      return new TokenListCommand(getName(), this);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return (TeXObjectList)content.clone();
   }

   public void trim()
   {
      content.trim();
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

   /**
    * Appends an element to the token list.
    */ 
   public void append(TeXObject obj)
   {
      content.add(obj);
   }

   /**
    * Prepends an element to the token list.
    */ 
   public void prepend(TeXObject obj)
   {
      content.add(0, obj);
   }

   /**
    * Sets an element in the sequence (0-indexing), replacing the
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
    * Clears the list.
    */ 
   public void clear()
   {
      content.clear();
   }

   /**
    * Gets the first non space token in the list.
    */ 
   public TeXObject head()
   {
      for (int i = 0; i < content.size(); i++)
      {
         TeXObject obj = content.get(i);

         if (!(obj instanceof Ignoreable || obj instanceof WhiteSpace))
         {
            return obj;
         }
      }

      return new TeXObjectList();
   }

   /**
    * Gets the tail. (Everything except the head.)
    */ 
   public TeXObject tail()
   {
      TeXObjectList list = new TeXObjectList();
      boolean skipping = true;

      for (int i = 0; i < content.size(); i++)
      {
         TeXObject obj = content.get(i);

         if (skipping 
              && !(obj instanceof Ignoreable || obj instanceof WhiteSpace))
         {
            skipping = false;
         }

         if (!skipping)
         {
            list.add((TeXObject)obj.clone());
         }
      }

      return list;
   }

   protected TeXObjectList content;

   public static final TokenListCommand EMPTY 
     = new TokenListCommand("c_empty_tl");
}
