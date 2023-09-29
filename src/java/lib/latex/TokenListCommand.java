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

   public TokenListCommand(String name, TeXObject newContent)
   {
      super(name);

      if (newContent instanceof TeXObjectList && !(newContent instanceof Group))
      {
         content = (TeXObjectList)newContent;
      }
      else
      {
         content = new TeXObjectList();
         content.add(newContent);
      }
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

   public void rightConcat(TeXObjectList list)
   {
      content.addAll(list);
   }

   public void appendValue(TeXObject obj, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (obj instanceof TokenListCommand)
      {
         appendValue((TokenListCommand)obj);
      }
      else if (!obj.isEmpty())
      {
         TeXObject expanded = TeXParserUtils.expandOnce(
           (TeXObject)obj.clone(), parser, stack);

         if (parser.isStack(expanded))
         {
            rightConcat((TeXObjectList)expanded);
         }
         else
         {
            append(expanded);
         }
      }
   }

   public void appendValue(TokenListCommand tl)
   {
      if (!tl.isEmpty())
      {
         rightConcat((TeXObjectList)tl.getContent().clone());
      }
   }

   /**
    * Prepends an element to the token list.
    */ 
   public void prepend(TeXObject obj)
   {
      content.add(0, obj);
   }

   public void leftConcat(TeXObjectList list)
   {
      content.addAll(0, list);
   }

   public void prependValue(TeXObject obj, TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      if (obj instanceof TokenListCommand)
      {
         prependValue((TokenListCommand)obj);
      }
      else if (!obj.isEmpty())
      {
         TeXObject expanded = TeXParserUtils.expandOnce(
           (TeXObject)obj.clone(), parser, stack);

         if (parser.isStack(expanded))
         {
            leftConcat((TeXObjectList)expanded);
         }
         else
         {
            prepend(expanded);
         }
      }
   }

   public void prependValue(TokenListCommand tl)
   {
      if (!tl.isEmpty())
      {
         leftConcat((TeXObjectList)tl.getContent().clone());
      }
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

   public String toString()
   {
      return String.format("%s[name=%s,content=%s]", getClass().getSimpleName(),
       getName(), content);
   }

   protected TeXObjectList content;

   public static final TokenListCommand EMPTY 
     = new TokenListCommand("c_empty_tl");
}
