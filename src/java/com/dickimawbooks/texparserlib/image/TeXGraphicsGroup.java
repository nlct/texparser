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

import java.util.Vector;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;

public class TeXGraphicsGroup extends Vector<TeXGraphicsObject>
  implements TeXGraphicsObject
{
   public TeXGraphicsGroup(TeXParser parser)
   {
      super();

      strokeAttributes = new TeXGraphicsStrokeAttributes();
      fillAttributes = new TeXGraphicsFillAttributes();
      transformAttributes = new TeXGraphicsTransformAttributes();
      textAttributes = new TeXGraphicsTextAttributes();

      this.parser = parser;
   }

   public TeXGraphicsGroup(TeXGraphicsGroup parent)
   {
      super();

      strokeAttributes = 
         (TeXGraphicsStrokeAttributes)parent.strokeAttributes.clone();
      fillAttributes = 
         (TeXGraphicsFillAttributes)parent.fillAttributes.clone();
      textAttributes = 
         (TeXGraphicsTextAttributes)parent.textAttributes.clone();
      transformAttributes = 
         (TeXGraphicsTransformAttributes)parent.transformAttributes.clone();

      this.parent = parent;
      this.parser = parent.parser;
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

   public TeXGraphicsTextAttributes getTextAttributes()
   {
      return textAttributes;
   }

   public void newPath()
   {
      currentPath = new TeXGraphicsShape(this);
   }

   public void strokePath() throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      add(new TeXGraphicsStrokedPath(currentPath));

      currentPath = null;
   }

   public void fillPath() throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      add(new TeXGraphicsFilledPath(currentPath));

      currentPath = null;
   }

   public void strokeFillPath() throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      add(new TeXGraphicsStrokedFilledPath(currentPath));

      currentPath = null;
   }

   public void clip() throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      add(new TeXGraphicsClippingPath(currentPath));

      currentPath = null;
   }

   public void noOpPath() throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      add(new TeXGraphicsNoOpPath(currentPath));

      currentPath = null;
   }

   public Rectangle2D computeBounds()
   {
      if (isEmpty())
      {
         return null;
      }

      TeXGraphicsObject object = firstElement();

      Rectangle2D bounds = object.computeBounds();

      for (int i = 1; i < size(); i++)
      {
         bounds.add(object.computeBounds());
      }

      return bounds;
   }

   public void setCurrentColor(Color col)
    throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      currentPath.setColor(col);
   }

   public void setCurrentLineColor(Color col)
    throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      currentPath.getStrokeAttributes().setLineColor(col);
   }

   public void setCurrentFillColor(Color col)
    throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      currentPath.getFillAttributes().setFillColor(col);
   }

   public void setCurrentTextColor(Color col)
    throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      currentPath.getTextAttributes().setTextColor(col);
   }

   public TeXGraphicsStrokeAttributes getCurrentStrokeAttributes()
     throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      return currentPath.getStrokeAttributes();
   }

   public TeXGraphicsFillAttributes getCurrentFillAttributes()
     throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      return currentPath.getFillAttributes();
   }

   public TeXGraphicsTextAttributes getCurrentTextAttributes()
     throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      return currentPath.getTextAttributes();
   }

   public TeXGraphicsTransformAttributes getCurrentTransformAttributes()
     throws TeXGraphicsException
   {
      if (currentPath == null)
      {
         throw new TeXGraphicsException(parser, 
           TeXGraphicsException.ERROR_NO_PATH);
      }

      return currentPath.getTransformAttributes();
   }

   public void setColor(Color col)
   {
      fillAttributes.setFillColor(col);
      strokeAttributes.setLineColor(col);
      textAttributes.setTextColor(col);
   }

   public void setLineColor(Color col)
   {
      strokeAttributes.setLineColor(col);
   }

   public void setFillColor(Color col)
   {
      fillAttributes.setFillColor(col);
   }

   public void setTextColor(Color col)
   {
      textAttributes.setTextColor(col);
   }

   public TeXParser getParser()
   {
      return parser;
   }

   private TeXGraphicsStrokeAttributes strokeAttributes;
   private TeXGraphicsFillAttributes fillAttributes;
   private TeXGraphicsTextAttributes textAttributes;
   private TeXGraphicsTransformAttributes transformAttributes;

   private TeXGraphicsShape currentPath = null;

   private TeXGraphicsGroup parent = null;

   private TeXParser parser;

}

