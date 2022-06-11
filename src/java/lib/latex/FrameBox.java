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
      this("fbox", BorderStyle.SOLID, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);
   }

   public FrameBox(String name)
   {
      this(name, BorderStyle.SOLID, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true);
   }

   @Deprecated
   public FrameBox(String name, byte style, byte halign, byte valign, 
      boolean isinline)
   {
      this(name, style, halign, valign, isinline, null, null);
   }

   public FrameBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
      boolean isinline)
   {
      this(name, style, halign, valign, isinline, null, null);
   }

   @Deprecated
   public FrameBox(String name, byte style, byte halign, byte valign, 
      boolean isinline, TeXDimension borderWidth, TeXDimension innerMargin)
   {
      this(name, style, halign, valign, isinline, false, borderWidth, innerMargin);
   }

   public FrameBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
      boolean isinline, TeXDimension borderWidth, TeXDimension innerMargin)
   {
      this(name, style, halign, valign, isinline, false, borderWidth, innerMargin);
   }

   @Deprecated
   public FrameBox(String name, byte style, byte halign, byte valign, 
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

   public FrameBox(String name, BorderStyle style, AlignHStyle halign, AlignVStyle valign, 
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

   public FrameBox createBox()
   {
      return new FrameBox(getName());
   }

   public Object clone()
   {
      FrameBox box = createBox();

      copySettingsInto(box);

      return box;
   }

   protected void copySettingsInto(FrameBox box)
   {
      box.isChangeable = isChangeable;

      box.id = id;
      box.isInline = isInline;
      box.isMultiLine = isMultiLine;
      box.style = style;
      box.halign = halign;
      box.valign = valign;
      box.currentAngle = currentAngle;

      if (currentBorderWidth == null)
      {
         box.currentBorderWidth = null;
      }
      else
      {
         box.currentBorderWidth = (TeXDimension)currentBorderWidth.clone();
      }

      if (currentInnerMargin == null)
      {
         box.currentInnerMargin = null;
      }
      else
      {
         box.currentInnerMargin = (TeXDimension)currentInnerMargin.clone();
      }

      if (currentOuterMarginLeft == null)
      {
         box.currentOuterMarginLeft = null;
      }
      else
      {
         box.currentOuterMarginLeft = (TeXDimension)currentOuterMarginLeft.clone();
      }

      if (currentOuterMarginRight == null)
      {
         box.currentOuterMarginRight = null;
      }
      else
      {
         box.currentOuterMarginRight = (TeXDimension)currentOuterMarginRight.clone();
      }

      if (currentOuterMarginTop == null)
      {
         box.currentOuterMarginTop = null;
      }
      else
      {
         box.currentOuterMarginTop = (TeXDimension)currentOuterMarginTop.clone();
      }

      if (currentOuterMarginBottom == null)
      {
         box.currentOuterMarginBottom = null;
      }
      else
      {
         box.currentOuterMarginBottom = (TeXDimension)currentOuterMarginBottom.clone();
      }

      if (currentBorderRadius == null)
      {
         box.currentBorderRadius = null;
      }
      else
      {
         box.currentBorderRadius = (TeXDimension)currentBorderRadius.clone();
      }

      if (currentWidth == null)
      {
         box.currentWidth = null;
      }
      else
      {
         box.currentWidth = (TeXDimension)currentWidth.clone();
      }

      if (currentHeight == null)
      {
         box.currentHeight = null;
      }
      else
      {
         box.currentHeight = (TeXDimension)currentHeight.clone();
      }

      box.currentBorderColor = currentBorderColor;
      box.currentFgColor = currentFgColor;
      box.currentBgColor = currentBgColor;

      box.textFont = textFont;

      box.prefix = prefix;
      box.suffix = suffix;
   }

   public void setPrefix(TeXObject prefix)
   {
      this.prefix = prefix;
   }

   public void setSuffix(TeXObject suffix)
   {
      this.suffix = suffix;
   }

   public boolean isStyleChangeable()
   {
      return isChangeable;
   }

   public void fixStyle()
   {
      isChangeable = false;
   }

   public boolean isInLine()
   {
      return isInline;
   }

   public void setIsInLine(boolean isinline)
   {
      if (isChangeable)
      {
         this.isInline = isinline;
      }
   }

   public boolean isMultiLine()
   {
      return isMultiLine;
   }

   public void setIsMultiLine(boolean isMultiLine)
   {
      if (isChangeable)
      {
         this.isMultiLine = isMultiLine;
      }
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   public BorderStyle getStyle()
   {
      return style;
   }

   public void setStyle(byte newStyle)
   {
      switch (newStyle)
      {
         case BORDER_NONE:
            style = BorderStyle.NONE;
         break;
         case BORDER_SOLID:
            style = BorderStyle.SOLID;
         break;
         case BORDER_DOUBLE:
            style = BorderStyle.DOUBLE;
         break;
         default:
            throw new IllegalArgumentException("Invalid style "+newStyle);
      }
   }

   public void setStyle(BorderStyle newStyle)
   {
      if (isChangeable)
      {
         style = newStyle;
      }
   }

   public AlignHStyle getHAlign()
   {
      return halign;
   }

   public void setHAlign(AlignHStyle newAlign)
   {
      if (isChangeable)
      {
         halign = newAlign;
      }
   }

   @Deprecated
   public void setHAlign(byte newAlign)
   {
      if (isChangeable)
      {
         switch (newAlign)
         {
            case ALIGN_DEFAULT:
              halign=AlignHStyle.DEFAULT;
            break;
            case ALIGN_LEFT:
              halign=AlignHStyle.LEFT;
            break;
            case ALIGN_CENTER:
              halign=AlignHStyle.CENTER;
            break;
            case ALIGN_RIGHT:
              halign = AlignHStyle.RIGHT;
            break;
            default:
               throw new IllegalArgumentException(
                  "Invalid horizontal alignment "+newAlign);
         }
      }
   }

   public AlignVStyle getVAlign()
   {
      return valign;
   }

   public void setVAlign(AlignVStyle newAlign)
   {
      if (isChangeable)
      {
         valign = newAlign;
      }
   }

   @Deprecated
   public void setVAlign(byte newAlign)
   {
      if (isChangeable)
      {
         switch (newAlign)
         {
            case ALIGN_DEFAULT:
              valign = AlignVStyle.DEFAULT;
            break;
            case ALIGN_TOP:
              valign = AlignVStyle.TOP;
            break;
            case ALIGN_MIDDLE:
              valign = AlignVStyle.MIDDLE;
            break;
            case ALIGN_BOTTOM:
              valign = AlignVStyle.BOTTOM;
            break;
            case ALIGN_BASE:
              valign = AlignVStyle.BASE;
            break;
            default:
               throw new IllegalArgumentException(
                 "Invalid vertical alignment "+newAlign);
         }
      }
   }

   public void setBorderColor(Color col)
   {
      if (isChangeable)
      {
         currentBorderColor = col;
      }
   }

   public void setForegroundColor(Color col)
   {
      if (isChangeable)
      {
         currentFgColor = col;
      }
   }

   public void setBackgroundColor(Color col)
   {
      if (isChangeable)
      {
         currentBgColor = col;
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
      if (isChangeable)
      {
         return currentBorderWidth == null ? parser.getDimenRegister("fboxrule")
           : currentBorderWidth;
      }
      else
      {
         return currentBorderWidth;
      }
   }

   public void setBorderRadius(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentBorderRadius = dim;
      }
   }

   public TeXDimension getBorderRadius(TeXParser parser) throws IOException
   {
      return currentBorderRadius;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      if (isChangeable)
      {
         return currentInnerMargin == null ? parser.getDimenRegister("fboxsep")
           : currentInnerMargin;
      }
      else
      {
         return currentInnerMargin;
      }
   }

   public TeXDimension getOuterMarginLeft(TeXParser parser) throws IOException
   {
      return currentOuterMarginLeft;
   }

   public TeXDimension getOuterMarginRight(TeXParser parser) throws IOException
   {
      return currentOuterMarginRight;
   }

   public TeXDimension getOuterMarginTop(TeXParser parser) throws IOException
   {
      return currentOuterMarginTop;
   }

   public TeXDimension getOuterMarginBottom(TeXParser parser) throws IOException
   {
      return currentOuterMarginBottom;
   }

   public void setOuterMarginLeft(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentOuterMarginLeft = dim;
      }
   }

   public void setOuterMarginRight(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentOuterMarginRight = dim;
      }
   }

   public void setOuterMarginTop(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentOuterMarginTop = dim;
      }
   }

   public void setOuterMarginBottom(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentOuterMarginBottom = dim;
      }
   }

   public TeXDimension getWidth(TeXParser parser) throws IOException
   {
      return currentWidth;
   }

   public void setWidth(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentWidth = dim;
      }
   }

   public TeXDimension getHeight(TeXParser parser) throws IOException
   {
      return currentHeight;
   }

   public void setHeight(TeXDimension dim)
   {
      if (isChangeable)
      {
         currentHeight = dim;
      }
   }

   public Angle getAngle(TeXParser parser) throws IOException
   {
      return currentAngle;
   }

   public void setAngle(Angle angle)
   {
      if (isChangeable)
      {
         currentAngle = angle;
      }
   }

   public TeXFontText getTextFont()
   {
      return textFont;
   }

   public void setTextFont(TeXFontText textFont)
   {
      this.textFont = textFont;
   }

   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject width = popOptArg(parser, stack);
      String pos = null;

      if (width != null)
      {
         if (!(width instanceof TeXDimension))
         {
            width = TeXParserUtils.expandFully(width, parser, stack);

            if (width instanceof TeXObjectList)
            {
               width = ((TeXObjectList)width).popDimension(parser);
            }
         }

         pos = popOptLabelString(parser, stack);
      }

      if (pos != null)
      {
         String val = pos.trim();

         if (val.equals("c"))
         {
            halign = AlignHStyle.CENTER;
         }
         else if (val.equals("l"))
         {
            halign = AlignHStyle.LEFT;
         }
         else if (val.equals("r"))
         {
            halign = AlignHStyle.RIGHT;
         }
         else
         {
            TeXApp texApp = parser.getListener().getTeXApp();

            texApp.warning(parser, texApp.getMessage(
              LaTeXSyntaxException.ILLEGAL_ARG_TYPE, val));
         }
      }

      if (width instanceof TeXDimension)
      {
         currentWidth = (TeXDimension)width;
      }
   }

   protected TeXObject popContents(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return popArg(parser, stack);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      FrameBox fbox = this;

      if (isChangeable)
      {
         TeXDimension orgWidth = currentWidth;
         TeXDimension orgHeight = currentHeight;
         AlignHStyle orgHalign = halign;
         AlignVStyle orgValign = valign;
         Color orgBorderColor = currentBorderColor;
         Color orgFgColor = currentFgColor;
         Color orgBgColor = currentBgColor;
         TeXDimension orgBorderWidth = currentBorderWidth;
         TeXDimension orgInnerMargin = currentInnerMargin;
         TeXDimension orgBorderRadius = currentBorderRadius;
         Angle orgAngle = currentAngle;

         popSettings(parser, stack);

         fbox = (FrameBox)clone();

         currentWidth = orgWidth;
         currentHeight = orgHeight;
         currentBorderColor = orgBorderColor;
         currentFgColor = orgFgColor;
         currentBgColor = orgBgColor;
         currentBorderWidth = orgBorderWidth;
         currentBorderRadius = orgBorderRadius;
         currentInnerMargin = orgInnerMargin;
         halign = orgHalign;
         valign = orgValign;
         currentAngle = orgAngle;
      }

      TeXObject arg = popContents(parser, stack);

      stack.push(new EndFrameBox(fbox));

      if (suffix != null)
      {
         stack.push((TeXObject)suffix.clone(), true);
      }

      stack.push(arg, true);

      if (prefix != null)
      {
         stack.push((TeXObject)prefix.clone(), true);
      }

      stack.push(new StartFrameBox(fbox));
   }

   protected String id;

   protected TeXDimension currentWidth=null;
   protected TeXDimension currentHeight=null;
   protected TeXDimension currentBorderWidth = null;
   protected TeXDimension currentInnerMargin = null;
   protected TeXDimension currentBorderRadius = null;

   protected TeXDimension currentOuterMarginLeft = null;
   protected TeXDimension currentOuterMarginRight = null;
   protected TeXDimension currentOuterMarginTop = null;
   protected TeXDimension currentOuterMarginBottom = null;

   protected Color currentBorderColor=null;
   protected Color currentFgColor=null;
   protected Color currentBgColor=null;

   protected BorderStyle style = BorderStyle.SOLID;
   protected AlignHStyle halign = AlignHStyle.DEFAULT;
   protected AlignVStyle valign = AlignVStyle.DEFAULT;

   protected Angle currentAngle = null;

   protected TeXFontText textFont = null;

   protected TeXObject prefix = null;
   protected TeXObject suffix = null;

   protected boolean isInline = true;
   protected boolean isMultiLine = true;
   protected boolean isChangeable = true;

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
