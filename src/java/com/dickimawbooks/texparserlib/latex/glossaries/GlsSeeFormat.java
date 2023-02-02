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

public class GlsSeeFormat extends Command
{
   public GlsSeeFormat()
   {
      this("glsseeformat");
   }

   public GlsSeeFormat(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsSeeFormat(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject tag = popOptArg(parser, stack);

      if (tag == null)
      {
         tag = parser.getListener().getControlSequence("seename");
      }

      TeXObject labelList = popArg(parser, stack);
      popArg(parser, stack);// ignore

      TeXObjectList expanded = parser.getListener().createStack();

      expanded.add(parser.getListener().getControlSequence("emph"));

      expanded.add(TeXParserUtils.createGroup(parser, tag));

      expanded.add(parser.getListener().getSpace());

      expanded.add(parser.getListener().getControlSequence("glsseelist"));

      expanded.add(TeXParserUtils.createGroup(parser, labelList));

      return expanded;
   }

}
