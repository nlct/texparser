/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HItem extends ListItem
{
   public L2HItem()
   {
      this("item");
   }

   public L2HItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HItem(getName());
   }

   @Override
   public void makelabel(TeXParser parser, TeXObjectList stack,
     TrivListDec trivList, TeXObject label)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (trivList.isInLine())
      {
         listener.writeliteral("<span class=\"inlineitem\">");
      }
      else
      {
         StartElement elem = new StartElement("li", true);

         if (attributes != null)
         {
            elem.putAllAttributes(attributes);
         }

         listener.startListItem(elem, stack);

         if (listener.isIfTrue(listener.getControlSequence("if@nmbrlist")))
         {
            listener.writeliteral("<span class=\"numitem\">");
         }
         else
         {
            listener.writeliteral("<span class=\"bulletitem\">");
         }
      }

      TeXParserUtils.process(label, parser, stack);

      listener.writeliteral("</span>");
   }

   public String removeAttribute(String attrName)
   {
      if (attributes == null)
      {
         return null;
      }

      return attributes.remove(attrName);
   }

   public String getAttribute(String attrName)
   {
      if (attributes == null)
      {
         return null;
      }

      return attributes.get(attrName);
   }

   public boolean hasAttribute(String attrName)
   {
      if (attributes == null)
      {
         return false;
      }

      return attributes.containsKey(attrName);
   }

   public void putAttribute(String attrName, String attrValue)
   {
      if (attributes == null)
      {
         attributes = new HashMap<String,String>();
      }

      attributes.put(attrName, attrValue);
   }

   public void putStyle(L2HConverter listener, HashMap<String,String> css)
   {
      String name = listener.getCssClass(css);

      if (name == null)
      {
         putAttribute("style", listener.cssAttributesToString(css));
      }
      else
      {
         putAttribute("class", name);
      }
   }

   private HashMap<String,String> attributes;
}
