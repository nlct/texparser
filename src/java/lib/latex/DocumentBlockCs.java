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
import java.util.Stack;
import java.util.Locale;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import com.dickimawbooks.texparserlib.*;

// \texparserlib@documentblock*{type}[options]{content}
// starred form indicates inline block
public class DocumentBlockCs extends ControlSequence
{
   public DocumentBlockCs()
   {
      this("texparserlib@documentblock");
   }

   public DocumentBlockCs(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DocumentBlockCs(getName());
   }

   protected DocumentBlock createBlock(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      int displayStyle = DocumentBlock.DISPLAY_BLOCK;

      TeXObject object = stack.peekStack();

      if (object instanceof CharObject)
      {
         if (((CharObject)object).getCharCode() == (int)'*')
         {
            displayStyle = DocumentBlock.DISPLAY_INLINE;

            if (stack == null || stack == parser)
            {
               parser.popStack();
            }
            else
            {
               stack.popStack(parser);
            }
         }
      }

      TeXObject arg;

      if (parser == stack || stack == null)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (stack == parser || stack == null)
         {
            expanded = ((Expandable)arg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)arg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      String blockType = arg.format();

      return new DocumentBlock(blockType, displayStyle);
   }

   public void setBlockAttributes(DocumentBlock block,
      TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject optArg = null;

      if (stack == null || stack == parser)
      {
         optArg = parser.popNextArg('[', ']');
      }
      else
      {
         optArg = stack.popArg(parser, '[', ']');
      }

      if (optArg != null)
      {
         KeyValList optList = KeyValList.getList(parser, optArg);

         for (Iterator<String> it = optList.getOrderedKeyIterator(); it.hasNext(); )
         {
            String key = it.next();
            TeXObject value = optList.getExpandedValue(key, parser, stack);

            if (value == null) continue;

            try
            {
               if (key.equals("label") || key.equals("class"))
               {
                  block.setAttribute(key, value.format());
               }
               else if (key.equals("display"))
               {
                  String displayVal = value.format();

                  if (displayVal.equals("block"))
                  {
                     block.setDisplayStyle(DocumentBlock.DISPLAY_BLOCK);
                  }
                  else if (displayVal.equals("inline-block"))
                  {
                     block.setDisplayStyle(DocumentBlock.DISPLAY_INLINE_BLOCK);
                  }
                  else if (displayVal.equals("inline"))
                  {
                     block.setDisplayStyle(DocumentBlock.DISPLAY_INLINE);
                  }

                  block.setAttribute(key, displayVal);
               }
               else if (key.equals("locale"))
               {
                  block.setAttribute(key, Locale.forLanguageTag(value.format()));
               }
               else if (key.equals("datetime"))
               {
                  block.setAttribute(key, DATETIME_FORMAT.parse(value.format()));
               }
               else if (key.equals("date"))
               {
                  block.setAttribute(key, DATE_FORMAT.parse(value.format()));
               }
               else if (key.equals("time"))
               {
                  block.setAttribute(key, TIME_FORMAT.parse(value.format()));
               }
               else
               {
                  block.setAttribute(key, value);
               }
            }
            catch (Exception e)
            {
               parser.getListener().getTeXApp().error(new TeXSyntaxException(
                 e, parser, TeXSyntaxException.ERROR_PARSING, 
                 String.format("%s={%s}", key, value.toString(parser))));
            }
         }
      }

   }

   protected void processContent(DocumentBlock block,
     TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject arg;

      if (parser == stack || stack == null)
      {
         arg = parser.popNextArg();
         arg.process(parser);
      }
      else
      {
         arg = stack.popArg(parser);
         arg.process(parser, stack);
      }
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      DocumentBlock block = createBlock(parser, stack);
      setBlockAttributes(block, parser, stack);

      ((LaTeXParserListener)parser.getListener()).startBlock(block);

      processContent(block, parser, stack);

      ((LaTeXParserListener)parser.getListener()).endBlock(block);
   }

   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
   public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
}
