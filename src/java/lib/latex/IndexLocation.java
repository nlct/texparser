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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.*;

import com.dickimawbooks.texparserlib.*;

public class IndexLocation implements TeXObject
{
   public IndexLocation(TeXObject location)
   {
      this(null, location, NORMAL);
   }

   public IndexLocation(TeXObject format, TeXObject location)
   {
      this(format, location, NORMAL);
   }

   public IndexLocation(TeXObject format, TeXObject location, byte type)
   {
      this.format = format;
      this.location = location;
      setType(type);
   }

   public Object clone()
   {
      return new IndexLocation(
        format == null ? null : (TeXObject)format.clone(),
        (TeXObject)location.clone(),
        type);
   }

   public void setType(byte type)
   {
      switch (type)
      {
         case NORMAL:
         case OPEN:
         case CLOSE:
            this.type = type;
         return;
      }

      throw new IllegalArgumentException("Invalid location type: "
       + type);
   }

   public byte getType()
   {
      return type;
   }

   public TeXObject getFormat()
   {
      return format;
   }

   public TeXObject getLocation()
   {
      return location;
   }

   public boolean equals(Object object)
   {
      if (object == null || !(object instanceof IndexLocation))
      {
         return false;
      }

      IndexLocation indexLoc = (IndexLocation)object;

      if (type != indexLoc.type) return false;

      if (format == null ^ indexLoc.format == null) return false;

      if (format != indexLoc.format // in case both are null
      || !format.equals(indexLoc.format))
      {
         return false;
      }

      if (location == null ^ indexLoc.location == null)
      {
         return false;
      }

      if (location == indexLoc.location) return true;

      return location.equals(indexLoc.location);
   }

   public void process(TeXParser parser) throws IOException
   {
      if (format == null)
      {
         location.process(parser);
         return;
      }

      if (location instanceof TeXObjectList
      && !(location instanceof Group))
      {
         Group group = parser.getListener().createGroup();

         group.addAll((TeXObjectList)location);

         parser.push(group);
      }
      else
      {
         parser.push(location);
      }

      format.process(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (format == null)
      {
         location.process(parser, stack);
         return;
      }

      if (location instanceof TeXObjectList
      && !(location instanceof Group))
      {
         Group group = parser.getListener().createGroup();

         group.addAll((TeXObjectList)location);

         stack.push(group);
      }
      else
      {
         stack.push(location);
      }

      format.process(parser, stack);
   }

   public boolean isPar()
   {
      return false;
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      if (format == null)
      {
         return location.string(parser);
      }

      TeXObjectList list = format.string(parser);
      list.add(parser.getListener().getOther(parser.getBgChar()));
      list.addAll(location.string(parser));
      list.add(parser.getListener().getOther(parser.getEgChar()));

      return list;
   }

   public String toString(TeXParser parser)
   {
      if (format == null)
      {
         return location.toString(parser);
      }

      return String.format("%s%s%s%s",
        format.toString(parser),
        new String(Character.toChars(parser.getBgChar())),
        location.toString(parser),
        new String(Character.toChars(parser.getEgChar())));
   }

   public String toString()
   {
      return String.format("IndexLocation[type=%d,format=%s,location=%s]",
        type, format.toString(), location.toString());
   }

   public String format()
   {
      if (format == null)
      {
         return location.format();
      }

      return String.format("%s{%s}",
        format.format(),
        location.format());
   }

   private byte type = NORMAL;
   private TeXObject format;
   private TeXObject location;

   public static final byte NORMAL=0, OPEN=1, CLOSE=2;
}
