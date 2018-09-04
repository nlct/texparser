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
 * Bib data
 */

public abstract class BibData
{
   public abstract String getEntryType();

   public abstract void parseContents(TeXParser parser, 
    TeXObjectList contents, TeXObject endGroupChar)
     throws IOException;

   public static BibData createBibData(String entryType)
   {
      String lc = entryType.toLowerCase();

      if (lc.equals("preamble"))
      {
         return new BibPreamble(entryType);
      }

      if (lc.equals("string"))
      {
         return new BibString(entryType);
      }

      if (lc.equals("comment"))
      {
         return new BibComment(entryType);
      }

      return new BibEntry(entryType);
   }

   public TeXObjectList readKeyObject(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();

      TeXObject object = stack.popStack(parser);

      while (object != null && object instanceof WhiteSpace)
      {
         object = stack.popStack(parser);
      }

      if (object == null)
      {
         return null;
      }

      while (object != null)
      {
         if (object instanceof WhiteSpace)
         {
            break;
         }

         if (object instanceof At)
         {// allow @ in label
            object = parser.getListener().getOther('@');
         }
         else if (object instanceof SbChar)
         {// allow _ in label
            object = parser.getListener().getOther('_');
         }

         if (object instanceof CharObject)
         {
            int code = ((CharObject)object).getCharCode();

            if (code == (int)'#' || code == (int)'"'
             || code == (int)'(' || code == (int)')')
            {
               throw new BibTeXSyntaxException(parser,
                 BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_FIELD_NAME,
                 object.format());
            }
            else if (code == (int)',' || code == (int)'=')
            {
               stack.push(object);
               break;
            }
         }
         else
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_FIELD_NAME,
              object.format());
         }

         list.add(object);
         object = stack.popStack(parser);
      }

      return list;
   }

   public String readKey(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = readKeyObject(parser, stack);

      if (list == null)
      {
         return null;
      }

      String key = list.format();

      if (key.isEmpty())
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_MISSING_FIELD_NAME);
      }

      return key;
   }

   public boolean readValue(TeXParser parser, TeXObjectList stack,
      BibValueList bibValList, TeXObject eg)
     throws IOException
   {
      BibParser bibParser = (BibParser)parser.getListener();

      TeXObject object = stack.popStack(parser);

      while (object != null && object instanceof WhiteSpace)
      {
         object = stack.popStack(parser);
      }

      if (object == null)
      {
         return false;
      }

      if (object instanceof CharObject)
      {
         int code = ((CharObject)object).getCharCode();

         if (code == (int)',')
         {
            return true;
         }

         if (code == (int)'#')
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
         }
      }

      if (object instanceof StringConcat)
      {
         throw new BibTeXSyntaxException(parser, 
           BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
      }
      else if (object instanceof Group)
      {
         bibValList.add(new BibUserString(object));
      }
      else if (object instanceof CharObject
            && ((CharObject)object).getCharCode() == (int)'"')
      {
         TeXObjectList list = new TeXObjectList();
         list.add(object);
         object = stack.popStack(parser);

         while (object != null)
         {
            list.add(object);

            if (object instanceof CharObject
             && ((CharObject)object).getCharCode() == (int)'"')
            {
               break;
            }

            object = stack.popStack(parser);
         }

         if (object == null)
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_UNBALANCED_BRACES);
         }

         bibValList.add(new BibUserString(list));
      }
      else if (object instanceof TeXNumber)
      {
         bibValList.add(new BibNumber((TeXNumber)object));
      }
      else
      {
         TeXObjectList list = new TeXObjectList();

         while (object != null && !(object instanceof WhiteSpace))
         {
            if (object instanceof CharObject)
            {
               int code = ((CharObject)object).getCharCode();

               if (code == (int)',')
               {
                  break;
               }

               if (code == (int)'=')
               {
                  if (list.isEmpty())
                  {
                     throw new BibTeXSyntaxException(parser,
                       BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
                  }
                  else
                  {
                     throw new BibTeXSyntaxException(parser,
                       BibTeXSyntaxException.ERROR_EXPECTING_OR,
                       ",", eg.format());
                  }
               }

               if (code == (int)'#')
               {
                  object = new StringConcat(code);
               }
            }

            if (object instanceof StringConcat)
            {
               stack.push(object);
               break;
            }

            list.add(object);
            object = stack.popStack(parser);

         }

         stack.push(object);

         if (list.isEmpty())
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_MISSING_FIELD_PART);
         }

         String val = list.toString(parser);

         try
         {
            bibValList.add(
              new BibNumber(new UserNumber(Integer.parseInt(val))));
         }
         catch (NumberFormatException e)
         {
            bibValList.add(new BibVariable(list, val));
         }
      }

      object = stack.popStack(parser);

      while (object != null && object instanceof WhiteSpace)
      {
         object = stack.popStack(parser);
      }

      if (object == null)
      {
         return false;
      }

      if (object instanceof CharObject)
      {
         int code = ((CharObject)object).getCharCode();

         if (code == ',')
         {
            return true;
         }

         if (code == (int)'=')
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_EXPECTING_OR,
              ",", eg.format());
         }

         if (code == '#')
         {
            object = new StringConcat(code);
         }
      }

      if (object instanceof StringConcat)
      {
         bibValList.add((StringConcat)object);
         int n = bibValList.size();

         boolean result = readValue(parser, stack, bibValList, eg);

         if (bibValList.size() == n)
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_EXPECTING_OR,
              ",", eg.format());
         }

         return result;
      }

      throw new BibTeXSyntaxException(parser,
        BibTeXSyntaxException.ERROR_EXPECTING_OR,
        ",", eg.format());
   }

   public String format()
   {
      return format(CASE_NOCHANGE, '{', '}', 
        BibValue.FIELD_DELIM_NOCHANGE);
   }

   public static String applyCase(String string, byte caseChange)
   {
      switch (caseChange)
      {
         case CASE_NOCHANGE: return string;
         case CASE_TO_LOWER: return string.toLowerCase();
         case CASE_TO_UPPER: return string.toUpperCase();
         case CASE_INITIAL_CAP:
           return string.substring(0,1).toUpperCase()
                + string.substring(1).toLowerCase();
      }

      throw new IllegalArgumentException("Invalid caseChange argument");
   }

   public abstract String format(byte caseChange, char openDelim, char closeDelim,
     byte fieldDelimChange);

   public static final byte CASE_NOCHANGE=0, CASE_TO_LOWER=1,
     CASE_TO_UPPER=2, CASE_INITIAL_CAP=3;

}
