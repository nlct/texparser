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
import java.util.Locale;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.html.*;

public class FlowFrameData
{
   public FlowFrameData(FlowFramSty sty, FlowFrameType type, String label, int id,
      boolean bordered, TeXDimension width, TeXDimension height,
      TeXDimension posX, TeXDimension posY)
   {
      if (type == null)
      {
         throw new NullPointerException();
      }

      this.sty = sty;
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
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof FlowFrameData)) return false;

      FlowFrameData data = (FlowFrameData)obj;

      return type == data.type && id == data.id && label.equals(data.label);
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
      if (newLabel == null)
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
         break;
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
         break;
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

   public void setCss()
   {
      this.css = css;
   }

   public String getCss()
   {
      return css;
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

   public void writeCss(L2HConverter l2h)
     throws IOException
   {
      l2h.write("div."+type+label);
      l2h.write(", div."+type+id);
      l2h.writeln("{");

      l2h.writeliteral(String.format(" width: %s;", l2h.getHtmlDimension(width)));
      l2h.writeliteral(String.format(" height: %s;", l2h.getHtmlDimension(height)));

      if (textCol != null)
      {
         l2h.writeliteral(String.format(" color: %s;%n",
           l2h.getHtmlColor(textCol)));
      }

      if (backCol != null)
      {
         l2h.writeliteral(String.format(" background-color: %s;%n",
           l2h.getHtmlColor(backCol)));
      }

      if (bordered)
      {
         if ("shadowbox".equals(frameCsName))
         {
            l2h.writeliteral(" box-shadow: ");

            if (borderCol == null)
            {
               l2h.writeliteral("black");
            }
            else
            {
               l2h.writeliteral(l2h.getHtmlColor(borderCol));
            }

            l2h.writeln(";");
         }
         else
         {
            l2h.writeliteral(" border-style: solid;");
            l2h.writeln();

            if (borderCol != null)
            {
               l2h.writeliteral(String.format(" border-color: %s;%n",
                 l2h.getHtmlColor(borderCol)));
            }

            if ("ovalbox".equals(frameCsName))
            {
               l2h.writeliteral(" border-radius: 10%;");
            }
         }
      }

      if (hasAngle())
      {
         String transform = String.format("rotate(%fdeg)", -angle.doubleValue());
         l2h.writeliteral(String.format(
          "transform: %s; -ms-transform: %s; -webkit-transform: %s;",
          transform, transform, transform));
      }

      if (css != null)
      {
         l2h.writeliteral(css);
         l2h.writeln();
      }

      l2h.writeln("}");
   }

   public void showContent(TeXParser parser, TeXObjectList stack,
    KeyValList opts)
     throws IOException
   {
      boolean useDiv = true;
      boolean l2hImg = false;
      String imgType = "image/png";
      TeXObject alt = null;
      String cssStyle = null;
      String cssClass = null;

      if (content == null || content.isEmpty()) return;

      if (hasShape())
      {
         l2hImg = true;
      }

      alt = opts.getValue("alt");

      String val = opts.getString("mime-type", parser, stack);

      if (val != null)
      {
         imgType = val;
      }

      Boolean bool = opts.getBoolean("image", parser, stack);

      if (bool != null)
      {
         l2hImg = bool.booleanValue();
      }

      bool = opts.getBoolean("div", parser, stack);

      if (bool != null)
      {
         useDiv = bool.booleanValue();
      }

      cssStyle = opts.getString("style", parser, stack);

      cssClass = opts.getString("class", parser, stack);

      String imgName = opts.getString("name", parser, stack);

      process(parser, stack, l2hImg, imgName, alt, imgType, 
      useDiv, cssStyle, cssClass);
   }

   public void process(TeXParser parser, TeXObjectList stack,
       boolean l2hImg, String imgName, TeXObject alt, String imgType,
       boolean useDiv, String cssStyle, String cssClass)
     throws IOException
   {
      if (content != null)
      {
         TeXParserListener listener = parser.getListener();
         TeXObjectList list;

         if (listener instanceof L2HConverter)
         {
            parser.startGroup();

            String prefix = type.toString().toLowerCase();

            L2HConverter l2h = (L2HConverter)listener;

            list = listener.createStack();
            StartElement startElem = null;
            EndElement endElem = null;

            if (useDiv)
            {
               startElem = new StartElement("div", true, true);
               endElem = new EndElement("div", true, true);

               if (cssStyle != null)
               {
                  startElem.putAttribute("style", cssStyle);
               }

               if (cssClass != null)
               {
                  startElem.putAttribute("class", cssClass);
               }
            }

            if (l2hImg)
            {
               if (startElem != null)
               {
                  TeXParserUtils.process(startElem, parser, stack);
               }

               StringBuilder builder = new StringBuilder();

               if (style != null)
               {
                  builder.append("\\csname ");
                  builder.append(style);
                  builder.append("\\endcsname");
                  builder.append('{');
               }

               builder.append("\\begin{minipage}[");
               builder.append(vAlign);
               builder.append(']');
               builder.append('[');

               String widthStr = null;
               String heightStr = null;

               FrameHtmlOptions fho = sty.getCurrentFrameHtmlOptions();

               if (fho != null)
               {
                  widthStr = fho.width;
                  heightStr = fho.height;
               }

               if (heightStr == null || heightStr.isEmpty())
               {
                  builder.append(height.toString(parser));
               }
               else
               {
                  builder.append(heightStr);
               }

               builder.append(']');
               builder.append('{');

               if (widthStr == null || widthStr.isEmpty())
               {
                  builder.append(width.toString(parser));
               }
               else
               {
                  builder.append(widthStr);
               }

               builder.append('}');

               if (shape != null)
               {
                  builder.append(shape.toString(parser));

                  if (parser.isStack(content))
                  {
                     TeXObjectList contentList = (TeXObjectList)content;

                     for (int i = 0; i < contentList.size(); i++)
                     {
                        TeXObject obj = contentList.get(i);

                        if (obj.isPar())
                        {
                           contentList.set(i, new TeXCsRef("FLFsimpar"));
                        }
                     }
                  }
               }

               builder.append(content.toString(parser));

               builder.append("\\end{minipage}");

               if (style != null)
               {
                  builder.append('}');
               }

               StringBuilder preamble = new StringBuilder(l2h.getImagePreamble());
               preamble.append("\\onecolumn");
               preamble.append("\\pagestyle{empty}");

               String name;

               if (imgName == null)
               {
                  name = prefix;

                  if (imgNum > 0)
                  {
                     name += imgNum;
                  }

                  imgNum++;
               }
               else
               {
                  name = imgName;
               }

               L2HImage image = l2h.toImage(preamble.toString(), builder.toString(),
                 imgType, alt, name, true, null);

               if (image != null)
               {
                  image.process(parser);
               }
               else
               {
                  listener.getWriteable().writeliteral(
                    String.format("<!-- Image %s Creation Failed -->", name));
                  alt.process(parser, stack);
                  listener.getWriteable().writeliteral(
                     String.format("<!-- End of Image %s Alt Block -->", name));
               }

               if (endElem != null)
               {
                  TeXParserUtils.process(endElem, parser, stack);
               }
            }
            else
            {
               if (startElem != null)
               {
                  list.add(startElem);

                  if (cssClass == null)
                  {
                     startElem.putAttribute("class", type+label+" "+type+id);
                  }
               }

               list.add((TeXObject)content.clone(), true);

               if (endElem != null)
               {
                  list.add(endElem);
               }

               TeXParserUtils.process(list, parser, stack);
            }

            parser.endGroup();
         }
         else
         {
            TeXObjectList contentList = TeXParserUtils.toList(
              (TeXObject)content.clone(), parser);

            if (hasShape())
            {
               contentList.push((TeXObject)shape.clone(), true);
            }

            // TODO check for colours

            contentList.push(TeXParserUtils.createGroup(listener,
             width));

            contentList.push(listener.getOther(']'));
            contentList.push(height);
            contentList.push(listener.getOther('['));

            contentList.push(listener.getOther(']'));
            contentList.push(listener.getLetter(vAlign));
            contentList.push(listener.getOther('['));

            contentList.push(listener.createGroup("minipage"));
            contentList.push(listener.getControlSequence("begin"));

            contentList.add(listener.getControlSequence("begin"));
            contentList.add(listener.createGroup("minipage"));

            if (bordered && frameCsName != null)
            {
               list = listener.createStack();
               list.add(listener.getControlSequence(frameCsName));
               list.add(TeXParserUtils.createGroup(listener,
                  contentList));
            }
            else
            {
               list = contentList;
            }

            if (angle != null)
            {
               contentList = list;

               list = TeXParserUtils.createStack(listener,
                listener.getControlSequence("rotatebox"),
                TeXParserUtils.createGroup(listener, angle),
                TeXParserUtils.createGroup(listener, contentList)
              );
            }

            TeXParserUtils.process(list, parser, stack);
         }
      }
   }

   public String toString()
   {
      return String.format("%s[type=%s,id=%d,label=%s]",
       getClass().getSimpleName(), type, id, label);
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
   FlowFramSty sty;

   String css;
   int imgNum=0;
}
