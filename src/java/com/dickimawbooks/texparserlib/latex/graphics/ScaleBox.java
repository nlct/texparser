/*
    Copyright (C) 2017 Nicola L.C. Talbot
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

public class ScaleBox extends ControlSequence
{
   public ScaleBox()
   {
      this("scalebox");
   }

   public ScaleBox(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new ScaleBox(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject hscale = (stack == parser ? parser.popNextArg()
        : stack.popArg(parser));

      if (hscale instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)hscale).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)hscale).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            hscale = expanded;
         }
      }

      double hscaleVal = GraphicsSty.getDouble(hscale, parser);
      double vscaleVal = hscaleVal;

      TeXObject option = null;

      if (parser == stack)
      {
         option = parser.popNextArg('[', ']');
      }
      else
      {
         option = stack.popArg(parser, '[', ']');
      }

      if (option instanceof Expandable)
      {
         TeXObjectList expanded = null;

         if (stack == parser)
         {
            expanded = ((Expandable)option).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)option).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            option = expanded;
         }
      }

      if (option != null)
      {
         vscaleVal = GraphicsSty.getDouble(option, parser);
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

      listener.scale(hscaleVal, vscaleVal, parser, stack, object);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
