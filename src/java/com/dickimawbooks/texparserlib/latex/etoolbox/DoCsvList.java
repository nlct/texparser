/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DoCsvList extends ControlSequence
{
   public DoCsvList()
   {
      this("dolistloop", true);
   }

   public DoCsvList(String name, boolean useDo)
   {
      super(name);
      this.useDo = useDo;
   }

   @Override
   public Object clone()
   {
      return new DoCsvList(getName(), useDo);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject handler;

      if (useDo)
      {
         handler = parser.getListener().getControlSequence("do");
      }
      else
      {
         handler = popArg(parser, stack);
      }

      TeXObject defn = popArg(parser, stack);

      defn = TeXParserUtils.resolve(defn, parser);

      if (defn instanceof GenericCommand)
      {
         defn = ((GenericCommand)defn).getDefinition();
      }
      else
      {
         defn = TeXParserUtils.expandOnce(defn, parser, stack);
      }

      if (defn instanceof TeXObjectList && ((TeXObjectList)defn).isStack()
          && ((TeXObjectList)defn).size() == 1)
      {
         defn = ((TeXObjectList)defn).firstElement();
      }

      CsvList csvlist;

      if (defn instanceof CsvList)
      {
         csvlist = (CsvList)defn;
      }
      else
      {
         csvlist = CsvList.getList(parser, defn);
      }

      for (int i = 0; i < csvlist.size(); i++)
      {
         TeXObjectList expanded = parser.getListener().createStack();

         expanded.add(handler);
         Group grp = parser.getListener().createGroup();
         expanded.add(grp);

         grp.add((TeXObject)csvlist.getValue(i).clone());

         TeXParserUtils.process(expanded, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean useDo;
}
