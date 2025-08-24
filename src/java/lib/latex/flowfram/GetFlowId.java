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

public class GetFlowId extends ControlSequence
{
   public GetFlowId(FlowFramSty sty)
   {
      this("getflowid", FlowFrameType.FLOW, sty);
   }

   public GetFlowId(String name, FlowFrameType type, FlowFramSty sty)
   {
      super(name);
      this.type = type;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new GetFlowId(getName(), type, sty);
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
      ControlSequence cs = popControlSequence(parser, stack);
      String csname = cs.getName();
      String label = popLabelString(parser, stack);

      int id;

      switch (type)
      {
         case STATIC:
            id = sty.getStaticFrameId(label);
         break;
         case DYNAMIC:
            id = sty.getDynamicFrameId(label);
         break;
         default:
            id = sty.getFlowFrameId(label);
      }

      parser.putControlSequence(true,
        new GenericCommand(csname, null, new UserNumber(id)));
   }

   FlowFramSty sty;
   FlowFrameType type;
}
