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

public class FlowFramTkSetOneHeadFoot extends ControlSequence
{
   public FlowFramTkSetOneHeadFoot(FlowFramTkUtilsSty sty, 
      String name, boolean isOdd, boolean isHead)
   {
      super(name);
      this.sty = sty;
      this.isOdd = isOdd;
      this.isHead = isHead;
   }

   @Override
   public Object clone()
   {
      return new FlowFramTkSetOneHeadFoot(sty, getName(), isOdd, isHead);
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
      String label = popLabelString(parser, stack);

      if (isOdd)
      {
         if (isHead)
         {
            sty.setOddHeadLabel(label);
         }
         else
         {
            sty.setOddFootLabel(label);
         }
      }
      else
      {
         if (isHead)
         {
            sty.setEvenHeadLabel(label);
         }
         else
         {
            sty.setEvenFootLabel(label);
         }
      }
   }

   FlowFramTkUtilsSty sty;
   boolean isOdd, isHead;
}
