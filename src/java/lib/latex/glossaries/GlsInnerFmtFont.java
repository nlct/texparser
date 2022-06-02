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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsInnerFmtFont extends ControlSequence
{
   public GlsInnerFmtFont(String name)
   {
      this(name, name.contains("first"), name.contains("long"));
   }

   public GlsInnerFmtFont(String name, boolean isFirst, boolean isLong)
   {
      super(name);
      this.isFirst = isFirst;
      this.isLong = isLong;
   }

   @Override
   public Object clone()
   {
      return new GlsInnerFmtFont(getName(), isFirst, isLong);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = popArg(parser, stack);

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      String csname;

      if (isFirst)
      {
         csname = "glsfirst";
      }
      else
      {
         csname = "gls";
      }

      if (isLong)
      {
         csname += "longfont";
      }
      else
      {
         csname += "abbrvfont";
      }

      list.add(listener.getControlSequence(csname));

      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
      Group subgrp = listener.createGroup();
      grp.add(subgrp);

      subgrp.add(arg);

      if (parser == stack || stack == null)
      {
         list.process(parser);
      }
      else
      {
         list.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   protected boolean isFirst = false, isLong = false;
}
