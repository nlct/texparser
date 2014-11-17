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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;

import com.dickimawbooks.texparserlib.*;

public class Today extends Command
{
   public Today()
   {
      this("today");
   }

   public Today(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Today(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return parser.getListener().createString(DateFormat.getDateInstance().format(new Date()));
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandfully(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
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
      Writeable writeable = parser.getListener().getWriteable();

      writeable.write(DateFormat.getDateInstance().format(new Date()));
   }

}
