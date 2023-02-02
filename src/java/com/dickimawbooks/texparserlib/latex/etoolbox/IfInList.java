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

public class IfInList extends AbstractEtoolBoxCommand
{
   public IfInList()
   {
      this("ifinlist", false, false);
   }

   public IfInList(String name, boolean expandItem, boolean isCsname)
   {
      super(name, isCsname);
      this.expandItem = expandItem;
   }

   @Override
   public Object clone()
   {
      return new IfInList(getName(), expandItem, isCsname);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject item;

      if (expandItem)
      {
         item = popArgExpandFully(parser, stack);
      }
      else
      {
         item = popArg(parser, stack);
      }

      ControlSequence cs = popCsArg(parser, stack);

      TeXObject defn = TeXParserUtils.resolve(cs, parser);

      if (defn instanceof GenericCommand)
      {
         defn = ((GenericCommand)defn).getDefinition();
      }
      else
      {
         defn = TeXParserUtils.expandOnce(defn, parser, stack);
      }

      if (parser.isStack(defn) && ((TeXObjectList)defn).size() == 1)
      {
         defn = ((TeXObjectList)defn).firstElement();
      }

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      boolean cond = false;

      String itemStr = item.toString(parser);

      if (!itemStr.isEmpty())
      {
         if (defn instanceof EtoolboxList)
         {
            EtoolboxList list = (EtoolboxList)defn;

            for (int i = 0; i < list.size(); i++)
            {
               if (itemStr.equals(list.get(i).toString(parser)))
               {
                  cond = true;
                  break;
               }
            }
         }
         else
         {
            String[] list = defn.toString(parser).split("|");

            for (String itm : list)
            {
               if (itm.equals(itemStr))
               {
                  cond = true;
                  break;
               }
            }
         }
      }

      TeXObject obj = (cond ? truePart : falsePart);

      if (parser.isStack(obj))
      {
         return (TeXObjectList)obj;
      }
      else
      {
         TeXObjectList expanded = parser.getListener().createStack();
         expanded.add(obj);

         return expanded;
      }
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject item;

      if (expandItem)
      {
         item = popArgExpandFully(parser, stack);
      }
      else
      {
         item = popArg(parser, stack);
      }

      ControlSequence cs = popCsArg(parser, stack);

      TeXObject defn = TeXParserUtils.resolve(cs, parser);

      if (defn instanceof GenericCommand)
      {
         defn = ((GenericCommand)defn).getDefinition();
      }
      else
      {
         defn = TeXParserUtils.expandOnce(defn, parser, stack);
      }

      if (parser.isStack(defn) && ((TeXObjectList)defn).size() == 1)
      {
         defn = ((TeXObjectList)defn).firstElement();
      }

      TeXObject truePart = popArg(parser, stack);
      TeXObject falsePart = popArg(parser, stack);

      boolean cond = false;

      String itemStr = item.toString(parser);

      if (!itemStr.isEmpty())
      {
         if (defn instanceof EtoolboxList)
         {
            EtoolboxList list = (EtoolboxList)defn;

            for (int i = 0; i < list.size(); i++)
            {
               if (itemStr.equals(list.get(i).toString(parser)))
               {
                  cond = true;
                  break;
               }
            }
         }
         else
         {
            cond = defn.toString(parser).equals(itemStr);
         }
      }

      TeXObject obj = (cond ? truePart : falsePart);

      TeXParserUtils.process(obj, parser, stack);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean expandItem;
}
