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

public class AuthorCs extends StoreBlockDataCs
{
   public AuthorCs()
   {
      this("author", "@shortauthor", "@author", DocumentBlock.DISPLAY_BLOCK,
       "authors", DocumentBlock.DISPLAY_INLINE, "shortauthors");
   }

   public AuthorCs(String name, String optInternalName, String internalName,
    int blockDisplayStyle, String blockType,
    int optBlockDisplayStyle, String optBlockType)
   {
      super(name, optInternalName, internalName, blockDisplayStyle, blockType,
        optBlockDisplayStyle, optBlockType);
   }

   public Object clone()
   {
      return new AuthorCs(getName(),
         getOptionalInternalName(), getInternalName(), style, type,
             optStyle, optType);
   }

   public void setData(TeXParser parser, TeXObject optArg, TeXObject arg)
   {
      // separate each author

      if (!(arg instanceof TeXObjectList))
      {
         super.setData(parser, optArg, arg);
         return;
      }

      TeXObjectList list = new TeXObjectList();

      int numAuthors = 0;

      BlockElement element = null;
      BlockElement details = null;

      for (TeXObject obj : (TeXObjectList)arg)
      {
         if (obj instanceof Ignoreable)
         {
            if (element == null)
            {
               list.add(obj);
            }
            else if (details == null)
            {
               element.add(obj);
            }
            else
            {
               details.add(obj);
            }

            continue;
         }

         if (element == null)
         {
            numAuthors++;
            element = new BlockElement("author",
               DocumentBlock.DISPLAY_INLINE_BLOCK);
            element.setAttribute("label", "author"+numAuthors);

            list.add(element);
         }

         ControlSequence cs = null;

         if (obj instanceof AssignedMacro)
         {
            TeXObject base = ((AssignedMacro)obj).getBaseUnderlying();

            if (base instanceof ControlSequence)
            {
               cs = (ControlSequence)base;
            }
         }
         else if (obj instanceof ControlSequence)
         {
            cs = (ControlSequence)obj;
         }

         if (cs != null && 
              (cs instanceof AuthorAnd || cs.getName().toLowerCase().equals("and")))
         {
            details = null;
            element = null;
            list.add(obj);
         }
         else if (details != null)
         {
            details.add(obj);
         }
         else if (cs != null && (cs instanceof Cr || cs.getName().equals("\\")))
         {
            element.add(obj);

            details = new BlockElement("authordetails",
              DocumentBlock.DISPLAY_INLINE_BLOCK);
            element.add(details);
         }
         else
         {
            element.add(obj);
         }
      }

      super.setData(parser, optArg, list);
   }

}
