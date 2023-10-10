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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.util.Collection;

import com.dickimawbooks.texparserlib.*;

public class PropertyCommand<T> extends ControlSequence
{
   public PropertyCommand(String name)
   {
      super(name);
      hashtable = new Hashtable<T,TeXObject>();
   }

   public PropertyCommand(String name, PropertyCommand<T> other)
   {
      super(name);
      hashtable = new Hashtable<T,TeXObject>();

      for (Enumeration<T> e = hashtable.keys(); e.hasMoreElements(); )
      {
         T key = e.nextElement();
         hashtable.put(key, (TeXObject)other.get(key).clone());
      }
   }

   public Object clone()
   {
      return new PropertyCommand<T>(getName(), this);
   }

   @Override
   public boolean isEmpty()
   {
      return hashtable.isEmpty();
   }

   public int size()
   {
      return hashtable.size();
   }

   public void clear()
   {
      hashtable.clear();
   }

   public TeXObject get(T key)
   {
      return hashtable.get(key);
   }

   public TeXObject getOrDefault(T key, TeXObject defValue)
   {
      return hashtable.getOrDefault(key, defValue);
   }

   public void put(T key, TeXObject value)
   {
      hashtable.put(key, value);
   }

   public TeXObject remove(Object key)
   {
      return hashtable.remove(key);
   }

   public boolean remove(Object key, TeXObject value)
   {
      return hashtable.remove(key, value);
   }

   public Enumeration<T> keys()
   {
      return hashtable.keys();
   }

   public Set<T> keySet()
   {
      return hashtable.keySet();
   }

   public Enumeration<TeXObject> elements()
   {
      return hashtable.elements();
   }

   public Collection<TeXObject> values()
   {
      return hashtable.values();
   }

   public Hashtable<T,TeXObject> getHashtable()
   {
      return hashtable;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
   }

   protected Hashtable<T,TeXObject> hashtable;
}
