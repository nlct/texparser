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

public class DTLformatlist extends Command
{
   public DTLformatlist()
   {
      this("DTLformatlist");
   }

   public DTLformatlist(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLformatlist(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar=parser.isNextChar('*', stack);

      CsvList csvList = CsvList.popCsvListFromStack(parser, stack, true);

      TeXObjectList expandedList = new TeXObjectList();
      AbstractTeXObjectList expanded;

      if (isStar)
      {
         expanded = expandedList;
      }
      else
      {
         expanded = parser.getListener().createGroup();
         expandedList.add(expanded);
      }

      boolean skipEmpty = parser.isControlSequenceTrue("ifDTLlistskipempty");

      int n;

      if (skipEmpty)
      {
         n = 0;

         for (int i = 0; i < csvList.size(); i++)
         {
            TeXObject obj = csvList.getValue(i);

            if (!obj.isEmptyObject())
            {
               n++;
            }
         }
      }
      else
      {
         n = csvList.size();
      }

      int j = 0;

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (!skipEmpty || !obj.isEmptyObject())
         {
            if (j == 0)
            {// no sep
            }
            else if (j == n-1)
            {
               if (n > 2)
               {
                  expanded.add(new TeXCsRef("DTLlistformatoxford"));
               }

               expanded.add(new TeXCsRef("DTLlistformatlastsep"));
            }
            else
            {
               expanded.add(new TeXCsRef("DTLlistformatsep"));
            }

            expanded.add(new TeXCsRef("DTLlistformatitem"));

            Group grp = parser.getListener().createGroup();
            grp.add(obj);
            expanded.add(grp);

            j++;
         }
      }

      return expandedList;
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
