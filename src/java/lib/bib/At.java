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

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class At extends ActiveChar
{
   public At()
   {
      this('@');
   }

   public At(char c)
   {
      this((int)c);
   }

   public At(int code)
   {
      charCode = code;
   }

   public int getCharCode()
   {
      return charCode;
   }

   protected void process(TeXParser parser, TeXObjectList entryTypeList,
     TeXObject contents, TeXObject eg)
     throws IOException
   {
      if (!(contents instanceof TeXObjectList))
      {
         throw new BibTeXSyntaxException(parser,
           BibTeXSyntaxException.ERROR_EXPECTING_OR, "{", "(");
      }

      BibParser bibParser = (BibParser)parser.getListener();

      String entryType = entryTypeList.toString(parser).trim();

      BibData data = BibData.createBibData(entryType);

      data.parseContents(parser, (TeXObjectList)contents, eg);

      if (data instanceof BibEntry)
      {
         String id = ((BibEntry)data).getId();

         if (bibParser.getBibEntry(id) != null)
         {
            throw new BibTeXSyntaxException(parser,
              BibTeXSyntaxException.ERROR_REPEATED_ENTRY, id);
         }
      }

      bibParser.addBibData(data);
   }

   // bibtex allows () delimiters for the entry in addition to {}
   // (but not for the field values)

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject object = stack.popStack(parser);

      while (object != null && (object instanceof WhiteSpace))
      {
         object = stack.popStack(parser);
      }

      TeXObjectList entryType = new TeXObjectList();

      while (object != null && object instanceof CharObject)
      {
         entryType.add(object);

         object = stack.popStack(parser);
      }

      while (object != null && (object instanceof WhiteSpace))
      {
         object = stack.popStack(parser);
      }

      TeXObject contents;

      TeXObject eg = null;

      if (object instanceof Group)
      {
         contents = object;
         eg = parser.getListener().getEgChar(parser.getEgChar());
      }
      else if (object instanceof BgChar)
      {
         stack.push(object);
         contents =
           (stack == parser ? parser.popNextArg() : stack.popArg(parser));
         eg = parser.getListener().getEgChar(parser.getEgChar());
      }
      else if ((object instanceof CharObject 
            && ((CharObject)object).getCharCode() == (int)'('))
      {
         contents = new TeXObjectList();
         TeXObjectList list = (TeXObjectList)contents;

         while ((object = stack.popToken()) != null)
         {
            if (object instanceof CharObject
            && ((CharObject)object).getCharCode() == (int)')')
            {
               eg = object;
               break;
            }

            if (object instanceof BgChar)
            {
               stack.push(object);
               object = stack.popStack(parser);
               list.add(object);
            }
            else if (object instanceof CharObject
                   && ((CharObject)object).getCharCode() == (int)'"')
            {
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
               }
            }
         }


         if (eg == null)
         {
            throw new BibTeXSyntaxException(parser, 
              BibTeXSyntaxException.ERROR_ILLEGAL_END);
         }
      }
      else
      {
         throw new BibTeXSyntaxException(parser,
          BibTeXSyntaxException.ERROR_IMMEDIATELY_FOLLOWS_ENTRY_TYPE,
          entryType.format());
      }

      process(parser, entryType, contents, eg);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
     throws IOException
   {
      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return null;
   }

   public Object clone()
   {
      return new At(charCode);
   }

   public String toString(TeXParser parser)
   {
      return new String(Character.toChars(charCode));
   }

   public int charCode = (int)'@';
}
