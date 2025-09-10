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

public class NewFlowFrame extends ControlSequence
{
   public NewFlowFrame(FlowFramSty sty)
   {
      this("newflowframe", FlowFrameType.FLOW, sty);
   }

   public NewFlowFrame(String name, FlowFrameType type, FlowFramSty sty)
   {
      super(name);
      this.type = type;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new NewFlowFrame(getName(), type, sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      boolean bordered = (popModifier(parser, stack, '*') == '*');
      String pages = popOptLabelString(parser, stack);

      TeXDimension width = popDimensionArg(parser, stack, true);
      TeXDimension height = popDimensionArg(parser, stack, true);
      TeXDimension posX = popDimensionArg(parser, stack, true);
      TeXDimension posY = popDimensionArg(parser, stack, true);

      String label = popOptLabelString(parser, stack);

      FlowFrameData data;

      switch (type)
      {
         case STATIC:
            data = sty.newStaticFrame(label, 
               bordered, width, height, posX, posY);
         break;
         case DYNAMIC:
            data = sty.newDynamicFrame(label, 
               bordered, width, height, posX, posY);
         break;
         default:
            data = sty.newFlowFrame(label, 
               bordered, width, height, posX, posY);
      }

      if (pages != null)
      {
         data.setPageList(pages);
      }
   }

   FlowFramSty sty;
   FlowFrameType type;
}
