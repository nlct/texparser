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

public class TwoToneTop extends ControlSequence
{
   public TwoToneTop(FlowFramSty sty)
   {
      this("vtwotonetop", true, sty);
   }

   public TwoToneTop(String name, boolean isVertical, FlowFramSty sty)
   {
      super(name);
      this.vertical = isVertical;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new TwoToneTop(getName(), vertical, sty);
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

      TeXDimension offset = popOptDimensionArg(parser, stack);

      if (offset == null)
      {
         offset = new UserDimension();
      }

      TeXDimension length = popDimensionArg(parser, stack, true);

      TeXDimension dim1 = popDimensionArg(parser, stack, true);
      TeXObject backCol1 = popArg(parser, stack);
      String label1 = popLabelString(parser, stack);

      TeXDimension dim2 = popDimensionArg(parser, stack, true);
      TeXObject backCol2 = popArg(parser, stack);
      String label2 = popLabelString(parser, stack);

      FlowFrameData frame1, frame2;

      UserDimension offset2;

      if (vertical)
      {
         DimenRegister reg = parser.getDimenRegister("paperheight");

         UserDimension pos = new UserDimension(
           reg.getUnit().toPt(parser, reg.getValue())
          -length.getUnit().toPt(parser, length.getValue()),
           TeXUnit.PT);

         frame1 = sty.newStaticFrame(label1, 
                  false, dim1, length, offset, pos);

         offset2 = new UserDimension(
           offset.getValue()
             + dim1.getUnit().toUnit(parser, dim1.getValue(), offset.getUnit()),
           offset.getUnit());

         frame2 = sty.newStaticFrame(label2, 
               false, dim2, length, offset2, new UserDimension());
      }
      else
      {
         DimenRegister reg = parser.getDimenRegister("paperwidth");

         UserDimension pos = new UserDimension(
           reg.getUnit().toPt(parser, reg.getValue())
          -length.getUnit().toPt(parser, length.getValue()),
           TeXUnit.PT);

         frame1 = sty.newStaticFrame(label1, 
                  false, length, dim1, new UserDimension(), offset);

         offset2 = new UserDimension(
           offset.getValue()
             + dim1.getUnit().toUnit(parser, dim1.getValue(), offset.getUnit()),
           offset.getUnit());


         frame2 = sty.newStaticFrame(label2, 
                  false, length, dim2, pos, offset2);
      }

      frame1.setBackColor(sty.getColor(backCol1));
      frame2.setBackColor(sty.getColor(backCol2));

      if (pages != null)
      {
         frame1.setPageList(pages);
         frame2.setPageList(pages);
      }
   }

   FlowFramSty sty;
   boolean vertical = true;
}
