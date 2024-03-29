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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.util.Vector;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ColourBox extends FrameBox
{
   public ColourBox(String name)
   {
      this(name, BorderStyle.SOLID, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);
   }

   public ColourBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
      boolean isinline)
   {
      this(name, style, halign, valign, isinline, null, null);
   }

   public ColourBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
      boolean isinline, TeXDimension borderWidth, TeXDimension innerMargin)
   {
      this(name, style, halign, valign, isinline, false, borderWidth, innerMargin);
   }

   public ColourBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
      boolean isinline, boolean isMultiLine, 
      TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name);
      setStyle(style);
      setHAlign(halign);
      setVAlign(valign);
      setIsInLine(isinline);
      setIsMultiLine(isMultiLine);
      currentBorderWidth = borderWidth;
      currentInnerMargin = innerMargin;
      id = name;
   }

   @Override
   public FrameBox createBox()
   {
      return new ColourBox(getName());
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return currentBorderWidth;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return currentInnerMargin;
   }

   @Override
   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
   }
}
