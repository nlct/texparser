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

   public boolean isEntryType(String type)
   {
      if (entryType == null || type == null) return false;

      return entryType.toLowerCase().equals(type.toLowerCase());
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   public BibValueList getIdField()
   {
      return idValue;
   }

   public BibValueList removeField(String fieldName)
   {
      return fields.remove(fieldName);
   }

   public void putField(String fieldName, BibValueList contents)
   {
      if (fieldName == null)
      {
         throw new NullPointerException();
      }

      String key = getKey(fieldName);

      if (key != null && !fieldName.equals(key))
      {
         fields.remove(key);
      }

      fields.put(fieldName, contents);
   }

   public Set<String> getKeySet()
   {
      return fields.keySet();
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


   /*
    * Checks if given key is permitted. Returns false if
    * key and its associated value should be skipped.
    * Throws exception if the use of the key is prohibited.
    */ 
   public boolean checkField(String key) throws BibTeXSyntaxException
   {
      return true;
   }

   public void parseContents(TeXParser parser, 
    TeXObjectList contents, TeXObject endGroupChar)
     throws IOException
   {
      TeXObjectList idList = null;

      try
      {
         idList = readKeyObject(parser, contents);
      }
      catch (BibTeXSyntaxException e)
      {
         throw new BibTeXSyntaxException(e, parser,
          BibTeXSyntaxException.ERROR_INVALID_ID, e.getParams());
      }

      if (idList == null)
      {
         throw new BibTeXSyntaxException(parser,
           BibTeXSyntaxException.ERROR_MISSING_ID);
      }

      String id = idList.format();

      if (id.isEmpty())
      {
         throw new BibTeXSyntaxException(parser,
           BibTeXSyntaxException.ERROR_MISSING_ID);
      }

      setId(id);

      BibValueList value = new BibValueList();
      value.add(new BibUserString(idList));
      idValue = value;

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
           ",", endGroupChar.format());
      }

      while (!contents.isEmpty())
      {
         String key = readKey(parser, contents);

         if (key == null)
         {
            break;
         }

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

         value = new BibValueList();

         readValue(parser, (TeXObjectList)contents, value, endGroupChar);

         if (value.isEmpty())
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
         }

         if (checkField(key))
         {
            putField(key, value);
         }
      }
   }

   public String format(byte caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange)
   {
      StringBuilder builder = new StringBuilder();

      builder.append(String.format("@%s%c%s", 
        applyCase(entryType, caseChange), openDelim, id));

      Set<String> keys = fields.keySet();

      Iterator<String> it = keys.iterator();

      while (it.hasNext())
      {
         String key = it.next();

         builder.append(String.format(",%n  %s = %s", 
           applyCase(key, caseChange), 
           fields.get(key).applyDelim(fieldDelimChange)));
      }

      builder.append(String.format("%n%c%n", closeDelim));

      return builder.toString();
   }

   public String toString()
   {
      return String.format("%s[type=%s,id=%s]",
         getClass().getSimpleName(), entryType, id);
   }

   public Vector<Contributor> getEditors(TeXParser parser)
     throws IOException
   {
      BibValueList field = getField("editor");

      if (field == null) return null;

      return parseContributors(parser, field);
   }

   public Vector<Contributor> getAuthors(TeXParser parser)
     throws IOException
   {
      BibValueList field = getField("author");

      if (field == null) return null;

      return parseContributors(parser, field);
   }

   public static Vector<Contributor> parseContributors(TeXParser parser, BibValueList field)
      throws IOException
   {
      Vector<Contributor> contributors = new Vector<Contributor>();

      TeXObjectList list = field.expand(parser);

      TeXObjectList word = null;

      Vector<TeXObjectList> current = new Vector<TeXObjectList>();

      while (!list.isEmpty())
      {
         if (word == null)
         {
            list.popLeadingWhiteSpace();

            if (list.isEmpty())
            {
               break;
            }
         }

         TeXObject object = list.pop();

         if (word == null)
         {
            if (object instanceof CharObject)
            {
               if (((CharObject)object).getCharCode() == (int)'a')
               {
                  if (list.size() > 3)
                  {
                     TeXObject nextObj = list.pop();
                     TeXObject nextObj2 = list.pop();
                     TeXObject nextObj3 = list.pop();

                     if (nextObj instanceof CharObject
                      && ((CharObject)nextObj).getCharCode() == (int)'n'
                      && nextObj2 instanceof CharObject
                      && ((CharObject)nextObj2).getCharCode() == (int)'d'
                      && nextObj3 instanceof WhiteSpace)
                     {
                        contributors.add(parseContributor(parser, current));
                        current = new Vector<TeXObjectList>();
                        continue;
                     }

                     word = new TeXObjectList();
                     word.add(object);
                     word.add(nextObj);
                     word.add(nextObj2);
                     word.add(nextObj3);
                  }
                  else
                  {
                     word = new TeXObjectList();
                     word.add(object);
                  }
               }
               else
               {
                  word = new TeXObjectList();
                  word.add(object);

                  if (((CharObject)object).getCharCode() == (int)',')
                  {
                     current.add(word);
                     word = null;
                  }
               }
            }
            else
            {
               word = new TeXObjectList();
               word.add(object);
            }
         }
         else if (object instanceof CharObject
              && ((CharObject)object).getCharCode() == (int)',')
         {
            current.add(word);
            word = new TeXObjectList();
            word.add(object);
            current.add(word);
            word = null;
         }
         else if (object instanceof WhiteSpace)
         {
            current.add(word);
            word = null;
         }
         else
         {
            word.add(object);
         }
      }

      if (word != null)
      {
         current.add(word);
      }

      contributors.add(parseContributor(parser, current));

      return contributors;
   }

   private static Contributor parseContributor(TeXParser parser,
     Vector<TeXObjectList> list)
    throws IOException
   {
      int commaCount = 0;

      StringBuffer buffer = new StringBuffer();

      for (int i = 0; i < list.size(); i++)
      {
         TeXObjectList word = list.get(i);

         if (word.size() == 1)
         {
            TeXObject object = word.firstElement();

            if (object instanceof CharObject 
             && ((CharObject)object).getCharCode() == (int)',')
            {
               commaCount++;
               buffer.append(",");
            }
            else if (i > 0)
            {
               buffer.append(String.format(" %s", object.format()));
            }
            else
            {
               buffer.append(object.format());
            }
         }
         else if (i > 0)
         {
            buffer.append(String.format(" %s", word.format()));
         }
         else
         {
            buffer.append(word.format());
         }
      }

      if (buffer.toString().equals("others"))
      {
         return new EtAl();
      }

      TeXObject forenames = null;
      TeXObject von = null;
      TeXObject suffix = null;
      TeXObject surname = null;

      switch (commaCount)
      {
         case 0: // forenames [von] surname

            if (list.isEmpty())
            {
               return new Contributor();
            }

            if (list.size() == 1)
            {
               surname = list.firstElement();
               break;
            }

            surname = new TeXObjectList();

            for (int i = 0, lastIdx = list.size()-1; i <= lastIdx; i++)
            {
               TeXObjectList word = list.get(i);

               if (surname == null && startsWithLower(word))
               {
                  von = appendName(parser, von, word);
               }
               else if (von != null || i == lastIdx)
               {
                  surname = appendName(parser, surname, word);
               }
               else
               {
                  forenames = appendName(parser, forenames, word);
               }
            }

         break;
         case 1: // [von] surname, forenames

            for (int i = 0, n = list.size(); i < n; i++)
            {
               TeXObjectList word = list.get(i);

               if (isComma(word))
               {
                  i++;

                  if (i < n)
                  {
                     forenames = list.get(i);
                  }
               }
               else if (surname == null && startsWithLower(word))
               {
                  von = appendName(parser, von, word);
               }
               else if (forenames == null)
               {
                  surname = appendName(parser, surname, word);
               }
               else
               {
                  forenames = appendName(parser, forenames, word);
               }
            }

         break;
         case 2: // [von] surname, suffix, forenames

            for (int i = 0, n = list.size(); i < n; i++)
            {
               TeXObjectList word = list.get(i);

               if (isComma(word))
               {
                  i++;

                  if (i < n)
                  {
                     if (suffix == null)
                     {
                        suffix = list.get(i);
                     }
                     else
                     {
                        forenames = list.get(i);
                     }
                  }
               }
               else if (surname == null && startsWithLower(word))
               {
                  von = appendName(parser, von, word);
               }
               else if (suffix == null)
               {
                  surname = appendName(parser, surname, word);
               }
               else if (forenames == null)
               {
                  suffix = appendName(parser, suffix, word);
               }
               else
               {
                  forenames = appendName(parser, forenames, word);
               }
            }

         break;
         default:
           throw new BibTeXSyntaxException(parser, 
             BibTeXSyntaxException.ERROR_TOO_MANY_COMMAS, buffer.toString());
      }

      return new Contributor(forenames, von, surname, suffix);
   }

   private static TeXObject appendName(TeXParser parser, 
     TeXObject name, TeXObjectList word)
   {
      if (name == null)
      {
         name = word;
      }
      else
      {
         TeXObjectList nameList;

         if (name instanceof TeXObjectList 
            && !(name instanceof Group))
         {
            nameList = (TeXObjectList)name;
         }
         else
         {
            nameList = new TeXObjectList();
            nameList.add(name);
            name = nameList;
         }

         if (!nameList.isEmpty())
         {
            nameList.add(parser.getListener().getSpace());
         }

         nameList.add(word);
      }

      return name;
   }

   private static boolean isComma(TeXObjectList word)
   {
      if (word.size() == 1)
      {
         TeXObject object = word.firstElement();

         return (object instanceof CharObject
                 && ((CharObject)object).getCharCode() == (int)',');
      }

      return false;
   }

   protected static boolean startsWithLower(TeXObjectList word)
   {
      return getInitialCase(word) == CASE_LOWER;
   }

   private static byte getInitialCase(TeXObjectList word)
   {
      for (TeXObject object : word)
      {
         if (object instanceof CharObject)
         {
            int code = ((CharObject)object).getCharCode();

            if (Character.isLowerCase(code)) return CASE_LOWER;

            if (Character.isUpperCase(code)) return CASE_UPPER;
         }
         else if (object instanceof TeXObjectList)
         {
            byte result = getInitialCase((TeXObjectList)object);

            if (result != CASE_NA)
            {
               return result;
            }
         }
      }

      return CASE_NA;
   }

   public Object clone()
   {
      BibEntry obj = new BibEntry(getEntryType());

      obj.id = id;

      if (idValue != null)
      {
         obj.idValue = (BibValueList)idValue.clone();
      }

      for (Iterator<String> it=fields.keySet().iterator();
           it.hasNext();)
      {
         String key = it.next();

         obj.fields.put(key, (BibValueList)fields.get(key).clone());
      }

      return obj;
   }

   private HashMap<String,BibValueList> fields;
   private String entryType;
   private String id;
   private BibValueList idValue;

   private static final byte CASE_LOWER=0, CASE_UPPER=1, CASE_NA=2;
}
