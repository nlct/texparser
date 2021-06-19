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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLifinlist extends ControlSequence
{
   public DTLifinlist()
   {
      this("DTLifinlist");
   }

   public DTLifinlist(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLifinlist(getName());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject element = parser.popRequired(stack);

      String elementStr;

      if (element instanceof AbstractTeXObjectList)
      {
         elementStr = ((AbstractTeXObjectList)element).format(true);
      }
      else
      {
         elementStr = element.format();
      }

      CsvList csvList = CsvList.popCsvListFromStack(parser, stack, true);

      TeXObject truePart = parser.popRequired(stack);
      TeXObject falsePart = parser.popRequired(stack);

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         String objStr;

         if (obj instanceof AbstractTeXObjectList)
         {
            objStr = ((AbstractTeXObjectList)obj).format(true);
         }
         else
         {
            objStr = obj.format();
         }

         if (objStr.equals(elementStr))
         {
            if (stack == parser)
            {
               truePart.process(parser);
            }
            else
            {
               truePart.process(parser, stack);
            }
            return;
         }
      }

      falsePart.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
