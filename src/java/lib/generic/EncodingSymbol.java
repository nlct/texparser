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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class EncodingSymbol extends ControlSequence
  implements Expandable,CaseChangeable
{
   public EncodingSymbol(String name, int code, FontEncoding enc)
   {
      super(name);
      this.value = enc == null ? new String(Character.toChars(code)) 
        : enc.getCharString(code);
      this.code = code;
   }

   public EncodingSymbol(String name, int code, String value)
   {
      super(name);
      this.value = value;
      this.code = code;
   }

   public Object clone()
   {
      return new EncodingSymbol(getName(), code, value);
   }

   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (other == null || !(other instanceof EncodingSymbol)) return false;

      return code == ((EncodingSymbol)other).code;
   }

   public TeXObject toLowerCase(TeXParser parser)
   {
      return parser.getListener().createString(value.toLowerCase());
   }

   public TeXObject toUpperCase(TeXParser parser)
   {
      return parser.getListener().createString(value.toUpperCase());
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return parser.getListener().createString(value);
   }

   public TeXObjectList expandonce(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser,
      TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(value);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      process(parser);
   }

   private String value;
   private int code;
}
