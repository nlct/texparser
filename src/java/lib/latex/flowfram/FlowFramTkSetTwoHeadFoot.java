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
import com.dickimawbooks.texparserlib.latex.KeyValList;

public class FlowFramTkSetTwoHeadFoot extends ControlSequence
{
   public FlowFramTkSetTwoHeadFoot(FlowFramTkUtilsSty sty, 
      String name, boolean isHead)
   {
      super(name);
      this.sty = sty;
      this.isHead = isHead;
   }

   @Override
   public Object clone()
   {
      return new FlowFramTkSetTwoHeadFoot(sty, getName(), isHead);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String oddLabel = popLabelString(parser, stack);
      String evenLabel = popLabelString(parser, stack);

      if (isHead)
      {
         sty.setOddHeadLabel(oddLabel);
         sty.setEvenHeadLabel(evenLabel);
      }
      else
      {
         sty.setOddFootLabel(oddLabel);
         sty.setEvenFootLabel(evenLabel);
      }
   }

   FlowFramTkUtilsSty sty;
   boolean isHead;
}
