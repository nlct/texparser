/*
    Copyright (C) 2018 Nicola L.C. Talbot
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

   public Object clone()
   {
      return new DTLformatlist(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject next = (parser == stack ? parser.popStack() : stack.pop());

      boolean isStar=false;

      if (next != null && next instanceof CharObject
       && ((CharObject)next).getCharCode() == (int)'*')
      {
         isStar = true;
      }
      else
      {
         stack.push(next);
      }

      TeXObject list;

      if (parser == stack)
      {
         list = parser.popNextArg();
      }
      else
      {
         list = stack.popArg(parser);
      }

      CsvList csvList = null;

      if (list instanceof CsvList)
      {
         csvList = (CsvList)list;
      }
      else if (list instanceof TeXObjectList
         && ((TeXObjectList)list).size() == 0
         && ((TeXObjectList)list).firstElement() instanceof CsvList)
      {
         csvList = (CsvList)((TeXObjectList)list).firstElement();
      }
      else if (list instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)list).expandonce(parser);
         }
         else
         {
            expanded = ((Expandable)list).expandonce(parser, stack);
         }

         if (expanded != null)
         {
            list = expanded;
         }

         if (list instanceof TeXObjectList
            && ((TeXObjectList)list).size() == 0
            && ((TeXObjectList)list).firstElement() instanceof CsvList)
         {
            csvList = (CsvList)((TeXObjectList)list).firstElement();
         }
      }

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, list);
      }

      TeXObjectList expandedList = new TeXObjectList();
      TeXObjectList expanded;

      if (isStar)
      {
         expanded = expandedList;
      }
      else
      {
         expanded = parser.getListener().createGroup();
         expandedList.add(expanded);
      }

      ControlSequence ifCs = parser.getControlSequence("ifDTLlistskipempty");
      boolean skipEmpty = (ifCs instanceof IfTrue);

      int n;

      if (skipEmpty)
      {
         n = 0;

         for (int i = 0; i < csvList.size(); i++)
         {
            TeXObject obj = csvList.getValue(i);

            if (!(obj instanceof TeXObjectList 
                   && ((TeXObjectList)obj).size() == 0))
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

         if (!skipEmpty || !(obj instanceof TeXObjectList 
                && ((TeXObjectList)obj).size() == 0))
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

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }


}
