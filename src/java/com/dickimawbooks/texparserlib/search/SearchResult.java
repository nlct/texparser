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

package com.dickimawbooks.texparserlib.search;

import java.io.File;

import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXReader;

public class SearchResult
{
   public SearchResult(TeXParser parser, SearchObject object)
   {
      TeXReader reader = parser.getReader();

      if (reader != null)
      {
         source = reader.getSource();

         if (source instanceof File)
         {
            file = (File)source;
         }

         lineNumber = reader.getLineNumber()+1;
      }

      foundObject = object;
      foundObjectDescription = object.getDescription(parser);
   }

   public File getFile()
   {
      return file;
   }

   public int getLineNumber()
   {
      return lineNumber;
   }

   public SearchObject getObject()
   {
      return foundObject;
   }

   public String getDescription()
   {
      return foundObjectDescription;
   }

   public String toString()
   {
      if (source != null)
      {
         if (lineNumber > 0)
         {
            return String.format("%s:%d: %s", source, lineNumber, 
              getDescription());
         }
         else
         {
            return String.format("%s: %s", source, getDescription());
         }
      }

      return getDescription();
   }

   private Object source;
   private File file;
   private int lineNumber=-1;
   private SearchObject foundObject;
   private String foundObjectDescription;
}
