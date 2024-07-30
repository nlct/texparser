/*
    Copyright (C) 2023-2024 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;

public class PropertyCommand<T> extends ControlSequence implements L3StorageCommand 
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

   @Override
   public Object clone()
   {
      return new PropertyCommand<T>(getName(), this);
   }

   public static <T> PropertyCommand<T> getPropertyCommand(String csname,
     TeXParser parser, boolean editable)
    throws TeXSyntaxException
   {
      ControlSequence cs =
         parser.getControlSequence(csname);

      PropertyCommand<T> propCs;

      if (cs == null)
      {
         propCs = new PropertyCommand<T>(csname);

         if (editable)
         {
            parser.putControlSequence(csname.startsWith("l_"), propCs);
         }
      }
      else
      {
         propCs = toPropertyCommand(cs, parser, editable);
      }

      return propCs;
   }

   /**
    * Checks if the given object is a property command and returns
    * it or throws an exception. If the given object is a reference
    * to an unknown or empty command, a new property command will be
    * created. If editable is true, a copy of the property will be added to
    * the current scope to allow for localised changes.
    */
   public static <T> PropertyCommand<T> toPropertyCommand(
      TeXObject obj, TeXParser parser, boolean editable)
    throws TeXSyntaxException
   {
      PropertyCommand<T> propCs;
      ControlSequence cs;

      if (obj instanceof TeXCsRef)
      {
         String csname = ((TeXCsRef)obj).getName();
         cs = parser.getControlSequence(csname);

         if (cs == null)
         {
            propCs = new PropertyCommand<T>(csname);

            if (editable)
            {
               parser.putControlSequence(csname.startsWith("l_"), propCs);
            }

            return propCs;
         }
      }
      else if (obj instanceof ControlSequence)
      {
         cs = (ControlSequence)obj;
      }
      else
      {
         obj = TeXParserUtils.resolve(obj, parser);

         if (obj instanceof ControlSequence)
         {
            cs = (ControlSequence)obj;
         }
         else
         {
            throw new LaTeXSyntaxException(parser,
               LaTeXSyntaxException.ERROR_NOT_PROPERTY);
         }
      }

      if (cs instanceof Undefined)
      {
         String csname = cs.getName();
         propCs = new PropertyCommand<T>(csname);

         if (editable)
         {
            parser.putControlSequence(csname.startsWith("l_"), propCs);
         }
      }
      else if (cs instanceof PropertyCommand)
      {
         TeXSettings settings = parser.getSettings();

         String csname = cs.getName();

         if (editable && csname.startsWith("l_")
               && !settings.isDefinedInCurrentScope(csname))
         {
            @SuppressWarnings("unchecked")
            PropertyCommand<T> pc = (PropertyCommand<T>)cs.clone();
            propCs = pc;
            settings.putControlSequence(propCs);
         }
         else
         {
            @SuppressWarnings("unchecked")
            PropertyCommand<T> pc = (PropertyCommand<T>)cs;
            propCs = pc;
         }
      }
      else
      {
         throw new LaTeXSyntaxException(parser,
            LaTeXSyntaxException.ERROR_NOT_PROPERTY);
      }

      return propCs;
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

   @Override
   public void clear()
   {
      hashtable.clear();
   }

   @Override
   public void append(TeXObject obj)
   {
   }

   @Override
   public void prepend(TeXObject obj)
   {
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

   public TeXObject remove(T key)
   {
      return hashtable.remove(key);
   }

   public boolean remove(T key, TeXObject value)
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
   public void setQuantity(TeXParser parser, TeXObject obj)
    throws TeXSyntaxException
   {
// TODO
   }

   @Override
   public TeXObject getQuantity(TeXParser parser, TeXObjectList stack)
    throws TeXSyntaxException
   {
// TODO
      return new TeXObjectList();
   }

   @Override
   public TeXObject getContent()
   {
      return new TeXObjectList();
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
