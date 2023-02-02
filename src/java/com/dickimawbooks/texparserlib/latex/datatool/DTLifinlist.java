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

   public Object clone()
   {
      return new DTLifinlist(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject element = stack.popArg(parser);

      TeXObject list = stack.popArg(parser);

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
         TeXObjectList expanded = ((Expandable)list).expandonce(parser, stack);

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

      TeXObject truePart = stack.popArg(parser);
      TeXObject falsePart = stack.popArg(parser);

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, list);
      }

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (obj.equals(element))
         {
            truePart.process(parser, stack);
            return;
         }
      }

      falsePart.process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject element = parser.popNextArg();

      TeXObject list = parser.popNextArg();

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
         TeXObjectList expanded = ((Expandable)list).expandonce(parser);

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

      TeXObject truePart = parser.popNextArg();
      TeXObject falsePart = parser.popNextArg();

      if (csvList == null)
      {
         csvList = CsvList.getList(parser, list);
      }

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         if (obj.equals(element))
         {
            truePart.process(parser);
            return;
         }
      }

      falsePart.process(parser);
   }

}
