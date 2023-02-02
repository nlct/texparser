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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HNewFontFamily extends ControlSequence 
{
   public L2HNewFontFamily()
   {
      this("newfontfamily");
   }

   public L2HNewFontFamily(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HNewFontFamily(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      // don't implement just define the command to do nothing

      ControlSequence cs = popControlSequence(parser, stack);
      popOptArg(parser, stack);
      popArg(parser, stack);

      parser.putControlSequence(true, new GenericCommand(true, cs.getName()));
   }
}
