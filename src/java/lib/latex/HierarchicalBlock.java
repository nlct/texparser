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

import java.util.Iterator;

public class HierarchicalBlock extends DocumentBlock
   implements Comparable<HierarchicalBlock>
{
   public HierarchicalBlock(int level, String type)
   {
      super(type);
      this.level = level;
   }

   public int compareTo(HierarchicalBlock other)
   {
      if (level > other.level) return 1;
      if (level < other.level) return -1;
      return 0;
   }

   public int getLevel()
   {
      return level;
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append(String.format("%s[type=%s,displayStyle=%d,level=%d", 
        getClass().getSimpleName(), getType(), getDisplayStyle(), getLevel()));

      for (Iterator<String> it = getKeySet().iterator(); it.hasNext(); )
      {
         String key = it.next();

         builder.append(String.format(",%s=%s", key, getAttribute(key)));
      }

      builder.append(']');

      return builder.toString();
   }

   private int level;
}
