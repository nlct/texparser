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
package com.dickimawbooks.texparserlib.latex.booktabs;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class BKTrule extends Hline implements Expandable
{
   public BKTrule(String name, int position)
   {
      super(name);
      setPosition(position);
   }

   public Object clone()
   {
      BKTrule rule = new BKTrule(getName(), getPosition());

      rule.width = width == null ? null : (TeXObject)width.clone();

      return rule;
   }

   public void setPosition(int position)
   {
      if (position != TOP && position != MIDDLE && position != BOTTOM)
      {
         throw new IllegalArgumentException("Invalid position: "+position);
      }

      this.position = position;
   }

   public int getPosition()
   {
      return position;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      return expandfully(parser, stack);
   }

   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = parser == stack ? parser.popNextArg('[', ']') :
         stack.popArg(parser, '[', ']');

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      TeXObjectList list = new TeXObjectList();

      if (arg == null)
      {
         setWidth(null);
      }
      else
      {
         setWidth(arg);
      }

      list.add(this);

      return list;
   }

   public TeXDimension getWidth(TeXParser parser)
    throws IOException
   {
      TeXObject obj;

      if (width == null)
      {
         if (position == MIDDLE)
         {
            obj = parser.getListener().getControlSequence("lightrulewidth");
         }
         else
         {
            obj = parser.getListener().getControlSequence("heavyrulewidth");
         }
      }
      else
      {
         obj = width;
      }

      if (obj instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)obj).expandfully(parser);

         if (expanded != null)
         {
            obj = expanded;
         }
      }

      if (obj instanceof TeXDimension)
      {
         return (TeXDimension)obj;
      }

      return new UserDimension(parser, obj.toString(parser));
   }

   public TeXObject getWidth()
   {
      return width;
   }

   public void setWidth(TeXObject width)
   {
      this.width = width;
   }

   private int position;

   private TeXObject width;

   public static int TOP=0, MIDDLE=1, BOTTOM=2;
}
