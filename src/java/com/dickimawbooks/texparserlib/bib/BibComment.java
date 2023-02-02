/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.bib;

import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/**
 * Bib comment data
 */

public class BibComment extends BibData
{
   public BibComment()
   {
      this("comment");
   }

   public BibComment(String entryType)
   {
      this.entryType = entryType;
   }

   public String getEntryType()
   {
      return entryType;
   }

   public Object clone()
   {
      BibComment obj = new BibComment(getEntryType());

      if (contents != null)
      {
         obj.contents = (TeXObjectList)contents.clone();
      }

      return obj;
   }

   public TeXObjectList getContents()
   {
      return contents;
   }

   public void parseContents(TeXParser parser, 
    TeXObjectList stack, TeXObject endGroupChar)
     throws IOException
   {
      contents = new TeXObjectList();

      while (stack.size() > 0)
      {
         contents.add(stack.popStack(parser));
      }
   }

   public String format(byte caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange)
   {
      return String.format("@%s%c%s%c", 
         applyCase(entryType, caseChange), openDelim,
         contents.format(),
         closeDelim);
   }

   public String toString()
   {
      return String.format("%s[type=%s,contents=%s]",
         getClass().getSimpleName(), entryType, contents);
   }

   private String entryType;
   private TeXObjectList contents;
}
