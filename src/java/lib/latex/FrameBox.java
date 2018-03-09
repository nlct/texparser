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

   public FrameBox(String name)
   {
      this(name, BORDER_SOLID, ALIGN_DEFAULT, ALIGN_DEFAULT, true);
   }

   public FrameBox(String name, byte style, byte halign, byte valign, 
      boolean isinline)
   {
      this(name, style, halign, valign, isinline, null, null);
   }

   public FrameBox(String name, byte style, byte halign, byte valign, 
      boolean isinline, TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name);
      setStyle(style);
      setHAlign(halign);
      setVAlign(valign);
      setIsInLine(isinline);
      currentBorderWidth = borderWidth;
      currentInnerMargin = innerMargin;
   }

   public Object clone()
   {
      return new FrameBox(getName(), style, halign, valign, isInline,
        currentBorderWidth == null ? null : 
          (TeXDimension)currentBorderWidth.clone(),
        currentInnerMargin == null ? null : 
          (TeXDimension)currentInnerMargin.clone()
      );
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
      return currentBorderColor;
   }

   public Color getForegroundColor(TeXParser parser) throws IOException
   {
      return currentFgColor;
   }

   public Color getBackgroundColor(TeXParser parser) throws IOException
   {
      return currentBgColor;
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return currentBorderWidth == null ? parser.getDimenRegister("fboxrule")
        : currentBorderWidth;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return currentInnerMargin == null ? parser.getDimenRegister("fboxsep")
        : currentInnerMargin;
   }

   public TeXDimension getWidth(TeXParser parser) throws IOException
   {
      return currentWidth;
   }

   public TeXDimension getHeight(TeXParser parser) throws IOException
   {
      return currentHeight;
   }

   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject width = null;

      if (parser == stack)
      {
         width = parser.popNextArg('[', ']');
      }
      else
      {
         width = stack.popArg(parser, '[', ']');
      }

      if (width != null)
      {
         if (width instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)width).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)width).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               width = expanded;
            }
         }

         if (width instanceof TeXObjectList)
         {
            width = ((TeXObjectList)width).popDimension(parser);
         }

         TeXObject pos = null;

         if (parser == stack)
         {
            pos = parser.popNextArg('[', ']');
         }
         else
         {
            pos = stack.popArg(parser, '[', ']');
         }

         if (pos != null)
         {
            if (pos instanceof Expandable)
            {
               TeXObjectList expanded;

               if (parser == stack)
               {
                  expanded = ((Expandable)pos).expandfully(parser);
               }
               else
               {
                  expanded = ((Expandable)pos).expandfully(parser, stack);
               }

               if (expanded != null)
               {
                  pos = expanded;
               }
            }

            String val = pos.toString(parser).trim();

            if (val.equals("c"))
            {
               halign = ALIGN_CENTER;
            }
            else if (val.equals("l"))
            {
               halign = ALIGN_LEFT;
            }
            else if (val.equals("r"))
            {
               halign = ALIGN_RIGHT;
            }
            else
            {
               TeXApp texApp = parser.getListener().getTeXApp();

               texApp.warning(parser, texApp.getMessage(
                 LaTeXSyntaxException.ILLEGAL_ARG_TYPE, val));
            }
         }
      }

      if (width instanceof TeXDimension)
      {
         currentWidth = (TeXDimension)width;
      }

   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXDimension orgWidth = currentWidth;
      TeXDimension orgHeight = currentHeight;
      byte orgHalign = halign;
      byte orgValign = valign;
      Color orgBorderColor = currentBorderColor;
      Color orgFgColor = currentFgColor;
      Color orgBgColor = currentBgColor;
      TeXDimension orgBorderWidth = currentBorderWidth;
      TeXDimension orgInnerMargin = currentInnerMargin;

      popSettings(parser, stack);

      TeXObject arg;

      if (parser == stack)
      {
         arg = parser.popNextArg();
      }
      else
      {
         arg = stack.popArg(parser);
      }

      LaTeXParserListener listener = ((LaTeXParserListener)parser.getListener());

      listener.startFrameBox(this);

      try
      {
         arg.process(parser, stack);
      }
      finally
      {
         try
         {
            listener.endFrameBox(this);
         }
         finally
         {
            currentWidth = orgWidth;
            currentHeight = orgHeight;
            currentBorderColor = orgBorderColor;
            currentFgColor = orgFgColor;
            currentBgColor = orgBgColor;
            currentBorderWidth = orgBorderWidth;
            currentInnerMargin = orgInnerMargin;
            halign = orgHalign;
            valign = orgValign;
         }
      }
   }

   protected TeXDimension currentWidth=null;
   protected TeXDimension currentHeight=null;
   protected TeXDimension currentBorderWidth = null;
   protected TeXDimension currentInnerMargin = null;

   protected Color currentBorderColor=null;
   protected Color currentFgColor=null;
   protected Color currentBgColor=null;

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
