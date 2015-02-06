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

import com.dickimawbooks.texparserlib.*;

// Can't implement this properly as there's no direct HTML
// equivalent
public class L2Hhfill extends ControlSequence
{
   public L2Hhfill()
   {
      this("hfill");
   }

   public L2Hhfill(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2Hhfill(getName());
   }

   public void process(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(new HtmlTag("<span style=\"float: right; \">"));

      TeXObject object = parser.pop();

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
           ((TeXCsRef)object).getName());
      }

      while (object != null
         && !(object instanceof Par)
         && !(object instanceof L2Hhfill))
      {
         list.add(object);

         object = parser.pop();

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
              ((TeXCsRef)object).getName());
         }
      }

      parser.push(object);

      list.add(new HtmlTag("</span>"));

      parser.addAll(0, list);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(new HtmlTag("<span style=\"float: right; \">"));

      TeXObject object = stack.pop();

      if (object instanceof TeXCsRef)
      {
         object = parser.getListener().getControlSequence(
           ((TeXCsRef)object).getName());
      }

      while (object != null
         && !(object instanceof Par)
         && !(object instanceof L2Hhfill))
      {
         list.add(object);

         object = stack.pop();

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
              ((TeXCsRef)object).getName());
         }
      }

      stack.push(object);

      list.add(new HtmlTag("</span>"));

      stack.addAll(0, list);
   }
}
