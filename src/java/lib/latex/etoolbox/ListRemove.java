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

public class ListRemove extends ControlSequence
{
   public ListRemove()
   {
      this("listremove", false, false);
   }

   public ListRemove(String name, boolean isGlobal, boolean isCsname)
   {
      super(name);
      this.isGlobal = isGlobal;
      this.isCsname = isCsname;
   }

   @Override
   public Object clone()
   {
      return new ListRemove(getName(), isGlobal, isCsname);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs;
      String csname;

      if (isCsname)
      {
         csname = popLabelString(parser, stack);

         cs = parser.getListener().getControlSequence(csname);
      }
      else
      {
         cs = popControlSequence(parser, stack);

         csname = cs.getName();

         if (cs instanceof TeXCsRef)
         {
            cs = parser.getListener().getControlSequence(csname);
         }
      }

      TeXObject defn = cs;

      if (cs instanceof AssignedControlSequence)
      {
         defn = ((AssignedControlSequence)cs).getBaseUnderlying();
      }

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

      TeXObject item = popArg(parser, stack);

      String itemStr = item.toString(parser);

      if (!itemStr.isEmpty())
      {
         EtoolboxList newList = new EtoolboxList();

         if (defn instanceof EtoolboxList)
         {
            EtoolboxList orgList = (EtoolboxList)defn;

            for (int i = 0; i < orgList.size(); i++)
            {
               TeXObject currItem = orgList.get(i);

               if (!itemStr.equals(currItem))
               {
                  newList.add((TeXObject)currItem.clone());
               }
            }
         }
         else if (!defn.toString(parser).equals(itemStr))
         {
            newList.add((TeXObject)defn.clone());
         }

         parser.putControlSequence(!isGlobal,
           new GenericCommand(true, csname, null, newList));
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean isGlobal, isCsname;
}
