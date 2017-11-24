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
package com.dickimawbooks.texparserlib.latex.tipa;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.fontenc.FontEncSty;

public class TipaEncoding extends Declaration
{
   public TipaEncoding()
   {
      this("tipaencoding");
   }

   public TipaEncoding(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new TipaEncoding(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      TeXObjectList expanded = new TeXObjectList();

      expanded.add(new TeXCsRef("fontencoding"));
      expanded.add(parser.getListener().createGroup("T3"));

      return expanded;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
      throws IOException
   {
      return expandonce(parser);
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
      TeXSettings settings = parser.getSettings();
      orgEncoding = settings.getCurrentFontEncoding();

      FontEncSty sty = 
        ((LaTeXParserListener)parser.getListener()).getFontEncSty();

      FontEncoding newEncoding = sty.getEncoding("T3");
      settings.setFontEncoding(newEncoding);
   }

   public void end(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontEncoding(orgEncoding);
   }

   public boolean isModeSwitcher()
   {
      return false;
   }

   private FontEncoding orgEncoding = null;
}
