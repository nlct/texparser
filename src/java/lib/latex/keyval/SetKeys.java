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
package com.dickimawbooks.texparserlib.latex.keyval;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class SetKeys extends ControlSequence
{
   public SetKeys()
   {
      this("setkeys");
   }

   public SetKeys(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new SetKeys(getName());
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = false;

      TeXParserListener listener = parser.getListener();

      TeXObject obj = (stack == null ? parser.peekStack() : stack.peekStack());

      if (obj instanceof CharObject && ((CharObject)obj).getCharCode() == (int)'*')
      {
         isStar = true;

         if (parser == stack || stack == null)
         {
            parser.popStack();
         }
         else
         {
            stack.popStack(parser);
         }
      }

      String prefix = popOptLabelString(parser, stack);

      if (prefix == null)
      {
         prefix = "KV";
      }

      String familyArg = popLabelString(parser, stack);

      String exclusions = popOptLabelString(parser, stack);

      String[] excList = null;

      if (exclusions != null)
      {
         excList = exclusions.trim().split(" *,[ ,]*");
      }

      TeXObject optionsArg = popArg(parser, stack);

      if (optionsArg instanceof TeXObjectList 
           && ((TeXObjectList)optionsArg).isEmpty())
      {
         return;
      }

      KeyValList keyvalList;

      if (optionsArg instanceof KeyValList)
      {
         keyvalList = (KeyValList)optionsArg;
      }
      else
      {
         keyvalList = KeyValList.getList(parser, optionsArg);
      }

      TeXObjectList list = new TeXObjectList();

      String[] families = familyArg.trim().split(" *,[ ,]*");

      String remaining = null;

      for (Iterator<String> it=keyvalList.keySet().iterator(); it.hasNext(); )
      {
         String key = it.next();

         if (excList != null && Arrays.binarySearch(excList, key) >= 0)
         {
            continue;
         }

         TeXObject val = keyvalList.getValue(key);

         ControlSequence cs = null;
         String keycs = "";

         for (String family : families)
         {
            keycs = prefix+"@"+family+"@"+key;

            cs = parser.getControlSequence(keycs);

            if (cs != null)
            {
               break;
            }
         }

         if (cs == null)
         {
            if (isStar)
            {
               if (remaining == null)
               {
                  remaining = key;
               }
               else
               {
                  remaining += "," + key;
               }
            }
            else
            {// TODO warn or error?
            }
         }
         else
         {
            list.add(cs);

            if (val == null || val instanceof MissingValue)
            {
               list.add(new TeXCsRef(keycs+"@default"));
            }
            else
            {
               Group grp = listener.createGroup();
               list.add(grp);

               grp.add(val);
            }
         }
      }

      if (isStar)
      {
         parser.putControlSequence(true, 
           new GenericCommand("XKV@rm", null, 
            listener.createString(remaining==null?"":remaining)));
      }

      if (stack == null)
      {
         parser.addAll(0, list);
      }
      else
      {
         stack.addAll(0, list);
      }
   }

}
