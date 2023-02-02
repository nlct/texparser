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
      this(name, BorderStyle.NONE, null, null);
   }

   public ParBox(String name, BorderStyle borderStyle, 
      TeXDimension borderWidth, TeXDimension innerMargin)
   {
      super(name, borderStyle, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true, true,
        borderWidth, innerMargin);
   }

   @Override
   public FrameBox createBox()
   {
      return new ParBox(getName());
   }

   public TeXDimension getBorderWidth(TeXParser parser) throws IOException
   {
      return currentBorderWidth;
   }

   public TeXDimension getInnerMargin(TeXParser parser) throws IOException
   {
      return currentInnerMargin;
   }

   protected AlignVStyle getAlignVStyle(TeXParser parser, String val)
    throws LaTeXSyntaxException
   {
      if (val.equals("c"))
      {
         return AlignVStyle.MIDDLE;
      }
      else if (val.equals("t"))
      {
         return AlignVStyle.TOP;
      }
      else if (val.equals("b"))
      {
         return AlignVStyle.BOTTOM;
      }
      else
      {
         throw new LaTeXSyntaxException(parser, LaTeXSyntaxException.ILLEGAL_ARG_TYPE, val);
      }
   }

   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String pos = popOptLabelString(parser, stack);

      if (pos != null)
      {
         valign = getAlignVStyle(parser, pos);

         TeXDimension height = popOptDimensionArg(parser, stack);

         if (height != null)
         {
            currentHeight = height;
         }
      }

      currentWidth = popDimensionArg(parser, stack);
   }
}
