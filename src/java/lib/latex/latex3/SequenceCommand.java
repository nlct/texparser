/*
    Copyright (C) 2023-2025 Nicola L.C. Talbot
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

public class SequenceCommand extends Command implements L3StorageCommand 
{
   public SequenceCommand(String name)
   {
      super(name);
      content = new TeXObjectList();
   }

   public SequenceCommand(String name, int capacity)
   {
      super(name);
      content = new TeXObjectList(capacity);
   }

   public SequenceCommand(String name, SequenceCommand other)
   {
      super(name);
      content = (TeXObjectList)other.content.clone();
   }

   public static SequenceCommand createFromSeqContent(TeXParser parser, String name,
     TeXObjectList otherContent)
   throws IOException
   {
      SequenceCommand seq = new SequenceCommand(name);

      if (!otherContent.isEmpty())
      {
         boolean foundStart = false;

         while (!otherContent.isEmpty())
         {
            TeXObject obj = otherContent.popStack(parser,
              TeXObjectList.POP_IGNORE_LEADING_SPACE);

            if (foundStart)
            {
               if (obj instanceof ControlSequence
                   && ((ControlSequence)obj).getName().equals("__seq_item:n"))
               {
                  obj = otherContent.popArg(parser);
                  seq.append(obj);
               }
            }
            else if (!(obj instanceof WhiteSpace))
            {
               if (obj instanceof ControlSequence
                   && ((ControlSequence)obj).getName().equals("s__seq"))
               {
                  foundStart = true;
               }
               else
               {
                  throw new LaTeXSyntaxException(parser, 
                    LaTeXSyntaxException.ERROR_NOT_SEQUENCE, name);
               }
            }
         }
      }

      return seq;
   }

   public static SequenceCommand createFromClist(TeXParser parser,
     String name, CsvList csvList)
   {
      SequenceCommand seq = new SequenceCommand(name, csvList.capacity());

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.get(i);

         if (!(parser.isStack(obj) && obj.isEmpty()))
         {
            TeXObject item = csvList.getValue(i, true);

            seq.append(item);
         }
      }

      return seq;
   }

   @Override
   public Object clone()
   {
      SequenceCommand seq = new SequenceCommand(getName(), content.capacity());

      for (TeXObject obj : content)
      {
         seq.append((TeXObject)obj.clone());
      }

      return seq;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = TeXParserUtils.createStack(parser);

      if (!content.isEmpty())
      {
         list.add(new TeXCsRef("s__seq"));

         for (TeXObject obj : content)
         {
            list.add(new TeXCsRef("__seq_item:n"));
            list.add(TeXParserUtils.createGroup(parser, obj));
         }
      }

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
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
    * Appends an element to the sequence (<code>\seq_put_right:Nn</code>).
    */ 
   public void append(TeXObject obj)
   {
      content.add(obj);
   }

   /**
    * Prepends an element to the sequence (<code>\seq_put_left:Nn</code>).
    */ 
   public void prepend(TeXObject obj)
   {
      content.add(0, obj);
   }

   /**
    * Replaces existing item with a new item
    * (<code>\seq_set_item:Nnn</code>). Indexing starts at 1.
    */ 
   public void setItem(int index, TeXObject item)
   throws ArrayIndexOutOfBoundsException
   {
      if (index < 1 || index > content.size())
      {
         throw new ArrayIndexOutOfBoundsException(index);
      }

      content.set(index-1, item);
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
    * Gets an element in the sequence (0-indexing).
    */ 
   public TeXObject get(int index)
   {
     return content.get(index);
   }

   /**
    * Gets an element in the sequence (<code>\seq_item:Nn</code>).
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
    * Clears the sequence (<code>\seq_clear:N</code>).
    */ 
   @Override
   public void clear()
   {
      content.clear();
   }

   /**
    * Gets the first element in the sequence (<code>\seq_get_left:NN</code>).
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
    * Gets the last element in the sequence (<code>\seq_get_right:NN</code>).
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
    * Pops the first element in the sequence (<code>\seq_pop_left:NN</code>).
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
    * Pops the last element in the sequence (<code>\seq_pop_right:NN</code>).
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

   public boolean contains(TeXObject object)
   {
      return content.contains(object);
   }

   public boolean contains(String strItem)
   {
      int cp = -1;

      if (!strItem.isEmpty())
      {
         cp = strItem.codePointAt(0);

         if (Character.charCount(cp) != strItem.length())
         {
            cp = -1;
         }
      }

      for (TeXObject obj : content)
      {
         if ((obj instanceof TeXObjectList
              && ((TeXObjectList)obj).equals(strItem))
            ||
             (obj instanceof CharObject && cp != -1 
                  && ((CharObject)obj).getCharCode() == cp))
         {
            return true;
         }
      }

      return false;
   }

   protected TeXObjectList content;
}
