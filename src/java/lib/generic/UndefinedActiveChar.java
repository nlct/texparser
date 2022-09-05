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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class UndefinedActiveChar extends ActiveChar
{
   public UndefinedActiveChar(int code)
   {
      this(code, UndefAction.ERROR);
   }

   public UndefinedActiveChar(int code, UndefAction action)
   {
      charCode = code;
      this.action = action;
   }

   @Override
   public TeXObject clone()
   {
      return new UndefinedActiveChar(charCode, action);
   }

   public int getCharCode()
   {
      return charCode;
   }
   
   @Override
   public String toString(TeXParser parser)
   {
      return format();
   }

   @Override
   public String format()
   {
      return new String(Character.toChars(charCode));
   }

   protected void errOrMsg(TeXParser parser) throws TeXSyntaxException
   {
      switch (action)
      {
         case ERROR:
            throw new TeXSyntaxException(parser, 
              TeXSyntaxException.ERROR_UNDEFINED_CHAR,
               new String(Character.toChars(charCode)));
         case WARN:
            parser.warningMessage(TeXSyntaxException.ERROR_UNDEFINED_CHAR,
               new String(Character.toChars(charCode)));
         break;
         case MESSAGE:
            parser.message(TeXSyntaxException.ERROR_UNDEFINED_CHAR,
               new String(Character.toChars(charCode)));
         break;
      }
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      errOrMsg(parser);

      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));
      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }
   public void process(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      errOrMsg(parser);

      parser.getListener().getWriteable().writeCodePoint(charCode);
   }

   private int charCode;
   private UndefAction action;
}

