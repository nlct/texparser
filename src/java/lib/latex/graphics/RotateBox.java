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
package com.dickimawbooks.texparserlib.latex.graphics;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class RotateBox extends ControlSequence
{
   public RotateBox()
   {
      this("rotatebox");
   }

   public RotateBox(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new RotateBox(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject options = null;

      if (parser == stack)
      {
         options = parser.popNextArg('[', ']');
      }
      else
      {
         options = stack.popArg(parser, '[', ']');
      }

      KeyValList keyValList = null;

      if (options != null)
      {
         keyValList = KeyValList.getList(parser, options);
      }

      TeXObject angle = (stack == parser ? parser.popNextArg()
        : stack.popArg(parser));

      if (angle instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)angle).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)angle).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            angle = expanded;
         }
      }

      double angleVal = GraphicsSty.getDouble(angle, parser);
      TeXObject origin = null;
      TeXDimension x=null;
      TeXDimension y=null;

      if (keyValList != null)
      {
         TeXObject units = keyValList.get("units");

         if (units != null)
         {
            angleVal = angleVal * 360.0/GraphicsSty.getDouble(units, parser);
         }

         origin = keyValList.get("origin");

         TeXObject originX = keyValList.get("x");

         if (originX != null)
         {
            x = GraphicsSty.getDimension(originX, parser);
         }

         TeXObject originY = keyValList.get("y");

         if (originY != null)
         {
            y = GraphicsSty.getDimension(originY, parser);
         }
      }

      TeXObject object = (stack == parser ? parser.popNextArg()
        : stack.popArg(parser));

      if (object instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)object).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)object).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            object = expanded;
         }
      }

      if (origin != null)
      {
         double orgX = 0;
         double orgY = 0;

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser);

            if (expanded != null)
            {
               object = expanded;
            }
         }

         String str = object.toString(parser);

         if (str.contains("r"))
         {
            orgX=100;
         }
         else if (str.contains("c"))
         {
            orgX=50;
         }

         if (str.contains("t"))
         {
            orgY=100;
         }
         // don't know the baseline (font unknown)

         listener.rotate(angleVal, orgX, orgY, parser, stack, object);
      }
      else if (x != null || y != null)
      {
         listener.rotate(angleVal, x, y, parser, stack, object);
      }
      else
      {
         listener.rotate(angleVal, parser, stack, object);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
