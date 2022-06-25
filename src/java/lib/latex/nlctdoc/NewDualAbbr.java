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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class NewDualAbbr extends Command
{
   public NewDualAbbr()
   {
      this("bibglsnewdualindexabbreviationsecondary");
   }

   public NewDualAbbr(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new NewDualAbbr(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String label = popLabelString(parser, stack);
      KeyValList keyValList = KeyValList.getList(parser, popArg(parser, stack));
      popArg(parser, stack);// ignore primary label
      TeXObject shortArg = popArg(parser, stack);
      TeXObject longArg = popArg(parser, stack);
      TeXObject descArg = popArg(parser, stack);

      if (descArg.isEmpty())
      {
         keyValList.put("type", 
           parser.getListener().getControlSequence("glsxtrabbrvtype"));
      }
      else
      {
         keyValList.put("type", 
           parser.getListener().getControlSequence("glsdefaulttype"));
         keyValList.put("description", descArg);
      }

      TeXObjectList list = parser.getListener().createStack();

      list.add(parser.getListener().getControlSequence("newabbreviation"));
      list.add(parser.getListener().getOther('['));
      list.add(keyValList);
      list.add(parser.getListener().getOther(']'));
      list.add(parser.getListener().createGroup(label));

      Group grp = parser.getListener().createGroup();
      list.add(grp);

      grp.add(shortArg);

      grp = parser.getListener().createGroup();
      list.add(grp);

      grp.add(longArg);

      return list;
   }

}
