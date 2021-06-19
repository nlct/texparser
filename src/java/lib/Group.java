/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class Group extends AbstractGroup
{
   public Group()
   {
      super();
   }

   public Group(int capacity)
   {
      super(capacity);
   }

   public Group(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public AbstractTeXObjectList createList()
   {
      return new Group();
   }

   @Override
   public String format()
   {
      return "{"+super.format()+"}";
   }

   public BeginGroupObject getBegin(TeXParser parser)
   {
      return parser.getListener().getBgChar(parser.getBgChar());
   }

   public EndGroupObject getEnd(TeXParser parser)
   {
      return parser.getListener().getEgChar(parser.getEgChar());
   }

   @Override
   public boolean isEmptyObject()
   {
      for (int i = 0, n = size(); i < n; i++)
      {
         TeXObject obj = get(i);

         if (!(obj instanceof Ignoreable))
         {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object o)
   {
      return ((o instanceof Group) && super.equals(o));
   }
}
