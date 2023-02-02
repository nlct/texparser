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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.util.Vector;

public class AssignedActiveChar extends ActiveChar implements AssignedMacro
{
   public AssignedActiveChar(int charCode, TeXObject underlying)
   {
      this(charCode, underlying, false);
   }

   public AssignedActiveChar(int charCode, TeXObject underlying, boolean isRobust)
   {
      this.charCode = charCode;
      this.underlying = underlying;

      if (underlying instanceof Macro)
      {
         Macro macro = (Macro)underlying;

         setAllowsPrefix(macro.getAllowsPrefix());
         setShort(macro.isShort());
         setSyntax(macro.getSyntax());
      }
   }

   @Override
   public int getCharCode()
   {
      return charCode;
   }

   @Override
   public Object clone()
   {
      return new AssignedActiveChar(getCharCode(), 
        (TeXObject)underlying.clone(), isRobust);
   }

   @Override
   public boolean isPar()
   {
      return underlying.isPar();
   }

   @Override
   public boolean isEmpty()
   {
      return underlying.isEmpty();
   }

   public void setPrefix(byte prefix)
   {
      if (underlying instanceof Macro)
      {
         Macro macro = (Macro)underlying;

         macro.setPrefix(prefix);
         super.setPrefix(prefix);
      }
   }

   public void clearPrefix()
   {
      if (underlying instanceof Macro)
      {
         ((Macro)underlying).clearPrefix();
         super.clearPrefix();
      }
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof TeXObject))
      {
         return false;
      }

      if (obj instanceof AssignedMacro)
      {
         return underlying.equals(((AssignedMacro)obj).getUnderlying());
      }

      return underlying.equals(obj);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      underlying.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      underlying.process(parser, stack);
   }

   @Override
   public boolean canExpand()
   {
      return !isRobust && underlying.canExpand();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      if (isRobust) return null;

      if (!(underlying instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)underlying).expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isRobust) return null;

      if (!(underlying instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)underlying).expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      if (isRobust) return null;

      if (!(underlying instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)underlying).expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isRobust) return null;

      if (!(underlying instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)underlying).expandfully(parser, stack);
   }

   @Override
   public String toString(TeXParser parser)
   {
      return new String(Character.toChars(charCode));
   }

   @Override
   public String toString()
   {
      return String.format("%s[cp=%d,char=%s,robust=%s,underlying=%s]", 
        getClass().getSimpleName(),
        charCode, new String(Character.toChars(charCode)), isRobust,
        underlying);
   }

   @Override
   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(parser.getListener().getOther(charCode));
      return list;
   }

   @Override
   public TeXObject getUnderlying()
   {
      return underlying;
   }

   @Override
   public TeXObject getBaseUnderlying()
   {
      if (underlying instanceof AssignedMacro)
      {
         return ((AssignedMacro)underlying).getBaseUnderlying();
      }

      return underlying;
   }

   @Override
   public TeXObject resolve(TeXParser parser)
   {
      TeXObject obj = getBaseUnderlying();

      if (obj instanceof Resolvable)
      {
         return ((Resolvable)obj).resolve(parser);
      }

      return obj;
   }

   private TeXObject underlying;
   private int charCode;
   protected boolean isRobust = false;
}
