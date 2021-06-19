/*
    Copyright (C) 2018-20 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.latex.*;

public class DTLlistelement extends Command
{
   public DTLlistelement()
   {
      this("DTLlistelement");
   }

   public DTLlistelement(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLlistelement(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      CsvList csvList = CsvList.popCsvListFromStack(parser, stack, true);

      Numerical num = parser.popRequiredNumerical(stack);

      int n = num.number(parser);

      TeXObjectList result = new TeXObjectList();

      boolean skipEmpty = parser.isControlSequenceTrue("ifDTLlistskipempty");

      int j = 0;

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (!skipEmpty || !obj.isEmptyObject())
         {
            j++;

            if (j == n)
            {
               result.add(obj);
               break;
            }
         }
      }

      if (result.isEmpty())
      {
         TeXApp texApp = parser.getListener().getTeXApp();
         texApp.warning(parser, 
           texApp.getMessage(DataToolBaseSty.INDEX_OUT_OF_RANGE, n));
      }

      return result;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }
}
