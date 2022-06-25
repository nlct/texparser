/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

/*
 * A command that simply expands to a given unformatted string. For example,
 * a command used to a store label. If the label references an
 * object, the object can also be stored to save repeatedly looking
 * it up.
 */
public class TextualContentCommand extends ControlSequence implements Expandable
{
   public TextualContentCommand(String name, String text)
   {
      this(name, text, null);
   }

   public TextualContentCommand(String name, String text, Object data)
   {
      super(name);

      if (text == null)
      {
         throw new NullPointerException();
      }

      this.text = text;
      this.data = data;
   }

   @Override
   public Object clone()
   {
      return new TextualContentCommand(getName(), getText(), getData());
   }

   public TextualContentCommand duplicate(String newcsname)
   {
      TextualContentCommand copy = (TextualContentCommand)clone();
      copy.name = newcsname;
      return copy;
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return parser.getListener().createString(text);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      parser.getListener().getWriteable().write(text);
   }

   public String getText()
   {
      return text;
   }

   public Object getData()
   {
      return data;
   }

   @Override
   public boolean isEmpty()
   {
      return text.isEmpty();
   }

   @Override
   public String toString()
   {
      return String.format("%s[name=%s,text=%s,data=%s]",
       getClass().getSimpleName(), getName(), getText(), getData());
   }

   @Override
   public boolean equals(Object other)
   {
      if (!(other instanceof TextualContentCommand) || other == null)
      {
         return false;
      }

      TextualContentCommand cmd = (TextualContentCommand)other;

      if (!text.equals(cmd.text))
      {
         return false;
      }

      if (data == null && cmd.data == null)
      {
         return true;
      }

      if (data == null || cmd.data == null)
      {
         return false;
      }

      return data.equals(cmd.data);
   }

   protected String text;
   protected Object data;
}
