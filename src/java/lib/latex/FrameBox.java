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
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;

public class FrameBox extends ControlSequence
{
   public FrameBox()
   {
      this("fbox", BORDER_SOLID, ALIGN_DEFAULT, ALIGN_DEFAULT, true);
   }

   public FrameBox(String name, byte style, byte halign, byte valign, 
      boolean isinline)
   {
      super(name);
      setStyle(style);
      setHAlign(halign);
      setVAlign(valign);
      setIsInLine(isinline);
   }

   public Object clone()
   {
      return new FrameBox(getName(), style, halign, valign, isInline);
   }

   public boolean isInLine()
   {
      return isInline;
   }

   public void setIsInLine(boolean isinline)
   {
      this.isInline = isinline;
   }

   public byte getStyle()
   {
      return style;
   }

   public void setStyle(byte newStyle)
   {
      switch (newStyle)
      {
         case BORDER_NONE:
         case BORDER_SOLID:
         case BORDER_DOUBLE:
           style = newStyle;
         break;
         default:
            throw new IllegalArgumentException("Invalid style "+newStyle);
      }
   }

   public byte getHAlign()
   {
      return halign;
   }

   public void setHAlign(byte newAlign)
   {
      switch (newAlign)
      {
         case ALIGN_DEFAULT:
         case ALIGN_LEFT:
         case ALIGN_CENTER:
         case ALIGN_RIGHT:
           halign = newAlign;
         break;
         default:
            throw new IllegalArgumentException(
               "Invalid horizontal alignment "+newAlign);
      }
   }

   public byte getVAlign()
   {
      return valign;
   }

   public void setVAlign(byte newAlign)
   {
      switch (newAlign)
      {
         case ALIGN_DEFAULT:
         case ALIGN_TOP:
         case ALIGN_MIDDLE:
         case ALIGN_BOTTOM:
         case ALIGN_BASE:
           valign = newAlign;
         break;
         default:
            throw new IllegalArgumentException(
              "Invalid vertical alignment "+newAlign);
      }
   }

   public Color getBorderColor(TeXParser parser) throws IOException
   {
      return null;
   }

   public Color getForegroundColor(TeXParser parser) throws IOException
   {
      return null;
   }

   public Color getBackgroundColor(TeXParser parser) throws IOException
   {
      return null;
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return parser.getDimenRegister("fboxrule");
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return parser.getDimenRegister("fboxsep");
   }

   public TeXDimension getWidth(TeXParser parser) throws IOException
   {
      return null;
   }

   public TeXDimension getHeight(TeXParser parser) throws IOException
   {
      return null;
   }

   public void process(TeXParser parser) throws IOException
   {
      TeXObject arg1 = parser.popNextArg();

      LaTeXParserListener listener = ((LaTeXParserListener)parser.getListener());

      listener.startFrameBox(this);
      arg1.process(parser);
      listener.endFrameBox(this);
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      TeXObject arg1 = list.popArg(parser);

      LaTeXParserListener listener = ((LaTeXParserListener)parser.getListener());

      listener.startFrameBox(this);
      arg1.process(parser, list);
      listener.endFrameBox(this);
   }

   private byte style = BORDER_SOLID;
   private byte halign = ALIGN_DEFAULT;
   private byte valign = ALIGN_DEFAULT;
   private boolean isInline = true;

   public static final byte BORDER_NONE=0;
   public static final byte BORDER_SOLID=1;
   public static final byte BORDER_DOUBLE=2;

   public static final byte ALIGN_DEFAULT=0;

   public static final byte ALIGN_LEFT=1;
   public static final byte ALIGN_CENTER=2;
   public static final byte ALIGN_RIGHT=3;

   public static final byte ALIGN_TOP=1;
   public static final byte ALIGN_MIDDLE=2;
   public static final byte ALIGN_BOTTOM=3;
   public static final byte ALIGN_BASE=4;
}
