/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;

public class FlowFrameData extends AbstractTeXObject
{
   public FlowFrameData(FlowFrameType type, String label, int id,
      boolean bordered, TeXDimension width, TeXDimension height,
      TeXDimension posX, TeXDimension posY)
   {
      if (type == null)
      {
         throw new NullPointerException();
      }

      this.type = type;
      this.id = id;
      this.bordered = bordered;

      setLabel(label);
      setWidth(width);
      setHeight(height);
      setX(posX);
      setY(posY);
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public Object clone()
   {
      return this;
   }

   @Override
   public String format()
   {
      return "";
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return parser.getListener().createStack();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return "";
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof FlowFrameData)) return false;

      FlowFrameData data = (FlowFrameData)obj;

      return type == data.type && label.equals(data.label);
   }

   public FlowFrameType getType()
   {
      return type;
   }

   public String getLabel()
   {
      return label;
   }

   public void setLabel(String newLabel)
   {
      if (label == null)
      {
         throw new NullPointerException();
      }

      this.label = newLabel;
   }

   public int getID()
   {
      return id;
   }

   public boolean isBordered()
   {
      return bordered;
   }

   public void setBordered(boolean bordered)
   {
      this.bordered = bordered;
   }

   public void setFrameBorderCsName(String csname)
   {
      if (csname.equals("plain"))
      {
         bordered = true;
         frameCsName = "fbox";
      }
      else if (csname.equals("none") || csname.equals("relax") || csname.isEmpty())
      {
         bordered = false;
         frameCsName = "";
      }
      else
      {
         bordered = true;
         frameCsName = csname;
      }
   }

   public String getFrameBorderCsName()
   {
      return frameCsName;
   }

   public String getPageList()
   {
      return pages;
   }

   public void setPageList(String pages)
   {
      this.pages = pages;
   }

   public String getExcludedPageList()
   {
      return excludePages;
   }

   public void setExcludedPageList(String list)
   {
      this.excludePages = list;
   }

   public TeXDimension getWidth()
   {
      return width;
   }

   public void setWidth(TeXDimension width)
   {
      if (width == null)
      {
         throw new NullPointerException();
      }

      this.width = width;
   }

   public TeXDimension getHeight()
   {
      return height;
   }

   public void setHeight(TeXDimension height)
   {
      if (height == null)
      {
         throw new NullPointerException();
      }

      this.height = height;
   }

   public TeXDimension getX()
   {
      return posX;
   }

   public void setX(TeXDimension x)
   {
      if (x == null)
      {
         throw new NullPointerException();
      }

      posX = x;
   }

   public TeXDimension getY()
   {
      return posY;
   }

   public void setY(TeXDimension y)
   {
      if (y == null)
      {
         throw new NullPointerException();
      }

      posY = y;
   }

   public TeXDimension getOddX()
   {
      return oddPosX;
   }

   public void setOddX(TeXDimension x)
   {
      oddPosX = x;
   }

   public TeXDimension getOddY()
   {
      return oddPosY;
   }

   public void setOddY(TeXDimension y)
   {
      oddPosY = y;
   }

   public TeXDimension getEvenX()
   {
      return evenPosX;
   }

   public void setEvenX(TeXDimension x)
   {
      evenPosX = x;
   }

   public TeXDimension getEvenY()
   {
      return evenPosY;
   }

   public void setEvenY(TeXDimension y)
   {
      evenPosY = y;
   }

   public void swapEvenOdd()
   {
      TeXDimension dim = evenPosX;
      evenPosX = oddPosX;
      oddPosX = dim;
      dim = evenPosY;
      evenPosY = oddPosY;
      oddPosY = dim;
   }

   public TeXDimension getOffset()
   {
      return offset;
   }

   public void setOffset(TeXDimension offset)
   {
      this.offset = offset;
   }

   public void setVAlign(char align)
   throws IllegalArgumentException
   {
      switch (type)
      {
         case STATIC:
         case DYNAMIC:
            if (align == 'c' || align == 't' || align == 'b')
            {
               this.vAlign = align;
            }
            else
            {
               throw new IllegalArgumentException("Invalid valign "+align);
            }
         break;
         default:
           throw new IllegalArgumentException(
            "valign not permitted for frame type "+type);
      }
   }

   public void setBorderColor(Color c)
   {
      this.borderCol = c;
   }

   public Color getBorderColor()
   {
      return borderCol;
   }

   public void setTextColor(Color c)
   {
      this.textCol = c;
   }

   public Color getTextColor()
   {
      return textCol;
   }

   public void setBackColor(Color c)
   {
      this.backCol = c;
   }

   public Color getBackColor()
   {
      return backCol;
   }

   public boolean isHidden()
   {
      return hidden;
   }

   public void setHidden(boolean hidden)
   {
      switch (type)
      {
         case STATIC:
         case DYNAMIC:
           this.hidden = hidden;
         default:
           throw new IllegalArgumentException(
            "hide not permitted for frame type "+type);
      }
   }

   public boolean isClearOn()
   {
      return clear;
   }

   public void setClear(boolean on)
   throws IllegalArgumentException
   {
      switch (type)
      {
         case STATIC:
         case DYNAMIC:
           this.clear = on;
         default:
           throw new IllegalArgumentException(
            "clear not permitted for frame type "+type);
      }
   }

   public void setMarginSide(String option) 
   throws IllegalArgumentException
   {
      MarginSide marginSide = MarginSide.valueOf(option.toUpperCase());

      if (marginSide == null)
      {
         throw new IllegalArgumentException("Invalid margin value "+option);
      }

      setMarginSide(marginSide);
   }

   public void setMarginSide(MarginSide marginSide) 
   throws IllegalArgumentException
   {
      if (type == FlowFrameType.FLOW)
      {
         this.marginSide = marginSide;
      }
      else
      {
         throw new IllegalArgumentException(
          "margin not permitted for frame type "+type);
      }
   }

   public String getStyle()
   {
      return style;
   }

   public boolean hasStyle()
   {
      if (type == FlowFrameType.DYNAMIC)
      {
         return style != null && !style.isEmpty();
      }
      else
      {
         return false;
      }
   }

   public void setStyle(String csname) 
   throws IllegalArgumentException
   {
      if (type == FlowFrameType.DYNAMIC)
      {
         this.style = style;
      }
      else
      {
         throw new IllegalArgumentException(
          "style not permitted for frame type "+type);
      }
   }

   public void setAngle(TeXNumber angle)
   {
      this.angle = angle;
   }

   public TeXNumber getAngle()
   {
      return angle;
   }

   public boolean hasAngle()
   {
      return angle != null && angle.getValue() != 0;
   }

   public TeXObject getShape()
   {
      return shape;
   }

   public boolean hasShape()
   {
      return (! (shape == null || shape.isEmpty() ) );
   }

   public void setShape(TeXObject shape)
     throws IllegalArgumentException
   {
      if (type == FlowFrameType.FLOW)
      {
         throw new IllegalArgumentException(
          "shape not permitted for frame type "+type);
      }
      else if (TeXParserUtils.isControlSequence(shape, "relax"))
      {
         this.shape = null;
      }
      else
      {
         this.shape = shape;
      }
   }

   public TeXObject getContent()
   {
      return content;
   }

   public boolean hasContent()
   {
      return (! (content == null || content.isEmpty() ) );
   }

   public void setContent(TeXObject content)
     throws IllegalArgumentException
   {
      if (type == FlowFrameType.FLOW)
      {
         throw new IllegalArgumentException(
          "content not permitted for frame type "+type);
      }
      else
      {
         this.content = content;
      }
   }

   public enum MarginSide
   {
      LEFT, RIGHT, INNER, OUTER;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (content != null)
      {
         TeXParserListener listener = parser.getListener();
         TeXObjectList list;

         if (bordered && frameCsName != null
              && !frameCsName.startsWith("@flf@border@"))
         {
            list = listener.createStack();
            list.add(listener.getControlSequence(frameCsName));
            list.add(TeXParserUtils.createGroup(listener,
               (TeXObject)content.clone()));
         }
         else
         {
            list = TeXParserUtils.toList((TeXObject)content.clone(), parser);
         }

         TeXParserUtils.process(list, parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   FlowFrameType type;
   String label;
   int id;
   String pages = "all";
   String excludePages = "";
   boolean bordered = false;
   boolean hidden = false;
   boolean clear = false;
   String frameCsName = "fbox";
   String style;
   TeXDimension width, height, posX, posY;
   TeXDimension offset, oddPosX, oddPosY, evenPosX, evenPosY;// may be null
   Color borderCol, textCol, backCol;
   char vAlign = 'b';
   MarginSide marginSide;
   TeXNumber angle;
   TeXObject shape;
   TeXObject content;
}
