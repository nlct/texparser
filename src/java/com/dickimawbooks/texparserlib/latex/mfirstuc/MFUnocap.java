/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.mfirstuc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class MFUnocap extends ControlSequence
{
   public MFUnocap(MfirstucSty sty)
   {
      this(sty, false);
   }

   public MFUnocap(MfirstucSty sty, boolean isGlobal)
   {
      this(sty, "MFUnocap", isGlobal);
   }

   public MFUnocap(MfirstucSty sty, String name, boolean isGlobal)
   {
      super(name);

      this.sty = sty;
      this.isGlobal = isGlobal;
   }

   public Object clone()
   {
      return new MFUnocap(sty, getName(), isGlobal);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      sty.addException(arg, isGlobal);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();

      sty.addException(arg, isGlobal);
   }

   private boolean isGlobal;
   private MfirstucSty sty;
}
