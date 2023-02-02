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

public class DTLfetchlistelement extends ControlSequence
{
   public DTLfetchlistelement()
   {
      this("DTLfetchlistelement");
   }

   public DTLfetchlistelement(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DTLfetchlistelement(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
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

      Numerical num = stack.popNumericalArg(parser);

      int n = num.number(parser);

      ControlSequence cmd = stack.popControlSequence(parser);

      String csName = cmd.getName();

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

      cmd = null;

      int j = 0;

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (!skipEmpty || !(obj instanceof TeXObjectList 
                && ((TeXObjectList)obj).size() == 0))
         {
            j++;

            if (j == n)
            {
               cmd = new GenericCommand(true, csName, null, 
                  new TeXObject[] {obj});
               break;
            }
         }
      }

      if (cmd == null)
      {
         TeXApp texApp = parser.getListener().getTeXApp();
         texApp.warning(parser, 
           texApp.getMessage(DataToolBaseSty.INDEX_OUT_OF_RANGE, n));
      }
      else
      {
         parser.putControlSequence(true, cmd);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
