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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataToolHeader implements TeXObject
{
   public DataToolHeader(DataToolSty sty, int column, String key)
   {
      this(sty, column, key, TYPE_UNDEF, null);
   }

   public DataToolHeader(DataToolSty sty,
     int column, String key, byte type, TeXObject title)
   {
      this.sty = sty;
      setColumnIndex(column);
      setColumnLabel(key);
      setType(type);
      setTitle(title);
   }

   public Object clone()
   {
      return new DataToolHeader(sty, column, key, type, 
         title == null ? null : (TeXObject)title.clone());
   }

   public int getColumnIndex()
   {
      return column;
   }

   public void setColumnIndex(int column)
   {
      if (column < 1)
      {
         throw new IllegalArgumentException("Invalid column index "+column);
      }

      this.column = column;
   }

   public String getColumnLabel()
   {
      return key;
   }

   public void setColumnLabel(String key)
   {
      this.key = key;
   }

   public byte getType()
   {
      return type;
   }

   public void setType(byte newType)
   {
      switch (newType)
      {
         case TYPE_UNDEF:
         case TYPE_STRING:
         case TYPE_INT:
         case TYPE_REAL:
         case TYPE_CURRENCY:
            this.type = newType;
         break;
         default:
            throw new IllegalArgumentException("Invalid data type "+newType);
      }
   }

   public void updateType(DataElement element)
   {
      byte newType = element.getDataType();

      switch (type)
      {
         case TYPE_UNDEF:
         case TYPE_INT:
           type = newType;
         break;
         case TYPE_REAL:
           if (newType != TYPE_INT)
           {
              type = newType;
           }
         break;
         case TYPE_CURRENCY:
           if (newType == TYPE_STRING)
           {
              type = newType;
           }
         break;
      }
   }

   public TeXObject getTitle()
   {
      return title;
   }

   public void setTitle(TeXObject title)
   {
      this.title = title;
   }

   public static DataToolHeader popHeader(TeXParser parser, TeXObjectList stack,
     DataToolSty sty)
      throws IOException
   {
      TeXObject token = stack.peekStack(TeXObjectList.POP_IGNORE_LEADING_SPACE);

      if (token == null)
      {
         return null;
      }

      if (token instanceof DataToolHeader)
      {
         return (DataToolHeader)stack.popToken(
            TeXObjectList.POP_IGNORE_LEADING_SPACE);
      }

      if (!stack.popCsMarker(parser, "db@plist@elt@w"))
      {
         return null;
      }

      stack.popCsMarker(parser, "db@col@id@w");

      Numerical number = stack.popNumerical(parser);

      int columnIndex = number.number(parser);

      stack.popCsMarker(parser, "db@col@id@end@");

      stack.popCsMarker(parser, "db@key@id@w");

      TeXObjectList list = stack.popToCsMarker(parser, "db@key@id@end@");

      String keyStr = list.toString(parser);

      stack.popCsMarker(parser, "db@type@id@w");

      list = stack.popToCsMarker(parser, "db@type@id@end@");

      String dataType = list.toString(parser).trim();

      byte type;

      if (dataType.isEmpty())
      {
         type = TYPE_UNDEF;
      } 
      else
      {
         try
         {
            type = Byte.parseByte(dataType);
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(parser, 
             TeXSyntaxException.ERROR_NUMBER_EXPECTED, dataType);
         }
      }

      stack.popCsMarker(parser, "db@header@id@w");

      list = stack.popToCsMarker(parser, "db@header@id@end@");

      stack.popCsMarker(parser, "db@col@id@w");

      number = stack.popNumerical(parser);

      stack.popCsMarker(parser, "db@col@id@end@");

      int n = number.number(parser);

      if (columnIndex != n)
      {
         throw new LaTeXSyntaxException(parser,
            DataToolSty.ERROR_MISMATCHED, 
            String.format("\\db@col@id@w %d\\db@col@id@end@", columnIndex),
            String.format("\\db@col@id@w %d\\db@col@id@end@", n)
          );
      }

      stack.popCsMarker(parser, "db@plist@elt@end@");

      try
      {
         return new DataToolHeader(sty, columnIndex, keyStr, type, list);
      }
      catch (IllegalArgumentException e)
      {
         throw new LaTeXSyntaxException(e, parser, 
           LaTeXSyntaxException.PACKAGE_ERROR, e.getMessage());
      }
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      list.add(new TeXCsRef("db@plist@elt@w"));

      list.add(new TeXCsRef("db@col@id@w"));
      list.add(new UserNumber(column));
      list.add(new TeXCsRef("db@col@id@end@"));

      list.add(new TeXCsRef("db@key@id@w"));
      list.add(parser.getListener().createString(key));
      list.add(new TeXCsRef("db@key@id@end@"));

      list.add(new TeXCsRef("db@type@id@w"));

      if (type != TYPE_UNDEF)
      {
         list.add(new UserNumber(type));
      }

      list.add(new TeXCsRef("db@type@id@end@"));

      list.add(new TeXCsRef("db@header@id@w"));

      if (title == null)
      {
         list.add(parser.getListener().createString(key));
      }
      else
      {
         list.add((TeXObject)title.clone());
      }

      list.add(new TeXCsRef("db@header@id@end@"));

      list.add(new TeXCsRef("db@col@id@w"));
      list.add(new UserNumber(column));
      list.add(new TeXCsRef("db@col@id@end@"));

      list.add(new TeXCsRef("db@plist@elt@end@"));

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.addAll(0, expandonce(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      stack.addAll(0, expandonce(parser, stack));
   }

   public String toString(TeXParser parser)
   {
      try
      {
         return expandonce(parser).toString(parser);
      }
      catch (IOException e)
      {
         return "";
      }
   }

   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return expandonce(parser).string(parser);
   }

   public String format()
   {
      try
      {
         return expandonce(sty.getListener().getParser()).format();
      }
      catch (IOException e)
      {
         return "";
      }
   }

   public boolean isPar()
   {
      return false;
   }

   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof DataToolHeader))
      {
         return false;
      }

      DataToolHeader header = (DataToolHeader)obj;

      if (column != header.column || type != header.type)
      {
         return false;
      }

      if (!key.equals(header.key))
      {
         return false;
      }

      if (title != null && header.title != null
          && title.equals(header.title))
      {
         return true;
      }

      return title == header.title;
   }

   private DataToolSty sty;

   private int column;
   private String key;
   private byte type;
   private TeXObject title;

   public static final byte TYPE_UNDEF=-1;
   public static final byte TYPE_STRING=0;
   public static final byte TYPE_INT=1;
   public static final byte TYPE_REAL=2;
   public static final byte TYPE_CURRENCY=3;

}
