/*
    Copyright (C) 2015 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.image;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.geom.*;

import com.dickimawbooks.texparserlib.*;

public class TeXGraphicsShape
{
   private TeXGraphicsShape()
   {
   }

   public TeXGraphicsShape(TeXGraphicsGroup group)
   {
      this.parser = group.getParser();

      strokeAttributes = 
        (TeXGraphicsStrokeAttributes)group.getStrokeAttributes().clone();
      fillAttributes = 
        (TeXGraphicsFillAttributes)group.getFillAttributes().clone();
      textAttributes = 
        (TeXGraphicsTextAttributes)group.getTextAttributes().clone();
      transformAttributes = 
        (TeXGraphicsTransformAttributes)group.getTransformAttributes().clone();

      this.currentPath = new GeneralPath(fillAttributes.getWindingRule());
   }

   public TeXParser getParser()
   {
      return parser;
   }

   public void moveTo(TeXDimension x, TeXDimension y)
   throws TeXSyntaxException
   {
      currentPath.moveTo(toBp(x), toBp(y));
   }

   public void moveTo(float x, float y)
   throws TeXSyntaxException
   {
      currentPath.moveTo(toX(x), toY(y));
   }

   public void lineTo(TeXDimension x, TeXDimension y)
   throws TeXSyntaxException
   {
      currentPath.lineTo(toBp(x), toBp(y));
   }

   public void lineTo(float x, float y)
   throws TeXSyntaxException
   {
      currentPath.lineTo(toX(x), toY(y));
   }

   public void quadTo(TeXDimension x1, TeXDimension y1,
                      TeXDimension x2, TeXDimension y2)
   throws TeXSyntaxException
   {
      currentPath.quadTo(toBp(x1), toBp(y1), toBp(x2), toBp(y2));
   }

   public void quadTo(float x1, float y1, float x2, float y2)
   throws TeXSyntaxException
   {
      currentPath.quadTo(toX(x1), toY(y1), toX(x2), toY(y2));
   }

   public void curveTo(TeXDimension x1, TeXDimension y1, 
                       TeXDimension x2, TeXDimension y2,
                       TeXDimension x3, TeXDimension y3)
   throws TeXSyntaxException
   {
      currentPath.curveTo(toBp(x1), toBp(y1),
                          toBp(x2), toBp(y2),
                          toBp(x3), toBp(y3));
   }

   public void curveTo(float x1, float y1, float x2, float y2,
     float x3, float y3)
   throws TeXSyntaxException
   {
      currentPath.curveTo(toX(x1), toY(y1), toX(x2), toY(y2), toX(x3), toY(y3));
   }

   public void arcTo(int arcType, float xRadius, float yRadius, 
     float startAngle, float endAngle)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      Arc2D.Float arc = new Arc2D.Float(x, y, 
        2*toX(xRadius), 2*toY(yRadius), startAngle,
        endAngle-startAngle, arcType);

      currentPath.append(arc.getPathIterator(null), true);
   }

   public void arcTo(int arcType, TeXDimension xRadius, TeXDimension yRadius, 
     float startAngle, float endAngle)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      Arc2D.Float arc = new Arc2D.Float(x, y, 2*toBp(xRadius), 2*toBp(yRadius),
        startAngle, endAngle-startAngle, arcType);

      currentPath.append(arc.getPathIterator(null), true);
   }

   public void rectTo(float endX, float endY)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float x1 = toX(endX);
      float y1 = toY(endY);

      float width = x1-x;
      float height = y1-y;

      if (width < 0f)
      {
         x = x1;
         width = -width;
      }

      if (height < 0f)
      {
         y = y1;
         height = -height;
      }

      Rectangle2D.Float rect = new Rectangle2D.Float(x, y, width, height);

      currentPath.append(rect.getPathIterator(null), true);

      currentPath.moveTo(endX, endY);
   }

   public void rectTo(TeXDimension endX, TeXDimension endY)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float x1 = toBp(endX);
      float y1 = toBp(endY);

      float width = x1-x;
      float height = y1-y;

      if (width < 0f)
      {
         x = x1;
         width = -width;
      }

      if (height < 0f)
      {
         y = y1;
         height = -height;
      }

      Rectangle2D.Float rect = new Rectangle2D.Float(x, y, width, height);

      currentPath.append(rect.getPathIterator(null), true);

      currentPath.moveTo(toBp(endX), toBp(endY));
   }

   public void ellipse(float xRadius, float yRadius, float angle)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float xRad = toX(xRadius);
      float yRad = toY(yRadius);

      Shape ellipse = new Ellipse2D.Float(x-xRad, y-yRad, x+xRad, y+yRad);

      AffineTransform af = null;

      if (angle != 0f)
      {
         af = AffineTransform.getRotateInstance(angle, x, y);
      }

      currentPath.append(ellipse.getPathIterator(af), true);

      currentPath.moveTo(x, y);
   }

   public void ellipse(TeXDimension xRadius, TeXDimension yRadius, float angle)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float xRad = toBp(xRadius);
      float yRad = toBp(yRadius);

      Shape ellipse = new Ellipse2D.Float(x-xRad, y-yRad, x+xRad, y+yRad);

      AffineTransform af = null;

      if (angle != 0f)
      {
         af = AffineTransform.getRotateInstance(angle, x, y);
      }

      currentPath.append(ellipse.getPathIterator(af), true);

      currentPath.moveTo(x, y);
   }

   public void circle(float radius) throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float xRad = toX(radius);
      float yRad = toY(radius);

      Shape ellipse = new Ellipse2D.Float(x-xRad, y-yRad, x+xRad, y+yRad);

      currentPath.append(ellipse.getPathIterator(null), true);

      currentPath.moveTo(x, y);
   }

   public void circle(TeXDimension radius) throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float xRad = toBp(radius);
      float yRad = toBp(radius);

      Shape ellipse = new Ellipse2D.Float(x-xRad, y-yRad, x+xRad, y+yRad);

      currentPath.append(ellipse.getPathIterator(null), true);

      currentPath.moveTo(x, y);
   }

   public void roundedRectTo(float endX, float endY, 
      float arcWidth, float arcHeight)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float x1 = toX(endX);
      float y1 = toY(endY);

      float arcW = toX(arcWidth);
      float arcH = toY(arcHeight);

      float width = x1-x;
      float height = y1-y;

      if (width < 0f)
      {
         x = x1;
         width = -width;
      }

      if (height < 0f)
      {
         y = y1;
         height = -height;
      }

      RoundRectangle2D.Float rect = 
         new RoundRectangle2D.Float(x, y, width, height, arcW, arcH);

      currentPath.append(rect.getPathIterator(null), true);

      currentPath.moveTo(endX, endY);
   }

   public void roundedRectTo(TeXDimension endX, TeXDimension endY, 
      TeXDimension arcWidth, TeXDimension arcHeight)
   throws TeXSyntaxException
   {
      float x = 0f;
      float y = 0f;

      Point2D pos = currentPath.getCurrentPoint();

      if (pos == null)
      {
         currentPath.moveTo(x, y);
      }
      else
      {
         x = (float)pos.getX();
         y = (float)pos.getY();
      }

      float x1 = toBp(endX);
      float y1 = toBp(endY);

      float arcW = toBp(arcWidth);
      float arcH = toBp(arcHeight);

      float width = x1-x;
      float height = y1-y;

      if (width < 0f)
      {
         x = x1;
         width = -width;
      }

      if (height < 0f)
      {
         y = y1;
         height = -height;
      }

      RoundRectangle2D.Float rect = 
         new RoundRectangle2D.Float(x, y, width, height, arcW, arcH);

      currentPath.append(rect.getPathIterator(null), true);

      currentPath.moveTo(toBp(endX), toBp(endY));
   }

   public void closePath()
   {
      currentPath.closePath();
   }

   private float toX(float value) throws TeXSyntaxException
   {
      return transformAttributes.toBpX(parser, value);
   }

   private float toY(float value) throws TeXSyntaxException
   {
      return transformAttributes.toBpY(parser, value);
   }

   private float toBp(TeXDimension dim) throws TeXSyntaxException
   {
      return dim.getUnit().fromUnit(parser, dim.getValue(), TeXUnit.BP)
           * dim.getValue();
   }

   public Object clone()
   {
      TeXGraphicsShape shape = new TeXGraphicsShape();

      shape.parser = parser;

      shape.strokeAttributes = 
        (TeXGraphicsStrokeAttributes)strokeAttributes.clone();
      shape.fillAttributes = 
        (TeXGraphicsFillAttributes)fillAttributes.clone();
      shape.textAttributes = 
        (TeXGraphicsTextAttributes)textAttributes.clone();
      shape.transformAttributes = 
        (TeXGraphicsTransformAttributes)transformAttributes.clone();

      shape.currentPath = new GeneralPath(currentPath);

      return shape;
   }

   public Rectangle2D computeBounds()
   {
      return createTransformedShape().getBounds2D();
   }

   public Rectangle2D computeStrokedBounds()
   {
      GeneralPath path = new GeneralPath();
      path.append(createTransformedShape().getBounds2D().getPathIterator(null, 0), false);

      Stroke stroke = strokeAttributes.getStroke();

      return stroke.createStrokedShape(path).getBounds2D();
   }

   public Shape createTransformedShape()
   {
      return transformAttributes.getTransform()
                  .createTransformedShape(currentPath);
   }

   public TeXGraphicsStrokeAttributes getStrokeAttributes()
   {
      return strokeAttributes;
   }

   public TeXGraphicsFillAttributes getFillAttributes()
   {
      return fillAttributes;
   }

   public TeXGraphicsTransformAttributes getTransformAttributes()
   {
      return transformAttributes;
   }

   public void setColor(Color col)
   {
      strokeAttributes.setLineColor(col);
      fillAttributes.setFillColor(col);
      textAttributes.setTextColor(col);
   }

   public TeXGraphicsTextAttributes getTextAttributes()
   {
      return textAttributes;
   }

   private GeneralPath currentPath = null;

   private TeXParser parser;

   private TeXGraphicsStrokeAttributes strokeAttributes;
   private TeXGraphicsFillAttributes fillAttributes;
   private TeXGraphicsTextAttributes textAttributes;
   private TeXGraphicsTransformAttributes transformAttributes;
}

