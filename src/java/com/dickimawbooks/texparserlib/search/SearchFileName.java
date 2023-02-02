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
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class SearchFileName implements SearchObject
{
   public SearchFileName(File file, SearchMatcher matcher)
   {
      this.file = file;
      this.searchMatcher = matcher;
   }

   public String getDescription(TeXParser parser)
   {
      return file.toString();
   }

   public SearchMatcher getSearchMatcher()
   {
      return searchMatcher;
   }

   public File getFile()
   {
      return file;
   }

   private File file;
   private SearchMatcher searchMatcher;
}
