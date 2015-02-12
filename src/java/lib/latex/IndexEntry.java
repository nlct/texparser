/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
import java.util.*;

import com.dickimawbooks.texparserlib.*;

public class IndexEntry implements Comparable<IndexEntry>
{
   public IndexEntry(String sort, TeXObject entry)
   {
      this.sort = sort;
      this.entryValue = entry;

      subEntries = new TreeSet<IndexEntry>();
      locations = new Vector<IndexLocation>();
   }

   public void addLocation(IndexLocation location)
   {
      locations.add(location);
   }

   public IndexEntry addSubEntry(String subEntrySort, TeXObject subEntryValue)
   {
      return addSubEntry(subEntrySort, subEntryValue, null);
   }

   public IndexEntry addSubEntry(String subEntrySort, TeXObject subEntryValue,
     IndexLocation location)
   {
      IndexEntry entry = null;

      for (Iterator<IndexEntry> it = subEntries.iterator(); it.hasNext(); )
      {
         IndexEntry e = it.next();

         if (subEntrySort.equals(e.sort))
         {
            entry = e;
            break;
         }
      }

      if (entry == null)
      {
         entry = new IndexEntry(subEntrySort, subEntryValue);
         subEntries.add(entry);
      }

      if (location != null)
      {
         entry.addLocation(location);
      }

      return entry;
   }

   public int compareTo(IndexEntry e)
   {
      return sort.compareTo(e.sort);
   }

   private String sort;
   private TeXObject entryValue;

   private TreeSet<IndexEntry> subEntries;
   private Vector<IndexLocation> locations;
}
