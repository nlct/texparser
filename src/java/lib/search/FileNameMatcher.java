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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dickimawbooks.texparserlib.TeXPath;

public class FileNameMatcher implements SearchMatcher
{
   public FileNameMatcher(Pattern pattern, int type)
   {
      this.pattern = pattern;

      switch (type)
      {
         case FULL_PATH:
         case CANONICAL_PATH:
         case LEAF:
         case BASE_NAME:
         case EXTENSION:
            this.type = type;
         break;
         default:
            throw new IllegalArgumentException("Invalid type: "+type);
      }
   }

   public boolean isMatch(Object queryObject)
   {
      if (!(queryObject instanceof File)) return false;

      File file = (File)queryObject;

      String str = "";
      int idx;
      String name;

      switch (type)
      {
         case FULL_PATH:
            str = file.getAbsolutePath();
         break;
         case CANONICAL_PATH:
            try
            {
               str = file.getCanonicalPath();
            }
            catch (IOException | SecurityException e)
            {
               return false;
            }
         break;
         case LEAF:
            str = file.getName();
         break;
         case BASE_NAME:
            name = file.getName();
            idx = name.lastIndexOf(".");

            if (idx == -1)
            {
               str = name;
            }
            else if (idx > 0)
            {
               str = name.substring(0, idx);
            }
         break;
         case EXTENSION:
            name = file.getName();
            idx = name.lastIndexOf(".");

            if (idx > -1)
            {
               str = name.substring(idx+1);
            }
         break;
      }

      Matcher m = pattern.matcher(str);

      return m.matches();
   }

   private Pattern pattern;
   private int type;

   public static final int FULL_PATH=0;
   public static final int CANONICAL_PATH=1;
   public static final int LEAF=2;
   public static final int BASE_NAME=3;
   public static final int EXTENSION=4;
}
