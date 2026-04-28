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

      startElement = new StartElement("li", true);
      endElement = new EndElement("li");
   }

   public L2HItem(String name, StartElement startElem, EndElement endElem)
   {
      super(name);
      this.startElement = startElem;
      this.endElement = endElem;
   }

   public Object clone()
   {
      return new L2HItem(getName(), (StartElement)startElement.clone(),
       (EndElement)endElement.clone());
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
         listener.startListItem(startElement, stack);

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
      return startElement.removeAttribute(attrName);
   }

   public String getAttribute(String attrName)
   {
      return startElement.getAttribute(attrName);
   }

   public boolean hasAttribute(String attrName)
   {
      return startElement.hasAttribute(attrName);
   }

   public void putAttribute(String attrName, String attrValue)
   {
      startElement.putAttribute(attrName, attrValue);
   }

   public void putStyle(L2HConverter listener, HashMap<String,String> css)
   {
      startElement.putStyle(listener, css);
   }

   public StartElement getStartElement()
   {
      return startElement;
   }

   public EndElement getEndElement()
   {
      return endElement;
   }

   protected StartElement startElement;
   protected EndElement endElement;
}
