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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;
import java.net.URI;
import java.net.URISyntaxException;

import com.dickimawbooks.texparserlib.*;

public class HtmlTag implements TeXObject
{
   public HtmlTag(String tag)
   {
      this.tag = tag;
   }

   public Object clone()
   {
      return new HtmlTag(getTag());
   }

   public TeXObjectList string(TeXParser parser)
   {
      return parser.getListener().createString(getTag());
   }

   public String toString()
   {
      return String.format("%s[tag=%s]", 
        getClass().getName(), getTag());
   }

   public String format()
   {
      return getTag();
   }

   public String toString(TeXParser parser)
   {
      return getTag();
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.getListener().getWriteable().write(tag);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }

   public String getTag()
   {
      return tag;
   }

   public boolean isPar()
   {
      return false;
   }

   public static String getUriFragment(String label)
   {
      try
      {
         URI uri = new URI(null, null, label);

         return uri.getRawFragment();
      }
      catch (URISyntaxException e)
      {
         StringBuilder builder = new StringBuilder();

         for (int i = 0, n = label.length(); i < n; i++)
         {
            char c = label.charAt(i);

            if ((c >= 'A' && c <= 'Z')
             || (c >= 'a' && c <= 'z')
             || c == ':' || c == '-' || c == '_' || c == '+')
            {
               builder.append(c);
            }
         }

         return builder.toString();
      }
   }

   private String tag;
}
