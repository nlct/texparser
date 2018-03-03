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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class MathBg extends BgChar implements Expandable
{
   public MathBg(boolean isinline)
   {
      this('$', isinline);
   }

   public MathBg(char c, boolean isinline)
   {
      this((int)c, isinline);
   }

   public MathBg(int code, boolean isinline)
   {
      super(code);
      this.isinline = isinline;
   }

   public Object clone()
   {
      return new MathBg(getCharCode(), isInLine());
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList(1);
      list.add(this);
      parser.startGroup();
      parser.getSettings().setMode(
        isInLine() ? TeXSettings.MODE_INLINE_MATH : TeXSettings.MODE_DISPLAY_MATH);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   public String format()
   {
      String charStr = new String(Character.toChars(getCharCode()));

      return isInLine() ?  charStr : String.format("%s%s", charStr, charStr);
   }

   public String toString()
   {
      return String.format("%s[delim=%s]",
       getClass().getName(), format());
   }

   public String toString(TeXParser parser)
   {
      return parser.getMathDelim(isInLine());
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(getCharCode()));

      if (!isinline)
      {
         list.add(parser.getListener().getOther(getCharCode()));
      }

      return list;
   }


   public String show(TeXParser parser)
    throws IOException
   {
      return String.format("math character %s", 
       new String(Character.toChars(getCharCode())));
   }

   public boolean isInLine()
   {
      return isinline;
   }

   public Group createGroup(TeXParser parser)
   {
      MathGroup group = parser.getListener().createMathGroup();
      group.setInLine(isInLine());
      return group;
   }

   private boolean isinline = true;
}

