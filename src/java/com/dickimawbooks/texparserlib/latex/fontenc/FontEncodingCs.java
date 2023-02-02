/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.fontenc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FontEncodingCs extends Declaration
{
   public FontEncodingCs(FontEncSty sty)
   {
      this(sty, "fontencoding");
   }

   public FontEncodingCs(FontEncSty sty, String name)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new FontEncodingCs(getSty(), getName());
   }

   public FontEncSty getSty()
   {
      return sty;
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();
      orgEncoding = settings.getCurrentFontEncoding();

      String encName = popLabelString(parser, stack);

      FontEncoding newEncoding = sty.getEncoding(encName);

      settings.setFontEncoding(newEncoding);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontEncoding(orgEncoding);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   private FontEncoding orgEncoding = null;
   private FontEncSty sty;
}
