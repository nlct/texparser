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
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/**
 * Bib entry data
 */

public class BibEntry extends BibData
{
   public BibEntry(String entryType)
   {
      this.entryType = entryType;
      this.fields = new HashMap<String,BibValueList>();
   }

   public String getEntryType()
   {
      return entryType;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   public void putField(String fieldName, BibValueList contents)
   {
      String key = getKey(fieldName);

      if (key != null && !fieldName.equals(key))
      {
         fields.remove(key);
      }

      fields.put(fieldName, contents);
   }

   public String getKey(String fieldName)
   {
      BibValue value = fields.get(fieldName);

      if (value != null)
      {
         return fieldName;
      }

      Set<String> keyset = fields.keySet();

      Iterator<String> it = keyset.iterator();

      String lc = fieldName.toLowerCase();

      while (it.hasNext())
      {
         String key = it.next();

         if (lc.equals(key.toLowerCase()))
         {
            return key;
         }
      }

      return null;
   }

   public BibValueList getField(String fieldName)
   {
      BibValueList value = fields.get(fieldName);

      if (value != null)
      {
         return value;
      }

      String key = getKey(fieldName);

      if (key != null)
      {
         return fields.get(key);
      }

      return null;
   }

   public void parseContents(TeXParser parser, 
    TeXObjectList contents, TeXObject endGroupChar)
     throws IOException
   {
      id = readKey(parser, contents);

      TeXObject object = contents.popStack(parser);

      while (object != null && object instanceof WhiteSpace)
      {
         object = contents.popStack(parser);
      }

      if (object == null)
      {
         return;
      }

      if (!(object instanceof CharObject 
          && ((CharObject)object).getCharCode() == (int)','))
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_EXPECTING_OR,
           new String[] {",", endGroupChar.format()});
      }

      while (!contents.isEmpty())
      {
         String key = readKey(parser, contents);

         object = contents.popStack(parser);

         while (object != null && object instanceof WhiteSpace)
         {
            object = contents.popStack(parser);
         }

         if (object == null)
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_FIELD_NAME,
              endGroupChar.format());
         }

         if (!(object instanceof CharObject
               && ((CharObject)object).getCharCode() == (int)'='))
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_FIELD_NAME,
              object.format());
         }

         BibValueList value = new BibValueList();

         readValue(parser, (TeXObjectList)contents, value, endGroupChar);

         if (value.isEmpty())
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
         }

         putField(key, value);
      }
   }

   public String format(byte caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange)
   {
      StringBuilder builder = new StringBuilder();

      builder.append(String.format("@%s%c%s,%n", 
        applyCase(entryType, caseChange), openDelim, id));

      Set<String> keys = fields.keySet();

      Iterator<String> it = keys.iterator();

      while (it.hasNext())
      {
         String key = it.next();

         builder.append(String.format("%s = %s,%n", 
           applyCase(key, caseChange), 
           fields.get(key).applyDelim(fieldDelimChange)));
      }

      builder.append(String.format("%c%n", closeDelim));

      return builder.toString();
   }

   public String toString()
   {
      return String.format("%s[type=%s,id=%s]",
         getClass().getSimpleName(), entryType, id);
   }

   private HashMap<String,BibValueList> fields;
   private String entryType;
   private String id;
}
