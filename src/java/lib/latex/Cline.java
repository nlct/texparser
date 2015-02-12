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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Cline extends AlignSpan implements Expandable
{
   public Cline()
   {
      this("cline");
   }

   public Cline(String name)
   {
      this(name, -1, -1);
   }

   public Cline(String name, int firstColumn, int lastColumn)
   {
      super(name);
      setFirstColumn(firstColumn);
      setLastColumn(lastColumn);
   }

   public Object clone()
   {
      return new Cline(getName(), firstColumn(), lastColumn());
   }

   public int firstColumn()
   {
      return firstCol;
   }

   public int lastColumn()
   {
      return lastCol;
   }

   public void setFirstColumn(int firstCol)
   {
      this.firstCol = firstCol;
   }

   public void setLastColumn(int lastCol)
   {
      this.lastCol = lastCol;
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

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject arg = parser == stack ? parser.popNextArg() 
        : stack.popArg(parser);

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

      String str = arg.toString(parser);

      String[] split = str.split("-");

      if (split.length != 2)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_SYNTAX, str);
      }

      TeXObjectList list = new TeXObjectList();

      try
      {
         setFirstColumn(Integer.parseInt(split[0]));
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, split[0]);
      }

      try
      {
         setLastColumn(Integer.parseInt(split[1]));
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser, 
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, split[1]);
      }

      list.add(this);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      throw new TeXSyntaxException(parser, 
        TeXSyntaxException.ERROR_MISPLACED_OMIT);
   }

   public void process(TeXParser parser)
   throws IOException
   {
      throw new TeXSyntaxException(parser, 
        TeXSyntaxException.ERROR_MISPLACED_OMIT);
   }

   private int firstCol=-1, lastCol=-1;
}
