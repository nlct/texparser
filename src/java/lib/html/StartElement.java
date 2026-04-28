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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class StartElement extends HtmlTag
{
   public StartElement(String name)
   {
      this(name, false);
   }

   public StartElement(String name, boolean insertCR)
   {
      this(name, insertCR, name.equals("div"));
   }

   public StartElement(String name, boolean insertCR, boolean isBlock)
   {
      super(String.format("<%s>", name), name);

      if (name.contains("[^a-zA-Z]"))
      {
         throw new IllegalArgumentException(
          String.format("Invalid element name '%s'", name));
      }

      this.name = name;
      this.insertCR = insertCR;
      this.isBlock = isBlock;
   }

   @Override
   public Object clone()
   {
      StartElement elem = new StartElement(getName(), insertCR, isBlock);

      applyAttributesTo(elem);

      return elem;
   }

   @Override
   public String format()
   {
      StringBuilder builder = new StringBuilder();

      if (insertCR)
      {
         builder.append(String.format("%n"));
      }

      builder.append(String.format("<%s", name));

      if (attributes != null)
      {
         for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();
            String val = attributes.get(key);

            builder.append(String.format(" %s=\"%s\"", key, val));
         }
      }

      builder.append(">");

      return builder.toString();
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      if (isBlock)
      {
         listener.endParagraph();
         listener.setCurrentBlockType(DocumentBlockType.BLOCK);
      }

      if (insertCR)
      {
         listener.writeln();
      }

      String appendAnchor = null;

      if (listener.isNameAnchorRequired(name, isBlock) && attributes != null)
      {
         String id = attributes.get("id");

         if (id != null)
         {
            if (name.equals("a"))
            {
               attributes.put("name", id);
            }
            else
            {
               String anchor = String.format("<a name=\"%s\"></a>", id);

               if (name.equals("div"))
               {
                  appendAnchor = anchor;
               }
               else
               {
                  listener.writeliteral(anchor);
               }
            }

            attributes.remove("id");
         }
      }

      if (attributes == null || attributes.isEmpty())
      {
         listener.writeliteral(getHtmlCode());
      }
      else
      {
         listener.writeliteral("<"+name);

         for (Iterator<String> it = attributes.keySet().iterator(); it.hasNext(); )
         {
            String key = it.next();

            String val = attributes.get(key);

            listener.writeliteral(String.format(" %s=\"%s\"", key, val));
         }

         listener.writeliteral(">");
      }

      if (appendAnchor != null)
      {
         listener.writeliteral(appendAnchor);
      }
   }

   public String getName()
   {
      return name;
   }

   private String name;
   private boolean insertCR=false, isBlock=false;
}
