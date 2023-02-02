/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class MiniPage extends FrameBoxEnv
{
   public MiniPage(FrameBox parbox)
   {
      this("minipage", parbox);
   }

   public MiniPage(String name, FrameBox parbox)
   {
      super(name, parbox);
   }

   @Override
   public Object clone()
   {
      return new MiniPage(getName(), getFrameBox());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList substack = parser.getListener().createStack();

      currentParBox = (ParBox)fbox.clone();

      String pos = popOptLabelString(parser, stack);

      if (pos != null)
      {
         currentParBox.setVAlign(currentParBox.getAlignVStyle(parser, pos));

         TeXDimension height = popOptDimensionArg(parser, stack);

         if (height != null)
         {
            currentParBox.setHeight(height);
         }
      }

      TeXDimension width = popDimensionArg(parser, stack);

      currentParBox.setWidth(width);

      StartFrameBox obj = new StartFrameBox(currentParBox);

      if (parser == stack)
      {
         obj.process(parser);
      }
      else
      {
         obj.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      EndFrameBox obj = new EndFrameBox(currentParBox);
      obj.process(parser, stack);

      currentParBox = null;
   }

   public ParBox getCurrentParBox()
   {
      return currentParBox;
   }

   protected ParBox currentParBox;
}
