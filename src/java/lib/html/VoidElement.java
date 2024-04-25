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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class VoidElement extends HtmlTag
{
   public VoidElement(String name)
   {
      this(name, false, false);
   }

   public VoidElement(String name, boolean insertCR, boolean isXml)
   {
      super(isXml ? String.format("<%s/>", name) : String.format("<%s>", name));

      if (name.contains("[^a-zA-Z]"))
      {
         throw new IllegalArgumentException(
          String.format("Invalid element name '%s'", name));
      }

      this.name = name;
      this.insertCR = insertCR;
      this.isXml = isXml;
   }

   @Override
   public Object clone()
   {
      VoidElement elem = new VoidElement(getName(), insertCR, isXml);

      if (attributes != null)
      {
         elem.attributes = new HashMap<String,String>();
         elem.attributes.putAll(attributes);
      }

      return elem;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return toString();
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      if (insertCR)
      {
         builder.append(String.format("%n"));
      }

      builder.append(String.format("<%s", name));

      if (attributes != null)
      {
         for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            String val = attributes.get(key);

            builder.append(String.format(" %s=\"%s\"", key, val));
         }
      }

      if (isXml)
      {
         builder.append("/");
      }

      builder.append(">");

      return builder.toString();
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      if (insertCR)
      {
         writeable.writeliteralln("");
      }

      if (attributes == null || attributes.isEmpty())
      {
         writeable.writeliteral(getTag());
      }
      else
      {
         writeable.writeliteral("<"+name);

         for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            String val = attributes.get(key);

            writeable.writeliteral(String.format(" %s=\"%s\"", key, val));
         }

         if (isXml)
         {
            writeable.writeliteral("/");
         }

         writeable.writeliteral(">");
      }
   }

   public String getName()
   {
      return name;
   }

   public String removeAttribute(String attrName)
   {
      if (attributes == null)
      {
         return null;
      }

      return attributes.remove(attrName);
   }

   public String getAttribute(String attrName)
   {
      if (attributes == null)
      {
         return null;
      }

      return attributes.get(attrName);
   }

   public boolean hasAttribute(String attrName)
   {
      if (attributes == null)
      {
         return false;
      }

      return attributes.containsKey(attrName);
   }

   public void putAttribute(String attrName, String attrValue)
   {
      if (attributes == null)
      {
         attributes = new HashMap<String,String>();
      }

      attributes.put(attrName, attrValue);
   }

   public void putStyle(L2HConverter listener, HashMap<String,String> css)
   {
      String name = listener.getCssClass(css);

      if (name == null)
      {
         putAttribute("style", listener.cssAttributesToString(css));
      }
      else
      {
         putAttribute("class", name);
      }
   }

   private String name;
   private boolean insertCR=false, isXml=false;
   private HashMap<String,String> attributes;
}
