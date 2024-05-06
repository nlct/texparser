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

public class AssignedControlSequence extends Command
   implements AssignedMacro
{
   public AssignedControlSequence(String name, TeXObject underlying)
   {
      this(name, underlying, false);
   }

   public AssignedControlSequence(String name, TeXObject underlying, boolean isRobust)
   {
      super(name);

      if (underlying == null)
      {
         throw new NullPointerException("Underlying object is null");
      }

      this.underlying = underlying;
      this.isRobust = isRobust;

      if (underlying instanceof Macro)
      {
         Macro macro = (Macro)underlying;

         setAllowsPrefix(macro.getAllowsPrefix());
         setShort(macro.isShort());
         setSyntax(macro.getSyntax());
      }
   }

   @Override
   public Object clone()
   {
      return new AssignedControlSequence(getName(), 
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
         ((Macro)underlying).setPrefix(prefix);
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
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
      {
         parser.logMessage("PROCESSING "+toString());
      }

      underlying.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
      {
         parser.logMessage("PROCESSING "+toString());
      }

      underlying.process(parser, stack);
   }

   @Override
   public boolean canExpand()
   {
      return !isRobust && underlying.canExpand();
   }

   @Override
   public boolean isExpansionBlocker()
   {
      return underlying.isExpansionBlocker();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      if (isRobust) return null;

      TeXObject base = getBaseUnderlying();

      if (!(base instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)base).expandonce(parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isRobust) return null;

      TeXObject base = getBaseUnderlying();

      if (!(base instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)base).expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      if (isRobust) return null;

      TeXObject base = getBaseUnderlying();

      if (!(base instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)base).expandfully(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      if (isRobust) return null;

      TeXObject base = getBaseUnderlying();

      if (!(base instanceof Expandable))
      {
         return null;
      }

      return ((Expandable)base).expandfully(parser, stack);
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

   @Override
   public String toString()
   {
      return String.format("%s[name=%s,robust=%s,underlying=%s]", 
        getClass().getSimpleName(), getName(), isRobust, underlying);
   }

   private TeXObject underlying;
   protected boolean isRobust = false;
}
