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

   public Object clone()
   {
      return new FontEncodingCs(getSty(), getName());
   }

   public FontEncSty getSty()
   {
      return sty;
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

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();
      orgEncoding = settings.getCurrentFontEncoding();

      TeXObject arg = stack.popArg(parser);

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser, stack);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      FontEncoding newEncoding = sty.getEncoding(arg.toString(parser));

      settings.setFontEncoding(newEncoding);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXSettings settings = parser.getSettings();
      orgEncoding = settings.getCurrentFontEncoding();

      TeXObject arg = parser.popNextArg();

      if (arg instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)arg).expandfully(parser);

         if (expanded != null)
         {
            arg = expanded;
         }
      }

      FontEncoding newEncoding = sty.getEncoding(arg.toString(parser));

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
   private FontEncSty sty;
}
