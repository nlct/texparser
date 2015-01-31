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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class L2HAmp extends Command
{
   public L2HAmp()
   {
      this("&");
   }

   public L2HAmp(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HAmp(getName());
   }

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      TeXObjectList list = new TeXObjectList(1);
      list.add(new HtmlTag("&amp;"));
      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList List)
      throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList List)
      throws IOException
   {
      return expandfully(parser);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      if (((L2HConverter)parser.getListener()).isInDocEnv())
      {
         parser.getListener().getWriteable().write("&amp;");
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      process(parser);
   }
}
