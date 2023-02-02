/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.primitives.Undefined;

public class PreTo extends AbstractEtoolBoxCommand
{
   public PreTo()
   {
      this("preto", false, true, false, false);
   }

   public PreTo(String name, boolean isGlobal, boolean isPre, 
      boolean expandCode, boolean isCsname)
   {
      this(name, isGlobal, isPre, expandCode, isCsname, false);
   }

   public PreTo(String name, boolean isGlobal, boolean isPre, 
      boolean expandCode, boolean isCsname, boolean isInternalList)
   {
      super(name, isCsname);
      this.isGlobal = isGlobal;
      this.isPre = isPre;
      this.expandCode = expandCode;
      this.isInternalList = isInternalList;
   }

   @Override
   public Object clone()
   {
      return new PreTo(getName(), isGlobal, isPre, expandCode, isCsname, isInternalList);
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = popCsArg(parser, stack);
      String csname = cs.getName();

      TeXObject code = popArg(parser, stack);

      if (expandCode)
      {
         code = TeXParserUtils.expandFully(code, parser, stack);
      }

      TeXObject hook = TeXParserUtils.resolve(cs, parser);

      if (cs instanceof Undefined)
      {
         if (isInternalList && !code.isEmpty())
         {
            EtoolboxList list = new EtoolboxList();
            list.add(code);
            cs = new GenericCommand(isShort, csname, null, list);
         }
         else
         {
            cs = new GenericCommand(isShort, csname, null, code);
         }

         parser.putControlSequence(!isGlobal, cs);

         return;
      }

      if (code.isEmpty())
      {
         return;
      }

      boolean isShort = cs.isShort();
      TeXObjectList syntax = cs.getSyntax();
      TeXObjectList defn;

      if (isInternalList)
      {
         defn = new EtoolboxList();
      }
      else
      {
         defn = parser.getListener().createStack();
      }

      if (isPre)
      {
         defn.add(code, true);
      }

      TeXObject origDef = hook;

      if (hook instanceof GenericCommand)
      {
         origDef = ((GenericCommand)hook).getDefinition();
      }
      else
      {
         origDef = TeXParserUtils.expandOnce(hook, parser, stack);
      }

      if (origDef instanceof TeXObjectList && ((TeXObjectList)origDef).isStack())
      {
         ((TeXObjectList)origDef).stripIgnoreables();

         if (isInternalList)
         {
            if (((TeXObjectList)origDef).size() == 1)
            {
               origDef = ((TeXObjectList)origDef).firstElement();
            }
         }
      }

      if (isInternalList)
      {
         if (origDef instanceof EtoolboxList)
         {
            defn.addAll((EtoolboxList)origDef);
         }
         else
         {
            defn.add(origDef);
         }
      }
      else
      {
         defn.add(origDef, true);
      }

      if (!isPre)
      {
         defn.add(code, true);
      }

      cs = new GenericCommand(isShort, csname, syntax, defn);

      parser.putControlSequence(!isGlobal, cs);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private boolean isGlobal, isPre, expandCode, isInternalList;
}
