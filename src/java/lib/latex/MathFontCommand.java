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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class MathFontCommand extends ControlSequence
{
   public MathFontCommand(String name, int style)
   {
      super(name);
      this.style = style;
   }

   public Object clone()
   {
      return new MathFontCommand(getName(), style);
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg = parser.popNextArg();

      TeXSettings settings = parser.getSettings();

      int orgStyle = settings.getCurrentMathFont();

      settings.setMathFont(style);

      arg.process(parser);

      settings.setMathFont(orgStyle);
   }

   public void process(TeXParser parser, TeXObjectList list)
       throws IOException
   {
      TeXObject arg = list.popArg(parser);

      TeXSettings settings = parser.getSettings();

      int orgStyle = settings.getCurrentMathFont();

      settings.setMathFont(style);

      arg.process(parser, list);

      settings.setMathFont(orgStyle);
   }

   public int getStyle()
   {
      return style;
   }

   private int style;
}
