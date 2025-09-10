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

import com.dickimawbooks.texparserlib.*;

public class NumColumnInArea extends ControlSequence
{
   public NumColumnInArea(String name, int n, boolean hasAreaArgs, FlowFramSty sty)
   {
      super(name);
      this.sty = sty;
      this.numColumns = n;
      this.hasAreaArgs = hasAreaArgs;
   }

   @Override
   public Object clone()
   {
      return new NumColumnInArea(getName(), numColumns, hasAreaArgs, sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      String pages = popOptLabelString(parser, stack);
      int n = numColumns;

      if (numColumns == 0)
      {
         n = TeXParserUtils.popInt(parser, stack, true);
      }

      FlowFrameData[] frames = new FlowFrameData[n];
      TeXDimension width=null, height=null, posX=null, posY=null;

      if (!hasAreaArgs)
      {
         width = parser.getDimenRegister("typeblockwidth");
         height = parser.getDimenRegister("typeblockheight");
         posX = new UserDimension();
         posY = new UserDimension();
      }

      for (int i = 0; i < n; i++)
      {
         if (hasAreaArgs)
         {
            width = popDimensionArg(parser, stack, true);
            height = popDimensionArg(parser, stack, true);
            posX = popDimensionArg(parser, stack, true);
            posY = popDimensionArg(parser, stack, true);
         }

         frames[i] = sty.newFlowFrame(null, false, width, height, posX, posY);

         if (pages != null)
         {
            frames[i].setPageList(pages);
         }
      }

      String labels = popOptLabelString(parser, stack);

      String[] split = labels.trim().split(" *, *");

      for (int i = n-1, j = split.length-1;
           i >= 0 && j >= 0; i--, j--)
      {
         if (!split[j].isEmpty())
         {
            frames[i].setLabel(split[j]);
         }
      }
   }

   FlowFramSty sty;
   int numColumns;
   boolean hasAreaArgs;
}
