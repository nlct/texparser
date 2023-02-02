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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsNoIdxDisplayLoc extends Command
{
   public GlsNoIdxDisplayLoc()
   {
      this("glsnoidxdisplayloc");
   }

   public GlsNoIdxDisplayLoc(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new GlsNoIdxDisplayLoc(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String prefix = popLabelString(parser, stack);
      String counter = popLabelString(parser, stack);
      String csname = popLabelString(parser, stack);

      TeXObject loc = popArg(parser, stack);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();
      TeXObjectList list = listener.createStack();

      DataObjectList noexpand = listener.createDataList(true);
      list.add(noexpand);

      noexpand.add(listener.getControlSequence("setentrycounter"));

      if (!prefix.isEmpty())
      {
         noexpand.add(listener.getOther('['));
         noexpand.add(listener.createString(prefix), true);
         noexpand.add(listener.getOther(']'));
      }

      noexpand.add(listener.createGroup(counter));

      list.add(listener.getControlSequence(csname));
      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(loc);

      return list;
   }

}
