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
      TeXObject element = popArg(parser, stack);

      if (expandElement)
      {
         element = TeXParserUtils.expandOnce(element, parser, stack);
      }

      ControlSequence cs = stack.popControlSequence(parser);
      TeXObject criteria = stack.popControlSequence(parser);

      String csname = cs.getName();

      TeXObject csArg = TeXParserUtils.resolve(cs, parser);
      criteria = TeXParserUtils.resolve(criteria, parser);

      CsvList csvList = null;

      if (csArg instanceof GenericCommand)
      {
         TeXObjectList list = ((GenericCommand)csArg).getDefinition();

         if (list.size() == 1 
             && list.firstElement() instanceof CsvList)
         {
            csvList = (CsvList)list.firstElement();
         }
      }

      if (csvList == null)
      {
         TeXObject expanded = TeXParserUtils.expandOnce(csArg, parser, stack);

         csvList = CsvList.getList(parser, expanded);
         TeXObjectList list = parser.getListener().createStack();
         list.add(csvList);

         cs = new GenericCommand(true, csname, null, list);
         parser.putControlSequence(true, cs);
      }

      CountRegister reg = sty.getSortCountRegister();

      for (int i = 0; i < csvList.size(); i++)
      {
         TeXObject obj = csvList.getValue(i);

         TeXObjectList list = new TeXObjectList(4);
         list.add(criteria);
         list.add(reg);
         list.add(obj);
         list.add(element);

         TeXParserUtils.process(list, parser, stack);

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
