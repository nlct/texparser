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

public class GetFlowLabel extends Command
{
   public GetFlowLabel(FlowFramSty sty)
   {
      this("getflowlabel", FlowFrameType.FLOW, sty);
   }

   public GetFlowLabel(String name, FlowFrameType type, FlowFramSty sty)
   {
      super(name);
      this.type = type;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new GetFlowLabel(getName(), type, sty);
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int id = popInt(parser, stack);

      FlowFrameData data;

      switch (type)
      {
         case STATIC:
            data = sty.getStaticFrame(id);
         break;
         case DYNAMIC:
            data = sty.getDynamicFrame(id);
         break;
         default:
            data = sty.getFlowFrame(id);
      }

      if (data == null)
      {
         return parser.getListener().createStack();
      }
      else
      {
         return parser.getListener().createString(data.getLabel());
      }
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }


   FlowFramSty sty;
   FlowFrameType type;
}
