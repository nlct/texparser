/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DocumentBlock
{
   public DocumentBlock(String type)
   {
      this(type, DISPLAY_BLOCK);
   }

   public DocumentBlock(String type, int displayStyle)
   {
      this.type = type;
      setDisplayStyle(displayStyle);
      attributes = new HashMap<String,Object>();
   }

   private DocumentBlock()
   {
      type = "unknown";
      displayStyle = DISPLAY_BLOCK;
      attributes = new HashMap<String,Object>();
   }

   public String getType()
   {
      return type;
   }

   public int getDisplayStyle()
   {
      return displayStyle;
   }

   public void setDisplayStyle(int style)
   {
      switch (style)
      {
         case DISPLAY_BLOCK :
         case DISPLAY_INLINE_BLOCK :
         case DISPLAY_INLINE :
         break;
         default:
            throw new IllegalArgumentException("Invalid display style: "+style);
      }
   }

   public void clearAttributes()
   {
      attributes.clear();
   }

   public void setAttribute(String key, Object value)
   {
      attributes.put(key, value);
   }

   public Object getAttribute(String key)
   {
      return attributes.get(key);
   }

   public Set<String> getKeySet()
   {
      return attributes.keySet();
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(String.format("%s[type=%s,displayStyle=%d", 
        getClass().getSimpleName(), type, displayStyle));

      for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();

         builder.append(String.format(",%s=%s", key, attributes.get(key)));
      }

      builder.append(']');

      return builder.toString();
   }

   private String type;
   private int displayStyle;
   public static final int DISPLAY_BLOCK=0, DISPLAY_INLINE=1, DISPLAY_INLINE_BLOCK=2;
   private HashMap<String,Object> attributes;
}
