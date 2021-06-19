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
 * Bib string data
 */

public class BibString extends BibData
{
   public BibString()
   {
      this("string");
   }

   public BibString(String entryType)
   {
      this.entryType = entryType;
   }

   @Override
   public String getEntryType()
   {
      return entryType;
   }

   @Override
   public void parseContents(TeXParser parser, 
    AbstractTeXObjectList contents, TeXObject endGroupChar)
     throws IOException
   {
      key = readKey(parser, contents);

      if (key == null)
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_MISSING_FIELD_NAME);
      }

      TeXObject object = contents.popStack(parser);

      while (object != null && object instanceof WhiteSpace)
      {
         object = contents.popStack(parser);
      }

      if (object == null)
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_STRING_NAME,
           endGroupChar.format());
      }

      if (!(object instanceof CharObject
            && ((CharObject)object).getCharCode() == (int)'='))
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_STRING_NAME,
           object.format());
      }

      value = new BibValueList();

      readValue(parser, contents, value, endGroupChar);

      if (value.isEmpty())
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
      }
   }

   public String getKey()
   {
      return key;
   }

   public BibValueList getValue()
   {
      return value;
   }

   @Override
   public String format(CaseChange caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange)
   {
      return String.format("@%s%c%s = %s%c%n", 
         applyCase(entryType, caseChange), openDelim, key,
         value.applyDelim(fieldDelimChange),
         closeDelim);
   }

   @Override
   public String toString()
   {
      return String.format("%s[type=%s,key=%s]",
         getClass().getSimpleName(), entryType, key);
   }

   @Override
   public Object clone()
   {
      BibString obj = new BibString(getEntryType());
      obj.key = key;

      if (value != null)
      {
         obj.value = (BibValueList)value.clone();
      }

      return obj;
   }

   private String entryType;

   private String key;
   private BibValueList value;
}
