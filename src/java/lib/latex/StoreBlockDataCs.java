/*
    Copyright (C) 2020 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class StoreBlockDataCs extends StoreDataCs
{
   public StoreBlockDataCs(String name)
   {
      super(name);

      type = name;
      optType = type;
   }

   public StoreBlockDataCs(String name, String internalName)
   {
      super(name, internalName);

      if (internalName.startsWith("@"))
      {
         type = internalName.substring(1);
      }
      else
      {
         type = name;
      }

      optType = type;
   }

   public StoreBlockDataCs(String name, String optInternalName, String internalName)
   {
      super(name, optInternalName, internalName);

      if (internalName.startsWith("@"))
      {
         type = internalName.substring(1);
      }
      else
      {
         type = name;
      }

      if (optInternalName == null)
      {
         optType = type;
      }
      else if (optInternalName.startsWith("@"))
      {
         optType = optInternalName.substring(1);
      }
      else
      {
         optType = optInternalName;
      }
   }

   public StoreBlockDataCs(String name, String optInternalName, String internalName,
    int blockDisplayStyle, String blockType,
    int optBlockDisplayStyle, String optBlockType)
   {
      super(name, optInternalName, internalName);
      this.style = blockDisplayStyle;
      this.optStyle = optBlockDisplayStyle;
      this.type = blockType;
      this.optType = optBlockType;
   }

   public Object clone()
   {
      return new StoreBlockDataCs(getName(),
         getOptionalInternalName(), getInternalName(), style, type,
             optStyle, optType);
   }

   public void setData(TeXParser parser, TeXObject optArg, TeXObject arg)
   {
      arg = new BlockElement(type, style, arg);

      if (optArg != null)
      {
         optArg = new BlockElement(optType, optStyle, optArg);
      }

      super.setData(parser, optArg, arg);
   }

   protected int style=DocumentBlock.DISPLAY_BLOCK;
   protected int optStyle=DocumentBlock.DISPLAY_BLOCK;
   protected String type, optType;
}
