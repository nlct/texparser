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

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Color;

public class TeXGraphicsStrokeAttributes
{
   public TeXGraphicsStrokeAttributes()
   {
   }

   public float getPenWidth()
   {
      return penWidth;
   }

   public void setPenWidth(float width)
   {
      penWidth = width;
   }

   public int getCap()
   {
      return cap;
   }

   public void setCap(int capValue)
   {
      cap = capValue;
   }

   public int getJoin()
   {
      return join;
   }

   public void setJoin(int joinValue)
   {
      join = joinValue;
   }

   public float getMitreLimit()
   {
      return mitreLimit;
   }

   public void setMitreLimit(int limit)
   {
      mitreLimit = limit;
   }

   public void setDash(float[] dashValues, float dashPhase)
   {
      dash = dashValues;
      dash_phase = dashPhase;
   }

   public void setDash(float[] dashValues)
   {
      dash = dashValues;
   }

   public void setDashPhase(float phase)
   {
      dash_phase = phase;
   }

   public float[] getDash()
   {
      return dash;
   }

   public float getDashPhase()
   {
      return dash_phase;
   }

   public Stroke getStroke()
   {
      return new BasicStroke(penWidth, cap, join, mitreLimit, dash, dash_phase);
   }

   public void setLineColor(Color col)
   {
      lineCol = col;
   }

   public Color getLineColor()
   {
      return lineCol;
   }

   public Object clone()
   {
      TeXGraphicsStrokeAttributes attr = new TeXGraphicsStrokeAttributes();

      attr.penWidth = penWidth;
      attr.cap = cap;
      attr.join = join;
      attr.mitreLimit = mitreLimit;
      attr.dash_phase = dash_phase;

      if (dash == null)
      {
         attr.dash = null;
      }
      else
      {
         attr.dash = new float[dash.length];

         for (int i = 0; i < dash.length; i++)
         {
            attr.dash[i] = dash[i];
         }
      }

      attr.lineCol = lineCol;

      return attr;
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;

      if (obj == this) return true;

      if (!(obj instanceof TeXGraphicsStrokeAttributes)) return false;

      TeXGraphicsStrokeAttributes attr = (TeXGraphicsStrokeAttributes)obj;

      if (!lineCol.equals(attr.lineCol)) return false;

      if (penWidth != attr.penWidth) return false;

      if (cap != attr.cap) return false;

      if (join != attr.join) return false;

      if (mitreLimit != attr.mitreLimit) return false;

      if (dash_phase != attr.dash_phase) return false;

      if (dash == null && attr.dash != null) return false;

      if (dash != null && attr.dash == null) return false;

      if (dash != null && attr.dash != null)
      {
         if (dash.length != attr.dash.length) return false;

         for (int i = 0; i < dash.length; i++)
         {
            if (dash[i] != attr.dash[i]) return false;
         }
      }

      return lineCol.equals(attr.lineCol);
   }

   private float penWidth=1.0f;
   private int cap = BasicStroke.CAP_SQUARE;
   private int join = BasicStroke.JOIN_MITER;
   private float mitreLimit = 10.0f;
   private float[] dash = null;
   private float dash_phase = 0.0f;

   private Color lineCol = Color.BLACK;
}

