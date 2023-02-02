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

public class IndexRoot
{
   public IndexRoot()
   {
      entries = new TreeSet<IndexEntry>();
   }

   public void setEnCap(int value)
   {
      encap = value;
   }

   public void setLevel(int value)
   {
      level = value;
   }

   public void setActual(int value)
   {
      actual = value;
   }

   public void setRangeOpen(int value)
   {
      rangeOpen = value;
   }

   public void setRangeClose(int value)
   {
      rangeClose = value;
   }

   public void setEscape(int value)
   {
      escape = value;
   }

   public void addEntry(TeXParser parser, TeXObject object, TeXObject location)
     throws IOException
   {
      TeXObjectList list;

      if (object instanceof TeXObjectList)
      {
         list = (TeXObjectList)object;
      }
      else
      {
         list = parser.getListener().createString(object.toString(parser));
      }

      TeXObjectList encapObject = null;
      TeXObjectList entryValue = new TeXObjectList();
      String sort = null;

      IndexEntry parent = null;

      for (int i = 0, n = list.size(); i < n; i++)
      {
         TeXObject obj = list.get(i);

         if (obj instanceof CharObject)
         {
            int charCode = ((CharObject)obj).getCharCode();

            if (charCode == escape)
            {
               i++;
               entryValue.add(list.get(i));
            }
            else if (charCode == encap)
            {
               encapObject = new TeXObjectList();
               encapObject.addAll(list.subList(i+1, n));
               i = n;
            }
            else if (charCode == actual)
            {
               sort = entryValue.toString(parser);
               entryValue = new TeXObjectList();
            }
            else if (charCode == level)
            {
               if (parent == null)
               {
                  IndexEntry entry = new IndexEntry(sort, entryValue);
                  entries.add(entry);
                  parent = entry;
               }
               else
               {
                  parent = parent.addSubEntry(sort, entryValue);
               }
            }
            else
            {
               entryValue.add(obj);
            }
         }
         else
         {
            entryValue.add(obj);
         }
      }

      if (parent == null)
      {
         IndexEntry entry = new IndexEntry(sort, entryValue);
         entries.add(entry);
         parent = entry;
      }
      else
      {
         parent = parent.addSubEntry(sort, entryValue);
      }

      byte type = IndexLocation.NORMAL;

      if (encapObject != null)
      {
         if (encapObject.isEmpty())
         {
            encapObject = null;
         }
         else
         {
            TeXObject first = encapObject.firstElement();

            if (first instanceof CharObject)
            {
               int charCode = ((CharObject)first).getCharCode();

               if (charCode == rangeOpen)
               {
                  type = IndexLocation.OPEN;
                  encapObject.remove(0);
               }
               else if (charCode == rangeClose)
               {
                   type = IndexLocation.CLOSE;
                   encapObject.remove(0);
               }
            }
         }
      }

      parent.addLocation(new IndexLocation(encapObject, location, type));
   }

   private TreeSet<IndexEntry> entries;

   private int encap = (int)'|';
   private int level = (int)'!';
   private int actual = (int)'@';
   private int rangeOpen = (int)'(';
   private int rangeClose = (int)')';
   private int escape = (int)'"';
}
