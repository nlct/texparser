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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class DefineKey extends ControlSequence
{
   public DefineKey()
   {
      this("define@key");
   }

   public DefineKey(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DefineKey(getName());
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String prefix = "KV";

      TeXObject prefixArg = null;

      if (parser == stack || stack == null)
      {
         prefixArg = parser.popNextArg('[', ']');
      }
      else
      {
         prefixArg = stack.popArg(parser, '[', ']');
      }

      if (prefixArg != null)
      {
         if (prefixArg instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack || stack == null)
            {
               expanded = ((Expandable)prefixArg).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)prefixArg).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               prefixArg = expanded;
            }
         }

         prefix = prefixArg.toString(parser);
      }

      TeXObject familyArg;

      if (parser == stack || stack == null)
      {
         familyArg = parser.popNextArg();
      }
      else
      {
         familyArg = stack.popArg(parser);
      }

      if (familyArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)familyArg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)familyArg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            familyArg = expanded;
         }
      }

      String family = familyArg.toString(parser);

      TeXObject keyArg;

      if (parser == stack || stack == null)
      {
         keyArg = parser.popNextArg();
      }
      else
      {
         keyArg = stack.popArg(parser);
      }

      if (keyArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack || stack == null)
         {
            expanded = ((Expandable)keyArg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)keyArg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            keyArg = expanded;
         }
      }

      String key = keyArg.toString(parser);

      TeXObject defValArg = null;

      if (parser == stack || stack == null)
      {
         defValArg = parser.popNextArg('[', ']');
      }
      else
      {
         defValArg = stack.popArg(parser, '[', ']');
      }

      TeXObject valueArg;

      if (parser == stack || stack == null)
      {
         valueArg = parser.popNextArg();
      }
      else
      {
         valueArg = stack.popArg(parser);
      }

      defineKey(parser, prefix, family, key, defValArg, valueArg);
   }

   public static void defineKey(TeXParser parser, String prefix, 
     String family, String key, TeXObject defVal, TeXObject csDef)
   {
      String csname = "";

      if (!prefix.isEmpty())
      {
         csname = prefix+"@";
      }

      if (!family.isEmpty())
      {
         csname += family+"@";
      }

      csname += key;

      if (defVal != null)
      {
         TeXObjectList defn = new TeXObjectList();
         defn.add(new TeXCsRef(csname));
         Group grp = parser.getListener().createGroup();
         defn.add(grp);
         grp.add(defVal);

         parser.putControlSequence(true, new GenericCommand(false,
            csname+"@default", null, defn));
      }

      TeXObjectList defList;

      if (csDef instanceof TeXObjectList)
      {
         defList = (TeXObjectList)csDef;
      }
      else
      {
         defList = new TeXObjectList();
         defList.add(csDef);
      }

      parser.putControlSequence(new GenericCommand(parser.getListener(), true,
        csname, 1, defList));
   }
}
