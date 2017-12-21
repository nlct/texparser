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

public class DataToolEntry implements TeXObject
{
   public DataToolEntry(DataToolSty sty, int column)
   {
      this(sty, column, new TeXObjectList());
   }

   public DataToolEntry(DataToolSty sty, int column, 
      TeXObject contents)
   {
      this.sty = sty;
      setColumnIndex(column);
      setContents(contents);
   }

   public Object clone()
   {
      return new DataToolEntry(sty, column, (TeXObject)contents.clone());
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

   public TeXObject getContents()
   {
      return contents;
   }

   public void setContents(TeXObject contents)
   {
      if (contents instanceof DataElement)
      {
         this.contents = contents;
      }
      else
      {
         try
         {
            DataElement element = sty.getElement(contents);

            if (element == null)
            {
               this.contents = contents;
            }
            else
            {
               this.contents = element;
            }
         }
         catch (IOException e)
         {
            this.contents = contents;
         }
      }
   }

   public static DataToolEntry toEntry(TeXParser parser, TeXObjectList stack,
     DataToolSty sty)
      throws IOException
   {
      TeXObject object = stack.peekStack();

      if (object instanceof DataToolEntry)
      {
         return (DataToolEntry)stack.popToken();
      }

      if (object instanceof ControlSequence
          && ((ControlSequence)object).getName().equals("db@row@id@w"))
      {
         return null;
      }

      if (!stack.popCsMarker(parser, "db@col@id@w"))
      {
         return null;
      }

      Numerical number = stack.popNumerical(parser);

      int columnIndex = number.number(parser);

      stack.popCsMarker(parser, "db@col@id@end@");

      stack.popCsMarker(parser, "db@col@elt@w");

      object = stack.popToCsMarker(parser, "db@col@elt@end@",
        (byte)(TeXObjectList.POP_RETAIN_IGNOREABLES
               | TeXObjectList.POP_IGNORE_LEADING_SPACE));

      if (sty.isExpansionOn() && object instanceof Expandable)
      {
         TeXObjectList expanded;

         if (stack == parser)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            object = expanded;
         }
      }

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

      try
      {
         return new DataToolEntry(sty, columnIndex, object);
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

      list.add(new TeXCsRef("db@col@id@w"));
      list.add(new UserNumber(column));
      list.add(new TeXCsRef("db@col@id@end@"));

      list.add(new TeXCsRef("db@col@elt@w"));
      list.add((TeXObject)contents.clone());
      list.add(new TeXCsRef("db@col@elt@end@"));

      list.add(new TeXCsRef("db@col@id@w"));
      list.add(new UserNumber(column));
      list.add(new TeXCsRef("db@col@id@end@"));

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
      if (obj == null || !(obj instanceof DataToolEntry))
      {
         return false;
      }

      DataToolEntry entry = (DataToolEntry)obj;

      if (entry.column != column)
      {
         return false;
      }

      return contents.equals(entry.contents);
   }

   public String toString()
   {
      return String.format("%s[column=%d,contents=%s]",
        getClass().getSimpleName(), column, contents);
   }

   private DataToolSty sty;

   private int column;
   private TeXObject contents;
}
