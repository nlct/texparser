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

public class SetFrameContents extends ControlSequence
{
   public SetFrameContents(String name, FlowFrameType type, FlowFramSty sty)
   {
      this(name, type, false, sty);
   }

   public SetFrameContents(String name, FlowFrameType type,
     boolean append, FlowFramSty sty)
   {
      super(name);

      if (type == FlowFrameType.FLOW)
      {
         throw new IllegalArgumentException();
      }

      if (type != FlowFrameType.DYNAMIC && append)
      {
         throw new IllegalArgumentException();
      }

      this.type = type;
      this.sty = sty;
      this.append = append;
   }

   @Override
   public Object clone()
   {
      return new SetFrameContents(getName(), type, append, sty);
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
      boolean isStar = (popModifier(parser, stack, '*') == '*');
      FlowFrameData data;

      if (isStar)
      {
         String label = popLabelString(parser, stack);
         data = sty.getFrame(type, label);
      }
      else
      {
         int id = popInt(parser, stack);
         data = sty.getFrame(type, id);
      }

      TeXObject content = popArg(parser, stack);

      if (append && data.hasContent())
      {
         TeXObject oldContent = data.getContent();

         TeXObjectList list = TeXParserUtils.toList(oldContent, parser);
         list.add(content, true);
         content = list;
      }

      data.setContent(content);
   }

   FlowFramSty sty;
   FlowFrameType type;
   boolean append = false;
}
