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

public class ParBox extends FrameBox
{
   public ParBox()
   {
      this("parbox");
   }

   public ParBox(String name)
   {
      this(name, BORDER_NONE, null, null);
   }

   public ParBox(String name, byte borderStyle, 
      TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name, borderStyle, ALIGN_DEFAULT, ALIGN_DEFAULT, true, true,
        borderWidth, innerMargin);
   }

   public Object clone()
   {
      return new ParBox(getName(), getStyle(), 
        currentBorderWidth == null ? null :
          (TeXDimension)currentBorderWidth.clone(),
        currentInnerMargin == null ? null :
          (TeXDimension)currentInnerMargin.clone()
      );
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return currentBorderWidth;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return currentInnerMargin;
   }

   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject pos = null;
      TeXObject height = null;

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
               valign = ALIGN_CENTER;
            }
            else if (val.equals("t"))
            {
               valign = ALIGN_TOP;
            }
            else if (val.equals("b"))
            {
               valign = ALIGN_BOTTOM;
            }
            else
            {
               TeXApp texApp = parser.getListener().getTeXApp();

               texApp.warning(parser, texApp.getMessage(
                 LaTeXSyntaxException.ILLEGAL_ARG_TYPE, val));
            }
         }

         if (parser == stack)
         {
            height = parser.popNextArg('[', ']');
         }
         else
         {
            height = stack.popArg(parser, '[', ']');
         }

         if (height instanceof Expandable)
         {
            TeXObjectList expanded;

            if (parser == stack)
            {
               expanded = ((Expandable)height).expandfully(parser);
            }
            else
            {
               expanded = ((Expandable)height).expandfully(parser, stack);
            }

            if (expanded != null)
            {
               height = expanded;
            }
         }

         if (height instanceof TeXDimension)
         {
            currentHeight = (TeXDimension)height;
         }
         else if (height instanceof TeXObjectList)
         {
            currentHeight = ((TeXObjectList)height).popDimension(parser);
         }
         else if (height != null) 
         {
            throw new TeXSyntaxException(parser, 
             TeXSyntaxException.ERROR_DIMEN_EXPECTED);
         }
      }

      TeXObject width;

      if (parser == stack)
      {
         width = parser.popNextArg();
      }
      else
      {
         width = stack.popArg(parser);
      }

      if (width instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser != stack)
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

      if (width instanceof TeXDimension)
      {
         currentWidth = (TeXDimension)width;
      }
      else if (width instanceof TeXObjectList)
      {
         currentWidth = ((TeXObjectList)width).popDimension(parser);
      }
      else 
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_DIMEN_EXPECTED);
      }

   }
}
