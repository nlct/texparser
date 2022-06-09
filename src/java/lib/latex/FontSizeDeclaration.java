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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class FontSizeDeclaration extends Declaration
{
   public FontSizeDeclaration(String name, int sizeId)
   {
      super(name);
      this.orgSize = TeXFontSize.INHERIT;

      switch (sizeId)
      {
         case TeXSettings.INHERIT:
           size = TeXFontSize.INHERIT;
         break;
         case TeXSettings.SIZE_NORMAL:
           size = TeXFontSize.NORMAL;
         break;
         case TeXSettings.SIZE_LARGE:
           size = TeXFontSize.LARGE;
         break;
         case TeXSettings.SIZE_XLARGE:
           size = TeXFontSize.XLARGE;
         break;
         case TeXSettings.SIZE_XXLARGE:
           size = TeXFontSize.XXLARGE;
         break;
         case TeXSettings.SIZE_HUGE:
           size = TeXFontSize.HUGE;
         break;
         case TeXSettings.SIZE_XHUGE:
           size = TeXFontSize.XHUGE;
         break;
         case TeXSettings.SIZE_XXHUGE:
           size = TeXFontSize.XXHUGE;
         break;
         case TeXSettings.SIZE_SMALL:
           size = TeXFontSize.SMALL;
         break;
         case TeXSettings.SIZE_FOOTNOTE:
           size = TeXFontSize.FOOTNOTE;
         break;
         case TeXSettings.SIZE_SCRIPT:
           size = TeXFontSize.SCRIPT;
         break;
         case TeXSettings.SIZE_TINY:
           size = TeXFontSize.TINY;
         break;
         default:
            throw new IllegalArgumentException("Invalid size ID: "+sizeId);
      }
   }

   public FontSizeDeclaration(String name, TeXFontSize size)
   {
      super(name);
      this.size = size;
      this.orgSize = TeXFontSize.INHERIT;
   }

   @Override
   public Object clone()
   {
      return new FontSizeDeclaration(getName(), size);
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList list)
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
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList list)
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
   public void process(TeXParser parser) throws IOException
   {
      TeXSettings settings = parser.getSettings();

      orgSize = settings.getCurrentFontSize();

      settings.setFontSize(size);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      process(parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXSettings settings = parser.getSettings();
      settings.setFontSize(orgSize);
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   public TeXFontSize getSize()
   {
      return size;
   }

   private TeXFontSize size, orgSize;
}
