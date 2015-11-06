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

public class MultiColumn extends MultiCell
{
   public MultiColumn()
   {
      this("multicolumn");
   }

   public MultiColumn(String name)
   {
      super(name);
   }

   public Object clone()
   {
      MultiColumn object = new MultiColumn(getName());

      object.setColumnSpan(getColumnSpan());
      object.setRowSpan(getRowSpan());
      object.setAlignment(getAlignment());

      return object;
   }

   protected MultiCell createMultiCell(TeXParser parser,
     int numCols, TeXObject colAlignArg)
   throws IOException
   {
      return new MultiCell(getName(), numCols, 1, parser, colAlignArg);
   }  

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject numColsArg = stack.popArg(parser);

      if (numColsArg instanceof Expandable)
      {
         TeXObjectList expanded = 
            ((Expandable)numColsArg).expandfully(parser, stack);

         if (expanded != null)
         {
            numColsArg = expanded;
         }
      }

      int numCols;
      String str = numColsArg.toString(parser);

      try
      {
         numCols = Integer.parseInt(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }

      TeXObject colAlignArg = stack.popArg(parser);

      if (colAlignArg instanceof Expandable)
      {
         TeXObjectList expanded = 
            ((Expandable)colAlignArg).expandfully(parser, stack);

         if (expanded != null)
         {
            colAlignArg = expanded;
         }
      }

      TeXObject contents = stack.popArg(parser);

      TeXObjectList expanded = new TeXObjectList();

      expanded.add(createMultiCell(parser, numCols, colAlignArg));
      expanded.add(contents);

      return expanded;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObject numColsArg = parser.popNextArg();

      if (numColsArg instanceof Expandable)
      {
         TeXObjectList expanded = 
            ((Expandable)numColsArg).expandfully(parser);

         if (expanded != null)
         {
            numColsArg = expanded;
         }
      }

      int numCols;
      String str = numColsArg.toString(parser);

      try
      {
         numCols = Integer.parseInt(str);
      }
      catch (NumberFormatException e)
      {
         throw new TeXSyntaxException(parser,
           TeXSyntaxException.ERROR_NUMBER_EXPECTED, str);
      }

      TeXObject colAlignArg = parser.popNextArg();

      if (colAlignArg instanceof Expandable)
      {
         TeXObjectList expanded = 
            ((Expandable)colAlignArg).expandfully(parser);

         if (expanded != null)
         {
            colAlignArg = expanded;
         }
      }

      TeXObject contents = parser.popNextArg();

      TeXObjectList expanded = new TeXObjectList();

      expanded.add(createMultiCell(parser, numCols, colAlignArg));
      expanded.add(contents);

      return expanded;
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      expandonce(parser, stack).process(parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      expandonce(parser).process(parser);
   }

}
