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

      substack.add(getFrameBox());

      TeXObject pos = popOptArg(parser, stack);
      TeXObject height = null;

      if (pos != null)
      {
         substack.add(parser.getListener().getOther('['));
         substack.add(pos);
         substack.add(parser.getListener().getOther(']'));

         height = popOptArg(parser, stack);

         if (height != null)
         {
            substack.add(parser.getListener().getOther('['));
            substack.add(height);
            substack.add(parser.getListener().getOther(']'));
         }
      }

      TeXDimension width = popDimensionArg(parser, stack);

      substack.add(width);

      TeXObjectList contents = popContents(parser, stack);
      Group grp = parser.getListener().createGroup();
      substack.add(grp);
      grp.add(contents);

      if (parser == stack || stack == null)
      {
         substack.process(parser);
      }
      else
      {
         substack.process(parser, stack);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected FrameBox fbox;
}
