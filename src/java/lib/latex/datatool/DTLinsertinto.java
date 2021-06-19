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

public class DTLinsertinto extends ControlSequence
{
   public DTLinsertinto(DataToolBaseSty sty)
   {
      this("dtlinsertinto", false, sty);
   }

   public DTLinsertinto(String name, boolean expandElement, DataToolBaseSty sty)
   {
      super(name);
      this.sty = sty;
      this.expandElement = expandElement;
   }

   @Override
   public Object clone()
   {
      return new DTLinsertinto(getName(), expandElement, sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject element;

      if (expandElement)
      {
         element = parser.popRequiredExpandOnce(stack);
      }
      else
      {
         element = parser.popRequired(stack);
      }

      ControlSequence cs = parser.popRequiredControlSequence(stack, true);
      ControlSequence criteria = parser.popRequiredControlSequence(stack, true);

      TeXObjectList list;
      CsvList csvList = null;

      if (cs instanceof GenericCommand)
      {
         list = ((GenericCommand)cs).getDefinition();

         csvList = (CsvList)list.toObject(CsvList.class);
      }

      if (csvList == null)
      {
         TeXObject expanded = parser.expandOnce(cs, stack);

         if (expanded instanceof TeXObjectList)
         {
            list = (TeXObjectList)expanded;
         }
         else
         {
            list = new TeXObjectList();
         }

         csvList = CsvList.getList(parser, list);
         list = new TeXObjectList();
         list.add(csvList);

         cs = new GenericCommand(true, cs.getName(), null, list);
         parser.putControlSequence(true, cs);
      }

      CountRegister reg = sty.getSortCountRegister();

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         list = new TeXObjectList(4);
         list.add(criteria);
         list.add(reg);
         list.add(obj);
         list.add(element);

         if (parser == stack)
         {
            list.process(parser);
         }
         else
         {
            list.process(parser, stack);
         }

         if (reg.number(parser) > 0)
         {
            csvList.insertElementAt(element, i);
            return;
         }
      }

      csvList.add(element);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected DataToolBaseSty sty;
   private boolean expandElement;
}
