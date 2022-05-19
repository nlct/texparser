/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.util.HashMap;

public class Category
{
   public Category(String label)
   {
      this.label = label;
   }

   public String getLabel()
   {
      return label;
   }

   public void setAttribute(String name, String value)
   {
      if (attributes == null)
      {
         attributes = new HashMap<String,String>();
      }

      attributes.put(name, value);
   }

   public String getAttribute(String name)
   {
      if (attributes == null) return null;

      return attributes.get(name);
   }

   public boolean isAttributeTrue(String name)
   {
      return isAttribute(name, "true");
   }

   public boolean isAttributeFalse(String name)
   {
      return isAttribute(name, "false");
   }

   public boolean isAttribute(String name, String attrVal)
   {
      String val = getAttribute(name);

      return (val != null && val.equals(attrVal));
   }

   protected String label;
   protected HashMap<String,String> attributes;
}
