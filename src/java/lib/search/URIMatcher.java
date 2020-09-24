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

import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class URIMatcher implements SearchMatcher
{
   public URIMatcher(Pattern pattern, int type)
   {
      this.pattern = pattern;

      switch (type)
      {
         case ALL:
         case SCHEME:
         case USER_INFO:
         case HOST:
         case PORT:
         case PATH:
         case QUERY:
         case FRAGMENT:
            this.type = type;
         break;
         default:
            throw new IllegalArgumentException("Invalid type: "+type);
      }
   }

   public boolean isMatch(Object queryObject)
   {
      if (!(queryObject instanceof URI)) return false;

      URI uri = (URI)queryObject;

      String str = "";

      switch (type)
      {
         case ALL:
            str = uri.toString();
         break;
         case SCHEME:
            str = uri.getScheme();
         break;
         case USER_INFO:
            str = uri.getUserInfo();
         break;
         case HOST:
            str = uri.getHost();
         break;
         case PORT:
            str = ""+uri.getPort();
         break;
         case PATH:
            str = uri.getPath();
         break;
         case QUERY:
            str = uri.getQuery();
         break;
         case FRAGMENT:
            str = uri.getFragment();
         break;
      }

      Matcher m = pattern.matcher(str);

      return m.matches();
   }

   public String toString()
   {
      return String.format("%s[pattern=%s,type=%d]", getClass().getSimpleName(), pattern, type);
   }

   private Pattern pattern;
   private int type;

   public static final int ALL = 0;
   public static final int SCHEME = 1;
   public static final int USER_INFO = 2;
   public static final int HOST = 3;
   public static final int PORT = 4;
   public static final int PATH = 5;
   public static final int QUERY = 6;
   public static final int FRAGMENT = 7;
}
