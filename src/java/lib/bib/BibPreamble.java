/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
 * Bib preamble data
 */

public class BibPreamble extends BibData
{
   public BibPreamble()
   {
      this("preamble");
   }

   public BibPreamble(String entryType)
   {
      this.entryType = entryType;
   }

   @Override
   public String getEntryType()
   {
      return entryType;
   }

   public BibValueList getPreamble()
   {
      return preamble;
   }

   @Override
   public void parseContents(TeXParser parser, 
    AbstractTeXObjectList contents, TeXObject endGroupChar)
     throws IOException
   {
      preamble = new BibValueList();

      readValue(parser, contents, preamble, endGroupChar);

      if (preamble.isEmpty())
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
      }

      for (int i = 0; i < preamble.size(); i++)
      {
         BibValue value = preamble.get(i);
      }
   }

   @Override
   public String format(CaseChange caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange)
   {
      return String.format("@%s%c%s%c", 
         applyCase(entryType, caseChange), openDelim,
         preamble.applyDelim(fieldDelimChange),
         closeDelim);
   }

   @Override
   public String toString()
   {
      return String.format("%s[type=%s,contents=%s]",
         getClass().getSimpleName(), entryType, preamble);
   }

   @Override
   public Object clone()
   {
      BibPreamble obj = new BibPreamble(getEntryType());

      if (preamble != null)
      {
         obj.preamble = (BibValueList)preamble.clone();
      }

      return obj;
   }

   private String entryType;
   private BibValueList preamble;
}
